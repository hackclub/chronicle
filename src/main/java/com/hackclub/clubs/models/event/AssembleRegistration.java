package com.hackclub.clubs.models.event;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

//ID,Email,Log In Path,Full Name,Your Nearest Airport,Birthday,Vaccinated?,"If you're not vaccinated, please explain why:",Do you require a letter for visa applications?,Travel Stipend,Dietary Restrictions,"At the moment, what is your estimated travel cost?",Travel Stipend Cost INT,What would a travel stipend mean to you?,Skill Level,Would you be interested in hosting a workshop session at Assemble?,Workshop Topic,Shirt,Parent Name,Parent Email,Tabs or Spaces,Pineapple on Pizza,Submission Timestamp,Voted For,Team Notes,Stipend,Decision:,Follow Up,Estimated Cost(Hugo),Amount of Votes,Name (For Prefill),Follow Up (For Prefill),Vote *against*,18?,Serious Alum?,Pronouns,Password Code,Send 2 Weeks Out Email,Waiver,Freedom,Off Waitlist,Vaccinated,waiver_type,Send Wed 3 Email,Created at
public class AssembleRegistration {
    private String email;
    private String fullName;
    private String nearestAirport;
    private boolean receivedStipend;

    // Not publicly instantiable - use factory methods (fromCsv)
    private AssembleRegistration() {
    }

    public static AssembleRegistration fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        AssembleRegistration reg = new AssembleRegistration();

        reg.email = nextLine[columnIndices.get("Email")];
        reg.fullName = nextLine[columnIndices.get("Full Name")];
        reg.nearestAirport = nextLine[columnIndices.get("Your Nearest Airport")];
        reg.receivedStipend = StringUtils.isNotEmpty(nextLine[columnIndices.get("Travel Stipend")]);

        return reg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNearestAirport() {
        return nearestAirport;
    }

    public void setNearestAirport(String nearestAirport) {
        this.nearestAirport = nearestAirport;
    }

    public boolean isReceivedStipend() {
        return receivedStipend;
    }

    public void setReceivedStipend(boolean receivedStipend) {
        this.receivedStipend = receivedStipend;
    }
}
