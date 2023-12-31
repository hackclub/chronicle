package com.hackclub.clubs.events;

import com.hackclub.clubs.models.HackClubUser;
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

public class Assemble {
    private static String[] columns = "//ID,Email,Log In Path,Full Name,Your Nearest Airport,Birthday,Vaccinated?,\"If you're not vaccinated, please explain why:\",Do you require a letter for visa applications?,Travel Stipend,Dietary Restrictions,\"At the moment, what is your estimated travel cost?\",Travel Stipend Cost INT,What would a travel stipend mean to you?,Skill Level,Would you be interested in hosting a workshop session at Assemble?,Workshop Topic,Shirt,Parent Name,Parent Email,Tabs or Spaces,Pineapple on Pizza,Submission Timestamp,Voted For,Team Notes,Stipend,Decision:,Follow Up,Estimated Cost(Hugo),Amount of Votes,Name (For Prefill),Follow Up (For Prefill),Vote *against*,18?,Serious Alum?,Pronouns,Password Code,Send 2 Weeks Out Email,Waiver,Freedom,Off Waitlist,Vaccinated,waiver_type,Send Wed 3 Email,Created at".split(",");
    public static void conflate(URI uri) throws CsvValidationException, IOException {
        HashSet<HackClubUser> hackClubUsers = new HashSet<>(HackClubUser.getAllUsers().values());
        HashSet<AssembleRegistration> registrations = loadRegistrations(uri);
        Matcher<AssembleRegistration, HackClubUser> matcher = new Matcher<>("Assemble attendance -> Hack Clubbers", registrations, hackClubUsers, scorer);
        matcher.getResults().forEach(result -> result.getTo().setAssembleRegistration(result.getFrom()));
        matcher.getUnmatchedFrom().forEach(assembleRegistration -> {
            String rootId = String.format("assemble-email-%s", assembleRegistration.getEmail());
            HackClubUser newUser = new HackClubUser(rootId);
            newUser.setAssembleRegistration(assembleRegistration);
        });
    }

    private static HashSet<AssembleRegistration> loadRegistrations(URI uri) throws IOException, CsvValidationException {
        HashSet<AssembleRegistration> registrations = new HashSet<>();
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(uri))).withSkipLines(1).build();
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            registrations.add(AssembleRegistration.fromCsv(nextLine, columnIndices));
        }
        return registrations;
    }

    private static MatchScorer<AssembleRegistration, HackClubUser> scorer = new MatchScorer<>() {
        @Override
        public double score(AssembleRegistration from, HackClubUser to) {
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
