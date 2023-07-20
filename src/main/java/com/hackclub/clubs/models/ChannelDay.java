package com.hackclub.clubs.models;

import com.hackclub.clubs.GlobalData;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Model object that represents one day of channel data
 */
public class ChannelDay {
    private ChannelEvent[] entries;
    private String filename;
    private String channelName;
    private Integer month;
    private Integer day;
    private Integer year;
    public String getChannelName() {
        return channelName;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "clubs.pojo.Day{" +
                ", channelName='" + channelName + '\'' +
                ", month=" + month +
                ", day=" + day +
                ", year=" + year +
                ", numChats=" + entries.length +
                '}';
    }

    public ChannelDay(String filename, ChannelEvent[] entries) {
        this.filename = filename;

        String[] parts = filename.split("/");
        if (parts.length < 1) throw new RuntimeException("Whoa something is weird here");

        String localFilename = parts[parts.length-1];
        this.channelName = parts[parts.length-2];

        if (!localFilename.endsWith(".json"))
            throw new RuntimeException("Whoa");
        String prefix = localFilename.split("\\.")[0];
        String[] dateComponents = prefix.split("-");
        this.year = Integer.parseInt(dateComponents[0]);
        this.month = Integer.parseInt(dateComponents[1]);
        this.day = Integer.parseInt(dateComponents[2]);

        setEntries(entries);
    }
    public Stream<ChannelEvent> getEntries(boolean excludeStaff) {
        Stream<ChannelEvent> data = Stream.of(entries);

        if (excludeStaff) {
            data = data.filter(entry -> !GlobalData.staffUserIds.contains(entry.getUser()));
        }

        return data;
    }

    public void setEntries(ChannelEvent[] entries) {
        this.entries = entries;
        for(ChannelEvent entry : this.entries) {
            entry.setParent(this);
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    Stream<HackClubUser> allPostingUsers() {
        return Arrays.stream(entries).flatMap(entry -> HackClubUser.fromUserId(entry.getUser()).stream());
    }

    public LocalDate getLocalDate() {
        final String dayStr = String.format("%d-%02d-%02d", getYear(), getMonth(), getDay());
        return LocalDate.parse(dayStr);
    }
}
