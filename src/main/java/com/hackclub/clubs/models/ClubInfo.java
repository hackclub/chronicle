package com.hackclub.clubs.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClubInfo {
    private String leaderSlackIds;
    private String leaderEmails;
    private String clubAddress;
    private String tier;
    private String notes;
    private String applicationLink;
    private String venue;
    private String lastCheckIn;
    private String status;

    // Not publicly instantiable - use factory methods (fromCsv)
    private ClubInfo() {
    }

    // Venue,Application Link,Current Leader(s),Current Leaders' Emails,Notes,Status,Location,Slack ID,Leader Address,Address Line 1,Address Line 2,Address City,Address State,Address Zip,Address Country,Address Formatted,Last Check-In,Tier,T1-Engaged-Super,T1-Engaged,T1-Super,T1,On Bank,Latitude,Longitude,Last Outreach,Next check-In,Ambassador,Club Leaders,Prospective Leaders,Email (from Prospective Leaders),Full Name (from Prospective Leaders),Phone (from Prospective Leaders),Current Leaders' Phones,Leader Phone,Leader-Club Join,Leader Birthday,Continent
    public static ClubInfo fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        ClubInfo ci = new ClubInfo();

        ci.leaderEmails = nextLine[columnIndices.get("Current Leaders' Emails")];
        ci.leaderSlackIds = nextLine[columnIndices.get("Slack ID")];
        ci.clubAddress = nextLine[columnIndices.get("Address Formatted")];
        ci.lastCheckIn = nextLine[columnIndices.get("Last Check-In")];
        ci.tier = nextLine[columnIndices.get("Tier")];
        ci.notes = nextLine[columnIndices.get("Notes")];
        ci.applicationLink = nextLine[columnIndices.get("Application Link")];
        ci.status = nextLine[columnIndices.get("Status")];
        ci.venue = nextLine[columnIndices.get("Venue")];

        return ci;
    }

    public boolean hasEmail(String email) {
        return getLeaderEmails().collect(Collectors.toSet()).contains(email);
    }

    public boolean hasSlackId(String slackId) {
        return getLeaderSlackIds().collect(Collectors.toSet()).contains(slackId);
    }

    public Stream<String> getLeaderEmails() {
        return Arrays.stream(leaderEmails.replaceAll("\\s", "").split(",")).distinct();
    }

    public Stream<String> getLeaderSlackIds() {
        return Arrays.stream(leaderSlackIds.replaceAll("\\s", "").split(",")).distinct();
    }

    public void setLeaderSlackIds(String leaderSlackIds) {
        this.leaderSlackIds = leaderSlackIds;
    }

    public void setLeaderEmails(String leaderEmails) {
        this.leaderEmails = leaderEmails;
    }

    public String getClubAddress() {
        return clubAddress;
    }

    public void setClubAddress(String clubAddress) {
        this.clubAddress = clubAddress;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getApplicationLink() {
        return applicationLink;
    }

    public void setApplicationLink(String applicationLink) {
        this.applicationLink = applicationLink;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getLastCheckIn() {
        return lastCheckIn;
    }

    public void setLastCheckIn(String lastCheckIn) {
        this.lastCheckIn = lastCheckIn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
