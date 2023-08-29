package com.hackclub.clubs.models.event;

import com.hackclub.clubs.models.ScrapbookAccount;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Objects;

// ID,Name,Club Leader?,Workshop / Lightning Talk Focus,Email,Pronouns,Birthday,T-Shirt Size,Travel,Dietary Restrictions,Parent's Name,Parent's Email,GitHub,Example Project,Curiosity,Guild Interest,Guild Focus,ranking,Workshop / Lightning Talk Interest,Stipend Record,Cool ideas,Shuttle Record,migration,workshop status,Waiver Sent,Created,Stipend Approved,Pod,Checked In?,Notes,Contact's Phone number,Checked out,Accepted Stipends
public class OuternetRegistration {
    private String email;
    private String name;
    private boolean isLeader;
    private String github;
    private boolean checkedIn;
    private boolean receivedStipend;

    // Not publicly instantiable - use factory methods (fromCsv)
    private OuternetRegistration() {
    }
    public static OuternetRegistration fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        OuternetRegistration reg = new OuternetRegistration();

        reg.email = nextLine[columnIndices.get("Email")];
        reg.name = nextLine[columnIndices.get("Name")];
        reg.isLeader = Boolean.parseBoolean(nextLine[columnIndices.get("Club Leader?")]);
        reg.github = nextLine[columnIndices.get("GitHub")];
        reg.checkedIn = StringUtils.isNotEmpty(nextLine[columnIndices.get("Checked In?")]);
        reg.receivedStipend = StringUtils.isNotEmpty(nextLine[columnIndices.get("Stipend Approved")]);

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

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    @Override
    public String toString() {
        return "OuternetRegistration{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isLeader=" + isLeader +
                ", github='" + github + '\'' +
                ", checkedIn=" + checkedIn +
                '}';
    }

    public boolean isReceivedStipend() {
        return receivedStipend;
    }

    public void setReceivedStipend(boolean receivedStipend) {
        this.receivedStipend = receivedStipend;
    }
}
