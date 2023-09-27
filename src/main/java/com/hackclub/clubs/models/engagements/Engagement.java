package com.hackclub.clubs.models.engagements;

public class Engagement {
    // Whether or not this person had any interaction as a result of the engagement (i.e., joined a zoom, RSVP'd somehow, chatted in an associated slack channel
    private boolean impressed;

    // Whether or not this person was rewarded with a prize from this engagement
    private boolean rewarded;

    // The reason for the engagement happening ("I saw a poster!", etc)
    private String reason;

    public Engagement() {}

    public boolean isImpressed() {
        return impressed;
    }

    public void setImpressed(boolean impressed) {
        this.impressed = impressed;
    }

    public boolean isRewarded() {
        return rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
