package com.hackclub.clubs.models;

import java.util.List;

/**
 * Model object for a collection of Hack Club HQ staff members
 */
public class StaffUsers {
    private boolean ok;
    private List<String> users;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
