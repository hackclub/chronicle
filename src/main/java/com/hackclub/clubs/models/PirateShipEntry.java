package com.hackclub.clubs.models;

import java.util.HashMap;

/**
 * Model object for address data contained within PirateShip
 */
public class PirateShipEntry {
    private String recipient;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String concatenatedAddress;

    private PirateShipEntry() {}

    // Created Date,Recipient,Company,Email,Tracking Number,Cost,Status,Batch,Label Size,Saved Package,Ship From,Ship Date,Estimated Delivery Time,Weight (oz),Zone,Package Type,Package Length,Package Width,Package Height,Tracking Status,Tracking Info,Tracking Date,Address Line 1,Address Line 2,City,State,Zipcode,Country,Carrier,Service,Order ID,Rubber Stamp 1,Rubber Stamp 2,Rubber Stamp 3,Order Value
    public static PirateShipEntry fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        PirateShipEntry e = new PirateShipEntry();
        e.recipient = nextLine[columnIndices.get("Recipient")];

        e.addressLine1 = nextLine[columnIndices.get("Address Line 1")];
        e.addressLine2 = nextLine[columnIndices.get("Address Line 2")];
        e.city = nextLine[columnIndices.get("City")];
        e.state = nextLine[columnIndices.get("State")];
        e.zipCode = nextLine[columnIndices.get("Zipcode")];
        e.country = nextLine[columnIndices.get("Country")];
        e.concatenatedAddress = String.format("%s %s %s %s %s %s",
                e.addressLine1, e.addressLine2, e.city, e.state, e.zipCode, e.country);

        return e;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
