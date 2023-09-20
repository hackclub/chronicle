package com.hackclub.clubs.models;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * Model object representing information that we know about Club Leaders
 */
public class ClubLeaderApplicationInfo {
    private String email;
    private boolean isOrWasLeader;
    private String fullName;
    private String birthday;
    private String schoolYear;
    private String phoneNumber;
    private String address;
    private String country;
    private String gender;
    private String ethnicity;
    private String prettyAddress;
    private String twitter;
    private String github;
    private Integer birthYear;

    // Not publicly instantiable - use factory methods (fromCsv)
    private ClubLeaderApplicationInfo() {
    }

    // ID,Application,Email,Logins,Application ID,Log In Path,Completed,Full Name,Birthday,School Year,Code,Phone,Address,Address Line 1,Address Line 2,Address City,Address State,Address Zip,Address Country,Address Formatted,Gender,Ethnicity,Website,Twitter,GitHub,Other,Hacker Story,Achievement,Technicality,Accepted Tokens,New Fact,Clubs Dashboard,Birth Year,Turnover ID,Turnover Invite?,Turnover
    public static ClubLeaderApplicationInfo fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        ClubLeaderApplicationInfo cli = new ClubLeaderApplicationInfo();

        cli.email = nextLine[columnIndices.get("Email")];
        cli.isOrWasLeader = nextLine[columnIndices.get("Completed")].equals("checked");
        cli.fullName = nextLine[columnIndices.get("Full Name")];
        cli.birthday = nextLine[columnIndices.get("Birthday")];
        cli.schoolYear = nextLine[columnIndices.get("School Year")];
        cli.phoneNumber = nextLine[columnIndices.get("Phone")];
        cli.address = nextLine[columnIndices.get("Address")];
        cli.country = nextLine[columnIndices.get("Address Country")];
        cli.gender = nextLine[columnIndices.get("Gender")];
        cli.ethnicity = nextLine[columnIndices.get("Ethnicity")];
        cli.prettyAddress = nextLine[columnIndices.get("Address Formatted")];
        cli.twitter = nextLine[columnIndices.get("Twitter")];
        cli.github = nextLine[columnIndices.get("GitHub")];
        String rawBirthYear = nextLine[columnIndices.get("Birth Year")];
        cli.birthYear = (StringUtils.isEmpty(rawBirthYear) || rawBirthYear.equals("NaN")) ? null : Integer.parseInt(rawBirthYear);

        return cli;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOrWasLeader() {
        return isOrWasLeader;
    }

    public void setOrWasLeader(boolean orWasLeader) {
        isOrWasLeader = orWasLeader;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }
}
