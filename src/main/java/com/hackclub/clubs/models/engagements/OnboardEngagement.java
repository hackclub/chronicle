package com.hackclub.clubs.models.engagements;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class OnboardEngagement {
    private String fullName;
    private String githubHandle;
    private String status;
    private String birthDate;
    private String city;
    private String state;

    // Not publicly instantiable - use factory methods (fromCsv)
    private OnboardEngagement() {
    }

    // Full Name,Email,Proof of High School Enrollment,GitHub handle,Country,Status,Commented on Github? ,On HCB? ,Birthdate,1st line of shipping address,Zip/Postal code of shipping address,2nd line of shipping address,City (shipping address),State,Referral category,How did you hear about OnBoard?,Created,Is this the first PCB you've made?
    public static OnboardEngagement fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        OnboardEngagement eng = new OnboardEngagement();

        eng.fullName = nextLine[columnIndices.get("Full Name")];
        eng.githubHandle = nextLine[columnIndices.get("GitHub handle")];
        eng.status = nextLine[columnIndices.get("Status")];
        eng.birthDate = nextLine[columnIndices.get("Birthdate")];
        eng.city = nextLine[columnIndices.get("City (shipping address)")];
        eng.state = nextLine[columnIndices.get("State")];

        return eng;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGithubHandle() {
        return githubHandle;
    }

    public void setGithubHandle(String githubHandle) {
        this.githubHandle = githubHandle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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
}
