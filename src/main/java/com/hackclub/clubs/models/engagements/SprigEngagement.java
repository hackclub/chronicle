package com.hackclub.clubs.models.engagements;

import java.util.HashMap;

public class SprigEngagement {
    private String githubUsername;
    private String pullRequest;
    private String email;
    private String birthday;
    private String city;
    private String state;
    private String country;
    private String slackId;
    private String status;

    // Not publicly instantiable - use factory methods (fromCsv)
    private SprigEngagement() {
    }

    // GitHub Username,Submitted AT,Pull Request,Email,Proof of Student,Birthday,Authentication ID,Name,Address line 1,Address line 2,City,State or Province,Zip,Country,Phone (optional),Hack Club Slack ID (optional),Color,In a club?,Sprig Status,Club name,Sprig seeds mailed?,How did you hear about Sprig?,Address Formatted,Status,Notes,Tracking,Carrier,Tracking Base Link,Tracking Emailed,Referral Source,Age (years)
    public static SprigEngagement fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        SprigEngagement eng = new SprigEngagement();

        eng.githubUsername = nextLine[columnIndices.get("GitHub Username")];
        eng.pullRequest = nextLine[columnIndices.get("Pull Request")];
        eng.email = nextLine[columnIndices.get("Email")];
        eng.birthday = nextLine[columnIndices.get("Birthday")];
        eng.city = nextLine[columnIndices.get("City")];
        eng.state = nextLine[columnIndices.get("State or Province")];
        eng.country = nextLine[columnIndices.get("Country")];
        eng.slackId = nextLine[columnIndices.get("Hack Club Slack ID (optional)")];
        eng.status = nextLine[columnIndices.get("Sprig Status")];

        return eng;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(String pullRequest) {
        this.pullRequest = pullRequest;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSlackId() {
        return slackId;
    }

    public void setSlackId(String slackId) {
        this.slackId = slackId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
