package com.hackclub.bank.models;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Model object representing a single HCB account, including its balance, transacted funds, and money raised
 */
public class BankAccountDataEntry {
    private Double amountRaised;
    private Double amountTransacted;
    private Double balance;
    private HashSet<String> emailAddresses;

    public static BankAccountDataEntry fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        BankAccountDataEntry data = new BankAccountDataEntry();
        data.amountRaised = Double.parseDouble(nextLine[columnIndices.get("amount_raised")]);
        data.amountTransacted = Double.parseDouble(nextLine[columnIndices.get("amount_transacted")]);
        data.balance = Double.parseDouble(nextLine[columnIndices.get("balance")]);
        data.emailAddresses = parseEmails(nextLine[columnIndices.get("array_agg")]);
        return data;
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

    public HashSet<String> getEmailAddresses() {
        return emailAddresses;
    }

    @Override
    public String toString() {
        return "BankAccountDataEntry{" +
                "amountRaised=" + amountRaised +
                ", amountTransacted=" + amountTransacted +
                ", balance=" + balance +
                ", emailAddresses=" + emailAddresses +
                '}';
    }

    private static HashSet<String> parseEmails(String emails) {
        HashSet<String> ret = new HashSet<>();
        for(String email : emails.split(",")) {
            email = email.replace("{", "").replace("}", "");
            ret.add(email);
        }
        return ret;
    }
}
