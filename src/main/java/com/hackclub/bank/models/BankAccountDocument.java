package com.hackclub.bank.models;

import com.hackclub.clubs.models.GeoPoint;

/**
 * The model object that represents the document we actually want to index in Elasticsearch
 */
public class BankAccountDocument {
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
    private Double amountRaised;
    private Double amountTransacted;
    private Double balance;
    private boolean hasGeo = false;

    public BankAccountDocument(BankAccountMetadata account, BankAccountDataEntry data) {
        name = account.getName();
        orgType = account.getOrgType();
        emailAddress = account.getEmailAddress();
        firstName = account.getFirstName();
        lastName = account.getLastName();
        phoneNumber = account.getPhoneNumber();
        dateOfBirth = account.getDateOfBirth();
        mailingAddress = account.getMailingAddress();
        website = account.getWebsite();
        eventLocation = account.getEventLocation();
        geolocation = account.getGeolocation();
        transparency = account.getTransparency();
        hcbUrl = account.getHcbUrl();
        pendingState = account.getPendingState();
        amountRaised = data.getAmountRaised() / 100.0;
        amountTransacted = data.getAmountTransacted() / 100.0;
        balance = data.getBalance() / 100.0;
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
        this.hasGeo = geolocation != null;
    }

    public String getTransparency() {
        return transparency;
    }

    public void setTransparency(String transparency) {
        this.transparency = transparency;
    }

    public String getHcbUrl() {
        return hcbUrl;
    }

    public void setHcbUrl(String hcbUrl) {
        this.hcbUrl = hcbUrl;
    }

    public String getPendingState() {
        return pendingState;
    }

    public void setPendingState(String pendingState) {
        this.pendingState = pendingState;
    }

    public Double getAmountRaised() {
        return amountRaised;
    }

    public void setAmountRaised(Double amountRaised) {
        this.amountRaised = amountRaised;
    }

    public Double getAmountTransacted() {
        return amountTransacted;
    }

    public void setAmountTransacted(Double amountTransacted) {
        this.amountTransacted = amountTransacted;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BankAccountDocument{" +
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
                ", amountRaised=" + amountRaised +
                ", amountTransacted=" + amountTransacted +
                ", balance=" + balance +
                '}';
    }

    public boolean isHasGeo() {
        return hasGeo;
    }

    public void setHasGeo(boolean hasGeo) {
        this.hasGeo = hasGeo;
    }
}
