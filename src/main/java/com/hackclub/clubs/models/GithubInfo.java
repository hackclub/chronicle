package com.hackclub.clubs.models;

import java.time.LocalDate;
import java.util.Map;

/**
 * Model object for information retrieved from Github via GraphQL
 */
public class GithubInfo {
    private String id = null;
    private String bio = null;
    private String companyName = null;
    private Map<String, Integer> pullRequestCountsByLanguage = null;
    private Map<String, Integer> pullRequestCountsByOwner = null;
    private Map<String, Integer> pullRequestCountsByRepo = null;
    private Integer forkedRepoCount = null;
    private Integer ownedRepoCount = null;
    private Integer totalReceivedStarCount = null;
    private LocalDate lastPullRequestTime = null;

    public GithubInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getForkedRepoCount() {
        return forkedRepoCount;
    }

    public void setForkedRepoCount(Integer forkedRepoCount) {
        this.forkedRepoCount = forkedRepoCount;
    }

    public Integer getOwnedRepoCount() {
        return ownedRepoCount;
    }

    public void setOwnedRepoCount(Integer ownedRepoCount) {
        this.ownedRepoCount = ownedRepoCount;
    }

    public Integer getTotalReceivedStarCount() {
        return totalReceivedStarCount;
    }

    public void setTotalReceivedStarCount(Integer totalReceivedStarCount) {
        this.totalReceivedStarCount = totalReceivedStarCount;
    }

    public LocalDate getLastPullRequestTime() {
        return lastPullRequestTime;
    }

    public void setLastPullRequestTime(LocalDate lastPullRequestTime) {
        this.lastPullRequestTime = lastPullRequestTime;
    }

    public Map<String, Integer> getPullRequestCountsByLanguage() {
        return pullRequestCountsByLanguage;
    }

    public void setPullRequestCountsByLanguage(Map<String, Integer> pullRequestCountsByLanguage) {
        this.pullRequestCountsByLanguage = pullRequestCountsByLanguage;
    }

    public Map<String, Integer> getPullRequestCountsByOwner() {
        return pullRequestCountsByOwner;
    }

    public void setPullRequestCountsByOwner(Map<String, Integer> pullRequestCountsByOwner) {
        this.pullRequestCountsByOwner = pullRequestCountsByOwner;
    }

    public Map<String, Integer> getPullRequestCountsByRepo() {
        return pullRequestCountsByRepo;
    }

    public void setPullRequestCountsByRepo(Map<String, Integer> pullRequestCountsByRepo) {
        this.pullRequestCountsByRepo = pullRequestCountsByRepo;
    }

    @Override
    public String toString() {
        return "GithubInfo{" +
                "id='" + id + '\'' +
                ", bio='" + bio + '\'' +
                ", companyName='" + companyName + '\'' +
                ", pullRequestCountsByLanguage=" + pullRequestCountsByLanguage +
                ", pullRequestCountsByOwner=" + pullRequestCountsByOwner +
                ", pullRequestCountsByRepo=" + pullRequestCountsByRepo +
                ", forkedRepoCount=" + forkedRepoCount +
                ", ownedRepoCount=" + ownedRepoCount +
                ", totalReceivedStarCount=" + totalReceivedStarCount +
                ", lastPullRequestTime=" + lastPullRequestTime +
                '}';
    }
}
