package com.hackclub.clubs.engagements;

import com.hackclub.clubs.GlobalData;
import com.hackclub.clubs.models.HackClubUser;
import com.hackclub.clubs.models.engagements.BlotEngagement;
import com.hackclub.clubs.models.engagements.OnboardEngagement;
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

public class Onboard {
    private static String[] columns = "Full Name,Email,Proof of High School Enrollment,GitHub handle,Country,Status,Commented on Github? ,On HCB? ,Birthdate,1st line of shipping address,Zip/Postal code of shipping address,2nd line of shipping address,City (shipping address),State,Referral category,How did you hear about OnBoard?,Created,Is this the first PCB you've made?".split(",");
    public static void conflate(URI uri) throws CsvValidationException, IOException {
        HashSet<HackClubUser> hackClubUsers = new HashSet<>(GlobalData.allUsers.values());
        HashSet<OnboardEngagement> registrations = load(uri);
        Set<MatchResult<OnboardEngagement, HackClubUser>> results = new Matcher<>("Onboard engagements -> Hack Clubbers", registrations, hackClubUsers, scorer).getResults();
        results.forEach(result -> result.getTo().setOnboardEngagement(result.getFrom()));
    }

    private static HashSet<OnboardEngagement> load(URI uri) throws IOException, CsvValidationException {
        HashSet<OnboardEngagement> engagements = new HashSet<>();
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(uri))).withSkipLines(1).build();
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            engagements.add(OnboardEngagement.fromCsv(nextLine, columnIndices));
        }
        return engagements;
    }

    private static MatchScorer<OnboardEngagement, HackClubUser> scorer = new MatchScorer<>() {
        @Override
        public double score(OnboardEngagement from, HackClubUser to) {
            double normalizedEmailDistance = Utils.normalizedLevenshtein(Utils.safeToLower(from.getEmail()), Utils.safeToLower(to.getEmail()), 2);
            if (normalizedEmailDistance > 0.49) return 1.0;
            double normalizedFullnameDistance = Utils.normalizedLevenshtein(Utils.safeToLower(from.getFullName()), Utils.safeToLower(to.getFullRealName()), 2);
            if (normalizedFullnameDistance > 0.49)
                return 1.0;
            return (normalizedFullnameDistance + normalizedEmailDistance) / 2;
        }

        @Override
        public double getThreshold() {
            return 0.49;
        }
    };
}
