package com.hackclub.clubs.models.event;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

// Name,Email,Phone number,School,Grade,Pronouns,T-shirt size,Duration,Skill Level,Game experience,Goals,Helping,Source,Waivers Done,Added to Postal,Checked in,Checked out
public class AngelhacksRegistration {
    private String email;
    private String name;
    private String school;
    private boolean checkedIn;

    // Not publicly instantiable - use factory methods (fromCsv)
    private AngelhacksRegistration() {
    }

    public static AngelhacksRegistration fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        AngelhacksRegistration reg = new AngelhacksRegistration();

        reg.email = nextLine[columnIndices.get("Email")];
        reg.name = nextLine[columnIndices.get("Name")];
        reg.school = nextLine[columnIndices.get("School")];
        reg.checkedIn = StringUtils.isNotEmpty(nextLine[columnIndices.get("Checked in")]);

        return reg;
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

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }
}
