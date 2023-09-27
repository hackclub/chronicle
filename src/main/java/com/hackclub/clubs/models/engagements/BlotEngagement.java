package com.hackclub.clubs.models.engagements;

import java.util.HashMap;

public class BlotEngagement {
    private String email;
    private String name;
    private String city;
    private String state;
    private String country;
    private String slackId;
    private String status;

    // Not publicly instantiable - use factory methods (fromCsv)
    private BlotEngagement() {
    }

    // Email,Name,Address Line 1,Address Line 2,Address City,Address State,Address Country,Address Zip,Phone Number,Student Proof,Is Slack User?,Slack ID,Needs Printed Parts?,Status,Created At
    public static BlotEngagement fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        BlotEngagement eng = new BlotEngagement();

        eng.email = nextLine[columnIndices.get("Email")];
        eng.name = nextLine[columnIndices.get("Name")];
        eng.city = nextLine[columnIndices.get("Address City")];
        eng.state = nextLine[columnIndices.get("Address State")];
        eng.country = nextLine[columnIndices.get("Address Country")];
        eng.slackId = nextLine[columnIndices.get("Slack ID")];
        eng.status = nextLine[columnIndices.get("Status")];

        return eng;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
