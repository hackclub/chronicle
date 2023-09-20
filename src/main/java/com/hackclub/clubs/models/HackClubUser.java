package com.hackclub.clubs.models;

import com.hackclub.clubs.models.event.AngelhacksRegistration;
import com.hackclub.clubs.models.event.AssembleRegistration;
import com.hackclub.clubs.models.event.EventRegistration;
import com.hackclub.clubs.models.event.OuternetRegistration;
import com.hackclub.common.geo.Geocoder;
import com.hackclub.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents the data that we want to persist in ElasticSearch
 */
public class HackClubUser {
    private String slackUserName;
    private String slackHandle;
    private String email;
    private String status;
    private String slackUserId;
    private String slackDisplayName;
    private long userIndex;
    private LocalDate earliestPostDate = null;
    private LocalDate latestPostDate = null;
    private boolean isStaff = false;
    private String githubUsername = null;
    private String timezone = null;
    private Integer maxScrapbookStreaks = null;
    private String pronouns = null;
    private String website = null;
    private boolean isScrapbookUser = false;
    private boolean isOrWasLeader = false;
    private String fullRealName;
    private String birthday;
    private String schoolYear;
    private String phoneNumber;
    private String address;
    private String country;
    private String gender = "Unknown";
    private String ethnicity = "Unknown";
    private String prettyAddress;
    private String twitter;
    private Integer birthYear;
    private Long lastSlackActivity = null;
    private boolean isAlumni = false;
    private Integer age = null;
    private GeoPoint geolocation = null;
    private boolean hasGeo = false;
    private static ConcurrentHashMap<Long, HackClubUser> indexToUserMapping = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> slackUserIdToIndexMapping = new ConcurrentHashMap<>();
    private static AtomicLong userIdMax = new AtomicLong(0);
    private Map<String, Integer> keywords = new HashMap<>();
    private Optional<SlackInfo> slackInfo = Optional.empty();
    private GithubInfo githubInfo = new GithubInfo();
    private Map<String, EventRegistration> eventAttendance = new HashMap<>();

    public HackClubUser(String slackUserId, String slackHandle, String slackUserName, String email, String status, String slackDisplayName) {
        this.slackUserId = slackUserId;
        this.slackHandle = slackHandle.length() == 0 ? null : slackHandle;
        this.slackUserName = slackUserName;
        this.email = email;
        this.status = status;
        this.slackDisplayName = slackDisplayName;
        this.userIndex = userIdMax.getAndIncrement();
        indexToUserMapping.put(this.userIndex, this);
        slackUserIdToIndexMapping.put(slackUserId, this.userIndex);
    }

    //username,email,status,billing-active,has-2fa,has-sso,userid,fullname,displayname,expiration-timestamp
    public static HackClubUser fromCsv(String[] parts) {
        return new HackClubUser(parts[6], parts[8], parts[0], parts[1], parts[2], parts[7]);
    }

    public static Optional<HackClubUser> fromUserIndex(Long userIndex) {
        return Optional.ofNullable(indexToUserMapping.get(userIndex));
    }

    public static Optional<HackClubUser> fromUserId(String userId) {
        if (userId == null)
            return Optional.empty();

        return Optional.ofNullable(slackUserIdToIndexMapping.get(userId)).flatMap(index -> Optional.ofNullable(indexToUserMapping.get(index)));
    }

    public String getSlackUserName() {
        return slackUserName;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getSlackUserId() {
        return slackUserId;
    }

    public String getSlackDisplayName() {
        return slackDisplayName;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public void onSlackChatMessageProcessed(ChannelEvent entry) {
        final ChannelDay day = entry.getParent();
        final LocalDate date = day.getLocalDate();

        // Let's be careful as this might be called from multiple threads...
        synchronized(this) {
            if(earliestPostDate == null || date.isBefore(earliestPostDate))
                earliestPostDate = date;

            if (latestPostDate == null || date.isAfter(latestPostDate))
                latestPostDate = date;
        }
    }

    public boolean isActiveSince(LocalDate date) {
        return latestPostDate.isAfter(date);
    }

    public void finish() {
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");

        if (latestPostDate != null) {
            lastSlackActivity = latestPostDate.atStartOfDay(zoneId).toEpochSecond();
            if (latestPostDate.isBefore(LocalDate.now().minusYears(5)))
                isAlumni = true;
        }

        if (birthYear != null) {
            age = LocalDate.now().getYear() - birthYear;
        }

        if (prettyAddress != null) {
            try {
                Geocoder.geocode(prettyAddress).ifPresent(this::setGeolocation);
            } catch (Throwable t) {
                System.out.printf("Issue geocoding: %s\n", t.getMessage());
            }
        }
    }

    public void setLeaderInfo(Optional<ClubInfo> clubInfoOpt, Optional<ClubLeaderApplicationInfo> leaderApplicationInfoOpt) {
        if (leaderApplicationInfoOpt.isPresent()) {
            ClubLeaderApplicationInfo leaderApplicationInfo = leaderApplicationInfoOpt.get();
            isOrWasLeader = leaderApplicationInfo.isOrWasLeader();
            fullRealName = leaderApplicationInfo.getFullName();

            birthday = sanitizeDate(leaderApplicationInfo.getBirthday());
            schoolYear = leaderApplicationInfo.getSchoolYear();
            phoneNumber = leaderApplicationInfo.getPhoneNumber();
            address = leaderApplicationInfo.getAddress();
            country = leaderApplicationInfo.getCountry();
            gender = StringUtils.isEmpty(leaderApplicationInfo.getGender()) ? "Unknown" : leaderApplicationInfo.getGender();
            ethnicity = StringUtils.isEmpty(leaderApplicationInfo.getEthnicity()) ? "Unknown" : leaderApplicationInfo.getEthnicity();
            prettyAddress = leaderApplicationInfo.getPrettyAddress();
            twitter = leaderApplicationInfo.getTwitter();

            // Only slurp this if it doesn't exist already
            if (!StringUtils.isEmpty(leaderApplicationInfo.getGithub())) {
                githubUsername = Utils.getLastPathInUrl(leaderApplicationInfo.getGithub());
            }
            birthYear = leaderApplicationInfo.getBirthYear();
        }

        if (clubInfoOpt.isPresent()) {
            // slurp data
        }
    }

    private String sanitizeDate(String birthday) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(birthday, formatter);
            return birthday;
        } catch (Throwable t) {
            return null;
        }
    }

    public void setScrapbookAccount(Optional<ScrapbookAccount> scrapbookAccount) {
        if (scrapbookAccount.isEmpty())
            return;

        ScrapbookAccount sba = scrapbookAccount.get();

        String githubUrl = StringUtils.isEmpty(sba.getGithubUrl()) ? null : sba.getGithubUrl();
        if (githubUrl != null) {
            githubUsername = Utils.getLastPathInUrl(githubUrl);
        }

        timezone = StringUtils.isEmpty(sba.getTimezone()) ? null : sba.getTimezone();
        maxScrapbookStreaks = sba.getMaxStreaks();
        pronouns = StringUtils.isEmpty(sba.getPronouns()) ? null : sba.getPronouns();
        website = StringUtils.isEmpty(sba.getWebsite()) ? null : sba.getWebsite();
        isScrapbookUser = true;
    }

    public void setKeywords(Map<String, Integer> keywords) {
        this.keywords = keywords;
    }

    public Map<String, Integer> getKeywords() {
        return keywords;
    }

    public String getSlackHandle() {
        return slackHandle;
    }

    public void setSlackHandle(String slackHandle) {
        this.slackHandle = slackHandle;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getMaxScrapbookStreaks() {
        return maxScrapbookStreaks;
    }

    public void setMaxScrapbookStreaks(Integer maxScrapbookStreaks) {
        this.maxScrapbookStreaks = maxScrapbookStreaks;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isScrapbookUser() {
        return isScrapbookUser;
    }

    public void setScrapbookUser(boolean scrapbookUser) {
        isScrapbookUser = scrapbookUser;
    }

    public boolean isOrWasLeader() {
        return isOrWasLeader;
    }

    public void setOrWasLeader(boolean orWasLeader) {
        isOrWasLeader = orWasLeader;
    }

    public String getFullRealName() {
        return fullRealName;
    }

    public void setFullRealName(String fullRealName) {
        this.fullRealName = fullRealName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getPrettyAddress() {
        return prettyAddress;
    }

    public void setPrettyAddress(String prettyAddress) {
        this.prettyAddress = prettyAddress;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Long getLastSlackActivity() {
        return lastSlackActivity;
    }

    public void setLastSlackActivity(Long lastSlackActivity) {
        this.lastSlackActivity = lastSlackActivity;
    }

    public boolean isAlumni() {
        return isAlumni;
    }

    public void setAlumni(boolean alumni) {
        isAlumni = alumni;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public GeoPoint getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeoPoint geolocation) {
        this.geolocation = geolocation;
        hasGeo = (geolocation != null);
    }

    public boolean isHasGeo() {
        return hasGeo;
    }

    public void setHasGeo(boolean hasGeo) {
        this.hasGeo = hasGeo;
    }

    public GithubInfo getGithubInfo() {
        return githubInfo;
    }

    public void setGithubInfo(GithubInfo githubInfo) {
        this.githubInfo = githubInfo;
    }

    public void setOperationsInfo(OperationsInfo opsInfo) {
    }

    @Override
    public String toString() {
        return "HackClubUser{" +
                "slackUserName='" + slackUserName + '\'' +
                ", slackHandle='" + slackHandle + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", slackUserId='" + slackUserId + '\'' +
                ", slackDisplayName='" + slackDisplayName + '\'' +
                ", userIndex=" + userIndex +
                ", earliestPostDate=" + earliestPostDate +
                ", latestPostDate=" + latestPostDate +
                ", isStaff=" + isStaff +
                ", timezone='" + timezone + '\'' +
                ", maxScrapbookStreaks=" + maxScrapbookStreaks +
                ", pronouns='" + pronouns + '\'' +
                ", website='" + website + '\'' +
                ", isScrapbookUser=" + isScrapbookUser +
                ", isOrWasLeader=" + isOrWasLeader +
                ", fullRealName='" + fullRealName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", schoolYear='" + schoolYear + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", gender='" + gender + '\'' +
                ", ethnicity='" + ethnicity + '\'' +
                ", prettyAddress='" + prettyAddress + '\'' +
                ", twitter='" + twitter + '\'' +
                ", birthYear=" + birthYear +
                ", lastSlackActivity=" + lastSlackActivity +
                ", isAlumni=" + isAlumni +
                ", age=" + age +
                ", geolocation=" + geolocation +
                ", hasGeo=" + hasGeo +
                ", keywords=" + keywords +
                ", githubInfo=" + githubInfo +
                '}';
    }

    public void setOuternetRegistration(OuternetRegistration reg) {
        EventRegistration data = new EventRegistration();
        data.setRsvped(true);
        data.setAttended(reg.isCheckedIn());
        data.setStipendRequested(reg.isReceivedStipend());
        eventAttendance.put("outernet", data);

        if (StringUtils.isEmpty(githubUsername)) {
            githubUsername = Utils.getLastPathInUrl(reg.getGithub());
        }
    }

    public void setAssembleRegistration(AssembleRegistration reg) {
        EventRegistration data = new EventRegistration();
        data.setRsvped(true);
        data.setAttended(true);
        data.setStipendRequested(reg.isReceivedStipend());

        if (StringUtils.isNotEmpty(reg.getNearestAirport())) {
            if (StringUtils.isEmpty(prettyAddress)) {
                prettyAddress = "Airport - " + reg.getNearestAirport();
            }
        }
        eventAttendance.put("assemble", data);
    }

    public void setAngelhacksRegistration(AngelhacksRegistration reg) {
        EventRegistration data = new EventRegistration();
        data.setAttended(reg.isCheckedIn());
        data.setRsvped(true);
        data.setStipendRequested(false);

        eventAttendance.put("angelhacks", data);
    }

    public Optional<SlackInfo> getSlackInfo() {
        return slackInfo;
    }

    public void setSlackInfo(Optional<SlackInfo> slackInfo) {
        this.slackInfo = slackInfo;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public void setSlackUserName(String slackUserName) {
        this.slackUserName = slackUserName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSlackUserId(String slackUserId) {
        this.slackUserId = slackUserId;
    }

    public void setSlackDisplayName(String slackDisplayName) {
        this.slackDisplayName = slackDisplayName;
    }

    public Map<String, EventRegistration> getEventAttendance() {
        return eventAttendance;
    }

    public void setEventAttendance(Map<String, EventRegistration> eventAttendance) {
        this.eventAttendance = eventAttendance;
    }
}
