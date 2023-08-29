package com.hackclub.clubs.models.event;

public class EventRegistration {
    private boolean rsvped = false;
    private boolean attended = false;
    private boolean stipendRequested = false;

    public EventRegistration() {}

    public boolean isRsvped() {
        return rsvped;
    }

    public void setRsvped(boolean rsvped) {
        this.rsvped = rsvped;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public boolean isStipendRequested() {
        return stipendRequested;
    }

    public void setStipendRequested(boolean stipendRequested) {
        this.stipendRequested = stipendRequested;
    }
}
