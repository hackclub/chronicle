package com.hackclub.clubs.models;

import com.hackclub.clubs.models.engagements.BlotEngagement;
import com.hackclub.clubs.models.engagements.OnboardEngagement;
import com.hackclub.clubs.models.engagements.SprigEngagement;
import com.hackclub.clubs.models.event.AngelhacksRegistration;
import com.hackclub.clubs.models.event.AssembleRegistration;
import com.hackclub.clubs.models.event.EventRegistration;
import com.hackclub.clubs.models.event.OuternetRegistration;
import com.hackclub.clubs.models.engagements.Engagement;
import com.hackclub.common.geo.Geocoder;
import com.hackclub.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the data that we want to persist in ElasticSearch
 */
public class HackClubUser {
    private static HashMap<String, HackClubUser> allUsers = new HashMap<>();

    /**
     * Fields we intend on serializing to JSON - BEGIN
     */
    private String rootId = null;
    private String slackUserName = null;
    private String slackHandle = null;
    private String email = null;
    private String status = null;
    private String slackUserId = null;
    private String slackDisplayName = null;
    private LocalDate earliestPostDate = null;
    private LocalDate latestPostDate = null;
    private String githubUsername = null;
    private String timezone = null;
    private Integer maxScrapbookStreaks = null;
    private String pronouns = null;
    private String website = null;
    private String fullRealName = null;
    private String birthday = null;
    private String schoolYear = null;
    private String phoneNumber = null;
    private String address = null;
    private String country = null;
    private String gender = null;
    private String ethnicity = null;
    private String prettyAddress = null;
    private String twitter = null;
    private Boolean isStaff = false;
    private Boolean isAlumni = false;
    private Boolean hasGeo = false;
    private Boolean isScrapbookUser = false;
    private Boolean isOrWasLeader = false;
    private Boolean isActiveLeader = false;
    private Integer age = null;
    private Integer birthYear = null;
    private Long lastSlackActivity = null;
    private GeoPoint geolocation = null;
    private Map<String, Integer> keywords = new HashMap<>();
    private Optional<SlackInfo> slackInfo = Optional.empty();
    private GithubInfo githubInfo = new GithubInfo();
    private Map<String, EventRegistration> eventAttendance = new HashMap<>();
    private Map<String, Engagement> engagements = new HashMap<>();
    /**
     * Fields we intend on serializing to JSON - END
     */

    // Constructor
    public HackClubUser(String rootId) {
        this.rootId = rootId;
        getAllUsers().put(rootId, this);
    }

    //username,email,status,billing-active,has-2fa,has-sso,userid,fullname,displayname,expiration-timestamp
    public static HackClubUser fromSlackCsv(String[] parts) {
        String slackUserId = parts[6];
        HackClubUser newUser = new HackClubUser(slackUserId);
        newUser.setSlackData(slackUserId, parts[8], parts[0], parts[1], parts[2], parts[7]);
        return newUser;
    }

    public static HashMap<String, HackClubUser> getAllUsers() {
        return allUsers;
    }

    public static Optional<HackClubUser> get(String rootId) {
        return Optional.ofNullable(allUsers.getOrDefault(rootId, null));
    }

    public void setSlackData(String slackUserId, String slackHandle, String slackUserName, String email, String status, String slackDisplayName) {
        this.slackUserId = slackUserId;
        this.slackHandle = slackHandle.length() == 0 ? null : slackHandle;
        this.slackUserName = slackUserName;
        this.email = email;
        this.status = status;
        this.slackDisplayName = slackDisplayName;
    }

    public static Optional<HackClubUser> getWithRootId(String rootId) {
        if (rootId == null)
            return Optional.empty();

        return Optional.ofNullable(getAllUsers().getOrDefault(rootId, null));
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

            birthday = Utils.sanitizeDate(leaderApplicationInfo.getBirthday());
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
            ClubInfo clubInfo = clubInfoOpt.get();

            isOrWasLeader = true;
            isActiveLeader = StringUtils.equals(clubInfo.getStatus(), "active");
            if (StringUtils.isEmpty(prettyAddress) && !StringUtils.isEmpty(clubInfo.getClubAddress()))
                prettyAddress = clubInfo.getClubAddress();
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
        this.birthday = Utils.sanitizeDate(birthday);
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

        if (StringUtils.isEmpty(email)) email = reg.getEmail();
        if (StringUtils.isEmpty(fullRealName)) fullRealName = reg.getName();
        if (StringUtils.isEmpty(githubUsername)) githubUsername = Utils.getLastPathInUrl(reg.getGithub());
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

        if (StringUtils.isEmpty(email)) email = reg.getEmail();
        if (StringUtils.isEmpty(fullRealName)) fullRealName = reg.getFullName();
    }

    public void setAngelhacksRegistration(AngelhacksRegistration reg) {
        EventRegistration data = new EventRegistration();
        data.setAttended(reg.isCheckedIn());
        data.setRsvped(true);
        data.setStipendRequested(false);
        eventAttendance.put("angelhacks", data);

        if (StringUtils.isEmpty(email)) email = reg.getEmail();
        if (StringUtils.isEmpty(fullRealName)) fullRealName = reg.getName();
    }

    public void setBlotEngagement(BlotEngagement reg) {
        Engagement data = new Engagement();
        data.setImpressed(true);
        data.setRewarded(StringUtils.equals(reg.getStatus(), "Shipped"));
        engagements.put("blot", data);
        if (StringUtils.isEmpty(email)) email = reg.getEmail();
        if (StringUtils.isEmpty(fullRealName)) fullRealName = reg.getName();
        if (StringUtils.isEmpty(slackUserId)) slackUserId = reg.getSlackId();
        String blotAddress = String.format("%s, %s, %s", reg.getCity(), reg.getState(), reg.getCountry());
        if (StringUtils.isEmpty(address)) address = blotAddress;
        if (StringUtils.isEmpty(prettyAddress)) prettyAddress = blotAddress;
    }

    public void setSprigEngagement(SprigEngagement reg) {
        Engagement data = new Engagement();
        data.setImpressed(true);
        data.setRewarded(StringUtils.equals(reg.getStatus(), "Shipped"));
        engagements.put("sprig", data);

        if (StringUtils.isEmpty(email)) email = reg.getEmail();
        String sprigAddress = String.format("%s, %s, %s", reg.getCity(), reg.getState(), reg.getCountry());
        if (StringUtils.isEmpty(address)) address = sprigAddress;
        if (StringUtils.isEmpty(prettyAddress)) prettyAddress = sprigAddress;
        if (StringUtils.isEmpty(slackUserId)) slackUserId = reg.getSlackId();
        if (StringUtils.isEmpty(fullRealName)) fullRealName = reg.getName();
    }

    public void setOnboardEngagement(OnboardEngagement reg) {
        Engagement data = new Engagement();
        data.setImpressed(true);
        data.setRewarded(StringUtils.equals(reg.getStatus(), "Approved"));
        engagements.put("onboard", data);
        String onboardAddress = String.format("%s, %s", reg.getCity(), reg.getState());
        if (StringUtils.isEmpty(email)) email = reg.getEmail();
        if (StringUtils.isEmpty(address)) address = onboardAddress;
        if (StringUtils.isEmpty(prettyAddress)) prettyAddress = onboardAddress;
        if (StringUtils.isEmpty(fullRealName)) fullRealName = reg.getFullName();
        if (StringUtils.isEmpty(birthday)) birthday = Utils.sanitizeDate(reg.getBirthDate());
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

    public boolean isActiveLeader() {
        return isActiveLeader;
    }

    public void setActiveLeader(boolean activeLeader) {
        isActiveLeader = activeLeader;
    }

    public Map<String, Engagement> getEngagements() {
        return engagements;
    }

    public void setEngagements(Map<String, Engagement> engagements) {
        this.engagements = engagements;
    }
    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

}
