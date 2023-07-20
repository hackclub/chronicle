package com.hackclub.clubs.models;

/**
 * Model object for information retrieved from Slack API
 */
public class SlackInfo {
    private String githubUrl = null;
    private String githubUsername = null;

    public SlackInfo() {
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    @Override
    public String toString() {
        return "SlackInfo{" +
                "githubUrl='" + githubUrl + '\'' +
                ", githubUsername='" + githubUsername + '\'' +
                '}';
    }
}
