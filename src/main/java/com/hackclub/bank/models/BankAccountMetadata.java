package com.hackclub.bank.models;

import com.hackclub.clubs.models.GeoPoint;

import java.util.HashMap;

/**
 * Model object representing bank account metadata
 */
public class BankAccountMetadata {
    private String name;
    private String orgType;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dateOfBirth;
    private String mailingAddress;
    private String website;
    private String eventLocation;
    private GeoPoint geolocation;
    private String transparency;
    private String hcbUrl;
    private String pendingState;

    // Not publicly instantiable - use factory methods (fromCsv)
    private BankAccountMetadata() {
    }

    public static BankAccountMetadata fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        BankAccountMetadata bam = new BankAccountMetadata();
        bam.name = nextLine[columnIndices.get("Event Name")];
        bam.orgType = nextLine[columnIndices.get("Org Type")];
        bam.emailAddress = nextLine[columnIndices.get("Email Address")];
        bam.firstName = nextLine[columnIndices.get("First Name")];
        bam.lastName = nextLine[columnIndices.get("Last Name")];
        bam.phoneNumber = nextLine[columnIndices.get("Phone Number")];
        bam.dateOfBirth = nextLine[columnIndices.get("Date of Birth")];
        bam.mailingAddress = nextLine[columnIndices.get("Formatted Mailing Address")];
        bam.website = nextLine[columnIndices.get("Event Website")];
        bam.eventLocation = nextLine[columnIndices.get("Event Location")];
        bam.transparency = nextLine[columnIndices.get("Event Name")];
        bam.hcbUrl = nextLine[columnIndices.get("HCB account URL")];
        return bam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public GeoPoint getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeoPoint geolocation) {
        this.geolocation = geolocation;
    }

    public String getHcbUrl() {
        return hcbUrl;
    }

    public void setHcbUrl(String hcbUrl) {
        this.hcbUrl = hcbUrl;
    }

    public String getTransparency() {
        return transparency;
    }

    public void setTransparency(String transparency) {
        this.transparency = transparency;
    }

    public String getPendingState() {
        return pendingState;
    }

    public void setPendingState(String pendingState) {
        this.pendingState = pendingState;
    }

    @Override
    public String toString() {
        return "BankAccountMetadata{" +
                "name='" + name + '\'' +
                ", orgType='" + orgType + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", mailingAddress='" + mailingAddress + '\'' +
                ", website='" + website + '\'' +
                ", eventLocation='" + eventLocation + '\'' +
                ", geolocation=" + geolocation +
                ", transparency='" + transparency + '\'' +
                ", hcbUrl='" + hcbUrl + '\'' +
                ", pendingState='" + pendingState + '\'' +
                '}';
    }
}
