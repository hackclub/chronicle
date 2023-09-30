package com.hackclub.clubs.engagements;

import com.hackclub.clubs.GlobalData;
import com.hackclub.clubs.models.HackClubUser;
import com.hackclub.clubs.models.engagements.BlotEngagement;
import com.hackclub.clubs.models.engagements.SprigEngagement;
import com.hackclub.common.Utils;
import com.hackclub.common.conflation.MatchResult;
import com.hackclub.common.conflation.MatchScorer;
import com.hackclub.common.conflation.Matcher;
import com.hackclub.common.elasticsearch.ESUtils;
import com.hackclub.common.file.BlobStore;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Sprig {
    private static String[] columns = "GitHub Username,Submitted AT,Pull Request,Email,Proof of Student,Birthday,Authentication ID,Name,Address line 1,Address line 2,City,State or Province,Zip,Country,Phone (optional),Hack Club Slack ID (optional),Color,In a club?,Sprig Status,Club name,Sprig seeds mailed?,How did you hear about Sprig?,Address Formatted,Status,Notes,Tracking,Carrier,Tracking Base Link,Tracking Emailed,Referral Source,Age (years)".split(",");
    public static void conflate(URI uri) throws CsvValidationException, IOException {
        HashSet<HackClubUser> hackClubUsers = new HashSet<>(GlobalData.allUsers.values());
        HashSet<SprigEngagement> registrations = load(uri);
        Set<MatchResult<SprigEngagement, HackClubUser>> results = new Matcher<>("Sprig engagements -> Hack Clubbers", registrations, hackClubUsers, scorer).getResults();
        results.forEach(result -> result.getTo().setSprigEngagement(result.getFrom()));
    }

    private static HashSet<SprigEngagement> load(URI uri) throws IOException, CsvValidationException {
        HashSet<SprigEngagement> registrations = new HashSet<>();
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(uri))).withSkipLines(1).build();
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            registrations.add(SprigEngagement.fromCsv(nextLine, columnIndices));
        }
        return registrations;
    }

    private static MatchScorer<SprigEngagement, HackClubUser> scorer = new MatchScorer<>() {
        @Override
        public double score(SprigEngagement from, HackClubUser to) {
            if(to.getSlackUserId() != null && StringUtils.equals(from.getSlackId(), to.getSlackUserId())) {
                return 1.0f;
            }

            double emailDistance = Utils.normalizedLevenshtein(from.getEmail(), to.getEmail(), 2);
            if (emailDistance > getThreshold()) return emailDistance;
            return Utils.normalizedLevenshtein(from.getName(), to.getFullRealName(), 2);
        }

        @Override
        public double getThreshold() {
            return 0.49;
        }
    };
}
