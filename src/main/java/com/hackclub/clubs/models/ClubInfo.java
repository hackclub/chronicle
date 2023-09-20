package com.hackclub.clubs.models;

import java.util.HashMap;

public class ClubInfo {
    // Not publicly instantiable - use factory methods (fromCsv)
    private ClubInfo() {
    }

    // Venue,Application Link,Current Leader(s),Current Leaders' Emails,Notes,Status,Location,Slack ID,Leader Address,Address Line 1,Address Line 2,Address City,Address State,Address Zip,Address Country,Address Formatted,Last Check-In,Tier,T1-Engaged-Super,T1-Engaged,T1-Super,T1,On Bank,Latitude,Longitude,Last Outreach,Next check-In,Ambassador,Club Leaders,Prospective Leaders,Email (from Prospective Leaders),Full Name (from Prospective Leaders),Phone (from Prospective Leaders),Current Leaders' Phones,Leader Phone,Leader-Club Join,Leader Birthday,Continent
    public static ClubInfo fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        ClubInfo ci = new ClubInfo();

        // ex..
        //ci.email = nextLine[columnIndices.get("Email")];
        return ci;
    }
}
