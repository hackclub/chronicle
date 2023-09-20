package com.hackclub.clubs.models;

import com.hackclub.clubs.GlobalData;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Model object for events that occur in a channel, such as slack messages, emojis, etc
 */
public class ChannelEvent {
    private String type;
    private String subType;
    private String user;
    private String text;
    private ChannelDay parent;
    private HashMap<String, Integer> tokenOccurrences = new HashMap<>();
    public static ConcurrentHashMap<String, Boolean> ignoredAccounts = new ConcurrentHashMap<>();

    @Override
    public String toString() {
        return "clubs.pojo.DayEntry{" +
                "type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", user='" + user + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void tokenize() {
        createTokens(text);
    }

    public void onComplete() {
        if (!GlobalData.allUsers.containsKey(user)) {
            ignoredAccounts.put(user, true);
            return;
        }

        GlobalData.allUsers.get(user).onSlackChatMessageProcessed(this);
    }

    private void createTokens(String text) {
        Stream.of(text.split(" ")).forEach(potentialToken -> {
            potentialToken = potentialToken.toLowerCase();
            if (GlobalData.validTokens.contains(clean(potentialToken))) {
                if (!tokenOccurrences.containsKey(potentialToken)) {
                    tokenOccurrences.put(potentialToken, 1);
                } else {
                    tokenOccurrences.put(potentialToken, tokenOccurrences.get(potentialToken) + 1);
                }
            }
        });
    }

    public Map<String, Integer> getTokenOccurrences() {
        return tokenOccurrences;
    }

    public String clean(String str) {
        return str.toLowerCase();
    }

    public ChannelDay getParent() {
        return parent;
    }

    public void setParent(ChannelDay parent) {
        this.parent = parent;
    }
}
