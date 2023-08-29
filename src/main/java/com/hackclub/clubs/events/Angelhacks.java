package com.hackclub.clubs.events;

import com.hackclub.clubs.GlobalData;
import com.hackclub.clubs.models.HackClubUser;
import com.hackclub.clubs.models.event.AngelhacksRegistration;
import com.hackclub.clubs.models.event.AssembleRegistration;
import com.hackclub.common.Utils;
import com.hackclub.common.conflation.MatchResult;
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
import java.util.Set;

public class Angelhacks {
    private static String[] columns = "Name,Email,Phone number,School,Grade,Pronouns,T-shirt size,Duration,Skill Level,Game experience,Goals,Helping,Source,Waivers Done,Added to Postal,Checked in,Checked out".split(",");
    public static void conflate(URI uri) throws CsvValidationException, IOException {
        HashSet<HackClubUser> hackClubUsers = new HashSet<>(GlobalData.allUsers.values());
        HashSet<AngelhacksRegistration> registrations = loadRegistrations(uri);
        Set<MatchResult<AngelhacksRegistration, HackClubUser>> results = new Matcher<>(registrations, hackClubUsers, scorer).getResults();
        results.forEach(result -> result.getTo().setAngelhacksRegistration(result.getFrom()));
    }

    private static HashSet<AngelhacksRegistration> loadRegistrations(URI uri) throws IOException, CsvValidationException {
        HashSet<AngelhacksRegistration> registrations = new HashSet<>();
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(uri))).withSkipLines(1).build();
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            registrations.add(AngelhacksRegistration.fromCsv(nextLine, columnIndices));
        }
        return registrations;
    }

    private static MatchScorer<AngelhacksRegistration, HackClubUser> scorer = new MatchScorer<>() {
        @Override
        public double score(AngelhacksRegistration from, HackClubUser to) {
            return Utils.normalizedLevenshtein(from.getEmail(), to.getEmail(), 2);
        }

        @Override
        public double getThreshold() {
            return 0.49;
        }
    };
}
