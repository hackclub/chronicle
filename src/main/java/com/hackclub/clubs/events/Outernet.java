package com.hackclub.clubs.events;

import com.hackclub.clubs.GlobalData;
import com.hackclub.clubs.models.HackClubUser;
import com.hackclub.clubs.models.event.OuternetRegistration;
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

public class Outernet {
    private static String[] columns = "ID,Name,Club Leader?,Workshop / Lightning Talk Focus,Email,Pronouns,Birthday,T-Shirt Size,Travel,Dietary Restrictions,Parent's Name,Parent's Email,GitHub,Example Project,Curiosity,Guild Interest,Guild Focus,ranking,Workshop / Lightning Talk Interest,Stipend Record,Cool ideas,Shuttle Record,migration,workshop status,Waiver Sent,Created,Stipend Approved,Pod,Checked In?,Notes,Contact's Phone number,Checked out,Accepted Stipends".split(",");
    public static void conflate(URI uri) throws CsvValidationException, IOException {
        HashSet<HackClubUser> hackClubUsers = new HashSet<>(GlobalData.allUsers.values());
        HashSet<OuternetRegistration> registrations = loadRegistrations(uri);
        Set<MatchResult<OuternetRegistration, HackClubUser>> results = new Matcher<>(registrations, hackClubUsers, scorer).getResults();
        results.forEach(result -> result.getTo().setOuternetRegistration(result.getFrom()));
    }

    private static HashSet<OuternetRegistration> loadRegistrations(URI uri) throws IOException, CsvValidationException {
        HashSet<OuternetRegistration> registrations = new HashSet<>();
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(uri))).withSkipLines(1).build();
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            registrations.add(OuternetRegistration.fromCsv(nextLine, columnIndices));
        }
        return registrations;
    }

    private static MatchScorer<OuternetRegistration, HackClubUser> scorer = new MatchScorer<>() {
        @Override
        public double score(OuternetRegistration from, HackClubUser to) {
            return Utils.normalizedLevenshtein(from.getEmail(), to.getEmail(), 2);
        }

        @Override
        public double getThreshold() {
            return 0.49;
        }
    };
}
