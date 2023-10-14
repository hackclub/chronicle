package com.hackclub.clubs.engagements;

import com.hackclub.clubs.models.HackClubUser;
import com.hackclub.clubs.models.engagements.BlotEngagement;
import com.hackclub.common.Utils;
import com.hackclub.common.conflation.MatchScorer;
import com.hackclub.common.conflation.Matcher;
import com.hackclub.common.elasticsearch.ESUtils;
import com.hackclub.common.file.BlobStore;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;

public class Blot {
    private static String[] columns = "Email,Name,Address Line 1,Address Line 2,Address City,Address State,Address Country,Address Zip,Phone Number,Student Proof,Is Slack User?,Slack ID,Needs Printed Parts?,Status,Created At".split(",");
    public static void conflate(URI uri) throws CsvValidationException, IOException {
        HashSet<HackClubUser> hackClubUsers = new HashSet<>(HackClubUser.getAllUsers().values());
        HashSet<BlotEngagement> registrations = load(uri);
        Matcher<BlotEngagement, HackClubUser> matcher = new Matcher<>("Blot engagements -> Hack Clubbers", registrations, hackClubUsers, scorer);
        matcher.getResults().forEach(result -> result.getTo().setBlotEngagement(result.getFrom()));
        matcher.getUnmatchedFrom().forEach(blotEngagement -> {
            HackClubUser newUser = new HackClubUser("blot-" + blotEngagement.getEmail());
            newUser.setBlotEngagement(blotEngagement);
        });
    }

    private static HashSet<BlotEngagement> load(URI uri) throws IOException, CsvValidationException {
        HashSet<BlotEngagement> registrations = new HashSet<>();
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(uri))).withSkipLines(1).build();
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            registrations.add(BlotEngagement.fromCsv(nextLine, columnIndices));
        }
        return registrations;
    }

    private static MatchScorer<BlotEngagement, HackClubUser> scorer = new MatchScorer<>() {
        @Override
        public double score(BlotEngagement from, HackClubUser to) {
            return Utils.normalizedLevenshtein(from.getEmail(), to.getEmail(), 2);
        }

        @Override
        public double getThreshold() {
            return 0.49;
        }
    };
}
