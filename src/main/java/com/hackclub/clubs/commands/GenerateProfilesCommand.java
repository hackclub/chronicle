package com.hackclub.clubs.commands;

import com.hackclub.clubs.Main;
import com.hackclub.clubs.GlobalData;
import com.hackclub.clubs.engagements.Blot;
import com.hackclub.clubs.engagements.Onboard;
import com.hackclub.clubs.engagements.Sprig;
import com.hackclub.clubs.events.Angelhacks;
import com.hackclub.clubs.events.Assemble;
import com.hackclub.clubs.events.Outernet;
import com.hackclub.clubs.github.Github;
import com.hackclub.clubs.models.*;
import com.hackclub.clubs.models.event.AngelhacksRegistration;
import com.hackclub.clubs.slack.SlackUtils;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hackclub.common.Utils;
import com.hackclub.common.agg.DoubleAggregator;
import com.hackclub.common.agg.EntityDataExtractor;
import com.hackclub.common.agg.EntityProcessor;
import com.hackclub.common.conflation.MatchResult;
import com.hackclub.common.conflation.MatchScorer;
import com.hackclub.common.conflation.Matcher;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.hackclub.common.agg.Aggregator;
import com.hackclub.common.elasticsearch.ESCommand;
import com.hackclub.common.elasticsearch.ESUtils;
import com.hackclub.common.file.BlobStore;
import com.hackclub.common.geo.Geocoder;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hackclub.common.elasticsearch.ESUtils.clearIndex;

/**
 * Generates user profiles and syncs them to an elasticsearch cluster
 */
@CommandLine.Command(name = "genprofiles")
public class GenerateProfilesCommand extends ESCommand {
    private static final AtomicLong counter = new AtomicLong(0);
    private static volatile boolean running = true;

    @CommandLine.ParentCommand
    private Main mainCmd;

    @CommandLine.Parameters(index = "0", description = "URI to csv file with a mapping of all slack users")
    private URI inputUsersUri;

    @CommandLine.Parameters(index = "1", description = "URI to json file with a mapping of all staff slack users")
    private URI staffJsonUri;

    @CommandLine.Parameters(index = "2", description = "URI to directory containing slack data export(s)")
    private URI inputDirUri;

    @CommandLine.Parameters(index = "3", description = "URO to scrapbook account data csv file")
    private URI scrapbookAccountDataCsvUri;

    @CommandLine.Parameters(index = "4", description = "URI to potential leaders (club applications) csv file")
    private URI clubApplicationsCsvUri;

    @CommandLine.Parameters(index = "5", description = "URI to active clubs csv file")
    private URI activeClubsCsvUri;

    @CommandLine.Parameters(index = "6", description = "URI to pirate ship shipment csv file")
    private URI pirateshipShipmentCsvUri;

    @CommandLine.Parameters(index = "7", description = "URI to assemble registration csv file")
    private URI assembleRegistrationCsvUri;

    @CommandLine.Parameters(index = "8", description = "URI to outernet registration csv file")
    private URI outernetRegistrationCsvUri;

    @CommandLine.Parameters(index = "9", description = "URI to angelhacks registration csv file")
    private URI angelhacksRegistrationCsvUri;

    @CommandLine.Parameters(index = "10", description = "URI to blot engagements csv file")
    private URI blotEngagementsCsvUri;

    @CommandLine.Parameters(index = "11", description = "URI to sprig engagements csv file")
    private URI sprigEngagementsCsvUri;

    @CommandLine.Parameters(index = "12", description = "URI to onboard engagements csv file")
    private URI onboardEngagementsCsvUri;

    @Override
    public Integer call() throws Exception {
        System.out.println("Initializing geocoder");
        Geocoder.initialize(googleGeocodingApiKey);
        System.out.printf("Initializing elasticsearch client (host: %s port: %d)%n", esHostname, esPort);
        ElasticsearchClient esClient = ESUtils.createElasticsearchClient(esHostname, esPort, esUsername, esPassword, esFingerprint);

        System.out.printf("Clearing the \"%s\" index%n", esIndex);
        clearIndex(esClient, esIndex);

        final long startTimeMs = System.currentTimeMillis();
        Thread monitoringThread = startMonitoringThread();

        GlobalData.staffUserIds = loadStaffUsers(BlobStore.load(staffJsonUri));
        GlobalData.allUsers = loadUsers(BlobStore.load(inputUsersUri));

        System.out.println("AGGREGATION PHASE");
        aggregate();

        System.out.printf("Ignored user accounts: %s", String.join(", ", ChannelEvent.ignoredAccounts.keySet()));

        System.out.println("CONFLATION PHASE");
        conflate();
        System.out.println("POST-PROCESSING PHASE");
        postProcess();
        System.out.println("UPLOAD PHASE");
        writeUsersToES(esClient);

        final long totalTimeMs = System.currentTimeMillis() - startTimeMs;
        System.out.printf("Total time in seconds: %.02f\n", totalTimeMs / 1000.0f);
        running = false;
        monitoringThread.join();
        System.out.println("Monitoring thread complete!");
        Geocoder.shutdown();
        return 0;
    }

    private static void postProcess() {
        GlobalData.allUsers.forEach((userId, hackClubUser) -> hackClubUser.finish());
    }

    private Map<String, OperationsInfo> loadOperationsInfo() {
        return Collections.emptyMap();
    }

    private Stream<ChannelDay> aggregate() throws Exception {
        Stream<ChannelDay> days = getDayStream(BlobStore.load(inputDirUri));
        Stream<ChannelEvent> dayEntries = days
                //.limit(500)
                //.filter(day -> day.getLocalDate().isAfter(LocalDate.now().minusMonths(12)))
                .flatMap(day -> day.getEntries(false))
                .filter(de -> de.getUser() != null)
                .peek(ChannelEvent::tokenize)
                .peek(ChannelEvent::onComplete);

        EntityDataExtractor<ChannelDay, Double> channelMessageCountWithStaffExtractor = entity -> (double) entity.getEntries(false).count();
        EntityDataExtractor<ChannelDay, Double> uniqueUsersCountWithStaffExtractor = entity -> (double)entity.getEntries(false)
                .map(ChannelEvent::getUser).distinct().count();
        EntityDataExtractor<ChannelDay, Double> uniqueUsersCountWithoutStaffExtractor = entity -> (double) entity.getEntries(true)
                .map(ChannelEvent::getUser).distinct().count();
        EntityDataExtractor<ChannelEvent, Map<String, Integer>> keywordsExtractor = ChannelEvent::getTokenOccurrences;

        EntityProcessor<ChannelDay, Double> channelMessageCountsWithStaffByDay = new EntityProcessor<>(new DoubleAggregator<>(day -> String.format("%d_%d_%d_%s", day.getYear(), day.getMonth(), day.getDay(), day.getChannelName())), channelMessageCountWithStaffExtractor);
        EntityProcessor<ChannelDay, Double> channelMessageCountsWithStaffByMonth = new EntityProcessor<>(new DoubleAggregator<>(day -> String.format("%d_%d_%s", day.getYear(), day.getMonth(), day.getChannelName())), channelMessageCountWithStaffExtractor);
        EntityProcessor<ChannelDay, Double> channelMessageCountsWithStaffByYear = new EntityProcessor<>(new DoubleAggregator<>(day -> String.format("%d_%s", day.getYear(), day.getChannelName())), channelMessageCountWithStaffExtractor);
        EntityProcessor<ChannelDay, Double> channelMessageCountsWithStaffByAlltime = new EntityProcessor<>(new DoubleAggregator<>(day -> String.format("%s", day.getChannelName())), channelMessageCountWithStaffExtractor);
        EntityProcessor<ChannelDay, Double> channelUniqueUserCountsByDay = new EntityProcessor<>(new DoubleAggregator<>(day -> String.format("%d_%d_%d_%s", day.getYear(), day.getMonth(), day.getDay(), day.getChannelName())), uniqueUsersCountWithStaffExtractor);
        EntityProcessor<ChannelDay, Double> channelUniqueUserCountsNonStaffByDay = new EntityProcessor<>(new DoubleAggregator<>(day -> String.format("%d_%d_%d_%s", day.getYear(), day.getMonth(), day.getDay(), day.getChannelName())), uniqueUsersCountWithoutStaffExtractor);
        EntityProcessor<ChannelDay, Double> channelUniqueUserCountsNonStaffByMonth = new EntityProcessor<>(new DoubleAggregator<>(day -> String.format("%d_%d_%s", day.getYear(), day.getMonth(), day.getChannelName())), uniqueUsersCountWithoutStaffExtractor);
        EntityProcessor<ChannelEvent, Map<String, Integer>> userKeywordAssociations = new EntityProcessor<>(keywordAggregator, keywordsExtractor);

        // Message counts
//        days = channelMessageCountsWithStaffByDay.process(days);
//        days = channelMessageCountsWithStaffByMonth.process(days);
//        days = channelMessageCountsWithStaffByYear.process(days);
//        days = channelMessageCountsWithStaffByAlltime.process(days);

        // Unique user counts
//        days = channelUniqueUserCountsByDay.process(days);
//        days = channelUniqueUserCountsNonStaffByDay.process(days);
//        days = channelUniqueUserCountsNonStaffByMonth.process(days);

        dayEntries = userKeywordAssociations.process(dayEntries);
        System.out.printf("Finished keyword pipeline\n", dayEntries.count());

        return days;
    }

    private ArrayList<PirateShipEntry> loadShipmentInfo() throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new FileReader(BlobStore.load(pirateshipShipmentCsvUri)));
        String [] nextLine;

        String[] columns = "Created Date,Recipient,Company,Email,Tracking Number,Cost,Status,Batch,Label Size,Saved Package,Ship From,Ship Date,Estimated Delivery Time,Weight (oz),Zone,Package Type,Package Length,Package Width,Package Height,Tracking Status,Tracking Info,Tracking Date,Address Line 1,Address Line 2,City,State,Zipcode,Country,Carrier,Service,Order ID,Rubber Stamp 1,Rubber Stamp 2,Rubber Stamp 3,Order Value".split(",");
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);

        ArrayList<PirateShipEntry> pirateShipEntries = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null)
        {
            pirateShipEntries.add(PirateShipEntry.fromCsv(nextLine, columnIndices));
        }
        return pirateShipEntries;
    }

    public Stream<ChannelDay> getDayStream(File dir) throws Exception {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new RuntimeException("Whoa - no files there");
        }

        return getAllAbsoluteFilePathsInDirectory(new ArrayList<>(), files)
                .parallel()
                .flatMap(this::processDayFile);
    }

    private static Thread startMonitoringThread() {
        Thread t = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(1000);
                    //System.out.println("Number: " + counter.get());
                } catch (InterruptedException e) {
                }
            }
        });
        t.start();

        return t;
    }

    public static Stream<String> getAllAbsoluteFilePathsInDirectory(ArrayList<String> paths, File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                getAllAbsoluteFilePathsInDirectory(paths, file.listFiles()); // Calls same method again.
            } else {
                paths.add(file.getAbsolutePath());
            }
        }
        return paths.stream();
    }

    private Stream<ChannelDay> processDayFile(String filePath) {
        Reader reader = null;
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            reader = Files.newBufferedReader(Paths.get(filePath));

            // convert a JSON string to a clubs.pojo.User object
            List<ChannelEvent> entries = gson.fromJson(reader, new TypeToken<List<ChannelEvent>>() {
            }.getType());
            counter.addAndGet(entries.size());
            return Stream.of(new ChannelDay(filePath, entries.toArray(new ChannelEvent[0])));
        } catch (Throwable t) {
            return Stream.empty();
        } finally {
            // close reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private HashMap<String, HackClubUser> loadUsers(File userCsv) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new FileReader(userCsv));
        String [] nextLine;

        HashMap<String, HackClubUser> allUsers = new HashMap<>();
        while ((nextLine = reader.readNext()) != null)
        {
            HackClubUser hackClubUser = HackClubUser.fromCsv(nextLine);
            hackClubUser.setStaff(GlobalData.staffUserIds.contains(hackClubUser.getSlackUserId()));
            allUsers.put(hackClubUser.getSlackUserId(), hackClubUser);
        }
        return allUsers;
    }

    private HashMap<String, ScrapbookAccount> loadScrapbookAccounts() throws IOException, CsvValidationException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(scrapbookAccountDataCsvUri)))
                .withSkipLines(1)
                .build();

        String[] columns = "lastusernameupdatedtime,github,website,timezone,displaystreak,avatar,webhookurl,customdomain,newmember,fullslackmember,cssurl,timezoneoffset,slackid,pronouns,streakcount,maxstreaks,customaudiourl,streakstoggledoff,webring,email,username,_airbyte_ab_id,_airbyte_emitted_at,_airbyte_normalized_at,_airbyte_synced_scrapbook_accounts_hashid".split(",");
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);

        String [] nextLine;

        HashMap<String, ScrapbookAccount> allScrapbookAccounts = new HashMap<>();
        while ((nextLine = reader.readNext()) != null)
        {
            ScrapbookAccount account = ScrapbookAccount.fromCsv(nextLine, columnIndices);
            if (account.getSlackId().length() > 0)
                allScrapbookAccounts.put(account.getSlackId(), account);
        }
        return allScrapbookAccounts;
    }

    private HashMap<String, ClubLeaderApplicationInfo> loadClubLeaderApplicationInfoByEmail() throws IOException, CsvValidationException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(clubApplicationsCsvUri)))
                .withSkipLines(1)
                .build();

        String[] columns = "ID,Application,Email,Logins,Application ID,Log In Path,Completed,Full Name,Birthday,School Year,Code,Phone,Address,Address Line 1,Address Line 2,Address City,Address State,Address Zip,Address Country,Address Formatted,Gender,Ethnicity,Website,Twitter,GitHub,Other,Hacker Story,Achievement,Technicality,Accepted Tokens,New Fact,Clubs Dashboard,Birth Year,Turnover ID,Turnover Invite?,Turnover".split(",");
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);

        String [] nextLine;

        HashMap<String, ClubLeaderApplicationInfo> allLeaderApplications = new HashMap<>();
        while ((nextLine = reader.readNext()) != null)
        {
            ClubLeaderApplicationInfo leader = ClubLeaderApplicationInfo.fromCsv(nextLine, columnIndices);
            if (leader.getEmail().length() > 0)
                allLeaderApplications.put(leader.getEmail(), leader);
        }
        return allLeaderApplications;
    }

    private void conflate() throws CsvValidationException, IOException {
        System.out.println("Loading slack keyword counts...");
        Map<String, Map<String, Integer>> userKeywordCounts = Collections.emptyMap();
        System.out.println("Loading scrapbook data...");
        Map<String, ScrapbookAccount> userScrapbookData = loadScrapbookAccounts();
        System.out.println("Loading clubs data...");
        Map<HackClubUser, ClubInfo> userClubInfo = loadAllUsersClubInfo();
        System.out.println("Loading clubs application data...");
        Map<String, ClubLeaderApplicationInfo> userClubLeaderApplicationInfo = loadClubLeaderApplicationInfoByEmail();

        System.out.println("Conflating keywords, scrapbook, and leader data...");
        GlobalData.allUsers.forEach((userId, hackClubUser) -> {
            hackClubUser.setKeywords(userKeywordCounts.getOrDefault(userId, Collections.emptyMap()));
            hackClubUser.setScrapbookAccount(Optional.ofNullable(userScrapbookData.getOrDefault(userId, null)));
            hackClubUser.setLeaderInfo(Optional.ofNullable(userClubInfo.get(hackClubUser)), Optional.ofNullable(userClubLeaderApplicationInfo.getOrDefault(hackClubUser.getEmail(), null)));
        });

        System.out.println("Conflating event data...");
        conflateEventData();
        System.out.println("Conflating engagement data...");
        conflateEngagementData();
        System.out.println("Conflating slack data...");
        conflateSlackData();
        System.out.println("Conflating github data...");
        conflateGithubData();
    }

    private Map<HackClubUser, ClubInfo> loadAllUsersClubInfo() throws IOException, CsvValidationException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(activeClubsCsvUri)))
                .withSkipLines(1)
                .build();

        String[] columns = "Venue,Application Link,Current Leader(s),Current Leaders' Emails,Notes,Status,Location,Slack ID,Leader Address,Address Line 1,Address Line 2,Address City,Address State,Address Zip,Address Country,Address Formatted,Last Check-In,Tier,T1-Engaged-Super,T1-Engaged,T1-Super,T1,On Bank,Latitude,Longitude,Last Outreach,Next check-In,Ambassador,Club Leaders,Prospective Leaders,Email (from Prospective Leaders),Full Name (from Prospective Leaders),Phone (from Prospective Leaders),Current Leaders' Phones,Leader Phone,Leader-Club Join,Leader Birthday,Continent".split(",");
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);

        String [] nextLine;

        HashSet<ClubInfo> allClubs = new HashSet<>();
        while ((nextLine = reader.readNext()) != null)
        {
            allClubs.add(ClubInfo.fromCsv(nextLine, columnIndices));
        }

        Matcher<HackClubUser, ClubInfo> clubMatcher = new Matcher<>("Slack users -> clubs", new HashSet<>(GlobalData.allUsers.values()), allClubs, clubScorer);
        Set<MatchResult<HackClubUser, ClubInfo>> results = clubMatcher.getResults();
        HashMap<HackClubUser, ClubInfo> allUsersClubInfo = new HashMap<>();
        for(MatchResult<HackClubUser, ClubInfo> result : results) {
            allUsersClubInfo.put(result.getFrom(), result.getTo());
        }

        return allUsersClubInfo;
    }

    private static MatchScorer<HackClubUser, ClubInfo> clubScorer = new MatchScorer<>() {
        @Override
        public double score(HackClubUser from, ClubInfo to) {
            boolean exactSlackIdMatch = to.getSlackId() != null && StringUtils.equals(to.getSlackId(), from.getSlackUserId());
            boolean hasEitherEmailOrSlackId = exactSlackIdMatch || to.hasEmail(from.getEmail()) || to.hasSlackId(from.getSlackUserId());
            return hasEitherEmailOrSlackId ? 1.0f : 0.0f;
        }

        @Override
        public double getThreshold() {
            return 0.99;
        }
    };

    private void conflateEngagementData() throws CsvValidationException, IOException {
        Blot.conflate(blotEngagementsCsvUri);
        Onboard.conflate(onboardEngagementsCsvUri);
        Sprig.conflate(sprigEngagementsCsvUri);
    }

    private void conflateEventData() throws CsvValidationException, IOException {
        Outernet.conflate(outernetRegistrationCsvUri);
        Assemble.conflate(assembleRegistrationCsvUri);
        Angelhacks.conflate(angelhacksRegistrationCsvUri);
    }

    private void conflateSlackData() {
        final AtomicLong lastReportTime = new AtomicLong(System.currentTimeMillis());
        Set<Map.Entry<String, HackClubUser>> allUsers = GlobalData.allUsers.entrySet();
        final AtomicLong totalEntries = new AtomicLong(allUsers.size());
        final AtomicLong processedEntries = new AtomicLong(0);

        // Iterate over all users
        allUsers.parallelStream().forEach(entry -> {
            String slackId = entry.getValue().getSlackUserId();
            entry.getValue().setSlackInfo(SlackUtils.getSlackInfo(slackId, slackApiKey));
            processedEntries.incrementAndGet();
            reportProgressIfNeeded(lastReportTime, totalEntries, processedEntries);
        });
    }

    private void reportProgressIfNeeded(AtomicLong lastReportTime, AtomicLong totalEntries, AtomicLong processedEntries) {
        Utils.doEvery(lastReportTime, 1000, () -> {
            float percent = ((float) processedEntries.get() / (float) totalEntries.get()) * 100.0f;
            String formattedTime = getFormattedSlackTimeLeft(processedEntries.get(), totalEntries.get());
            System.out.printf("%.2f%% complete (%d/%d) %s%n", percent, processedEntries.get(), totalEntries.get(), formattedTime);
        });
    }

    private String getFormattedSlackTimeLeft(float current, long total) {
        // We take advantage of knowing that we are rate limited to 100 RPM per https://api.slack.com/methods/users.profile.get
        long minutesLeft = (long)((total - current) / 100.0f);
        return String.format("%d minutes left", minutesLeft);
    }

    private void conflateGithubData() {
        GlobalData.allUsers.entrySet().parallelStream().forEach(entry -> {
            HackClubUser user = entry.getValue();
            String githubUsername = user.getGithubUsername();
            if (StringUtils.isNotEmpty(githubUsername)) {
                Github.getUserData(githubUsername, githubApiKey).ifPresent(user::setGithubInfo);
            }
        });
    }

    private void writeUsersToES(ElasticsearchClient esClient) {
        // TODO: Batch writes are much faster
        GlobalData.allUsers.forEach((userId, hackClubUser) -> {
            try {
                esClient.index(i -> i
                        .index(esIndex)
                        .id(hackClubUser.getSlackUserId())
                        .document(hackClubUser));
            } catch (Throwable t) {
                System.out.printf("Warning - %s", t.getMessage().substring(0, 20));
                /*
                t.printStackTrace();
                System.out.println(String.format("Issue writing user %s (%s) to ES - %s", userId, hackClubUser.getFullRealName(), t.getMessage()));
                 */
            }
        });
    }

    private HashSet<String> loadStaffUsers(File staffJsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        StaffUsers pojo = mapper.readValue(staffJsonFile, StaffUsers.class);
        return new HashSet<>(pojo.getUsers());
    }

    private static Aggregator<ChannelEvent, Map<String, Integer>> keywordAggregator = new Aggregator<ChannelEvent, Map<String, Integer>>() {
        @Override
        public Map<String, Integer> add(Map<String, Integer> v1, Map<String, Integer> v2) {
            return Stream.concat(v1.entrySet().stream(), v2.entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            Integer::sum));
        }

        @Override
        public String bucket(ChannelEvent entity) {
            String userId = entity.getUser();
            if (!GlobalData.allUsers.containsKey(userId)) {
                return null;
            }
            return userId;
        }

        @Override
        public Map<String, Integer> getDefaultValue() {
            return Collections.emptyMap();
        }
    };
}
