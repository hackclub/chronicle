package com.hackclub.clubs.models;

import java.util.HashMap;
import java.util.Objects;

/**
 * Model object for data relating to a Scrapbook account
 */
public class ScrapbookAccount {
    private String githubUrl = null;
    private String timezone = null;

    private boolean fullSlackMember;

    private String pronouns = null;

    private String slackId = null;

    private Integer maxStreaks = null;

    private String email = null;

    private String website = null;

    // Not publicly instantiable - use factory methods (fromCsv)
    private ScrapbookAccount() {
    }
    public static ScrapbookAccount fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        ScrapbookAccount sba = new ScrapbookAccount();
        sba.githubUrl = nextLine[columnIndices.get("github")];
        if (sba.githubUrl != null && !sba.githubUrl.startsWith("https://github.com/"))
            sba.githubUrl = null;
        sba.timezone = nextLine[columnIndices.get("timezone")];
        sba.fullSlackMember = Objects.equals(nextLine[columnIndices.get("github")], "t");

        String rawMaxStreaks = nextLine[columnIndices.get("maxstreaks")];
        sba.maxStreaks = rawMaxStreaks.length() > 0 ? Integer.parseInt(rawMaxStreaks) : null;
        sba.email = nextLine[columnIndices.get("email")];
        sba.slackId = nextLine[columnIndices.get("slackid")];
        sba.pronouns = nextLine[columnIndices.get("pronouns")];
        sba.website = nextLine[columnIndices.get("website")];
        return sba;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isFullSlackMember() {
        return fullSlackMember;
    }

    public void setFullSlackMember(boolean fullSlackMember) {
        this.fullSlackMember = fullSlackMember;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getSlackId() {
        return slackId;
    }

    public void setSlackId(String slackId) {
        this.slackId = slackId;
    }

    public Integer getMaxStreaks() {
        return maxStreaks;
    }

    public void setMaxStreaks(Integer maxStreaks) {
        this.maxStreaks = maxStreaks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "ScrapbookAccount{" +
                "githubUrl='" + githubUrl + '\'' +
                ", timezone='" + timezone + '\'' +
                ", fullSlackMember=" + fullSlackMember +
                ", pronouns='" + pronouns + '\'' +
                ", slackId='" + slackId + '\'' +
                ", maxStreaks=" + maxStreaks +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
