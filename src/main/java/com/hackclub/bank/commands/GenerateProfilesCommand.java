package com.hackclub.bank.commands;

import com.hackclub.bank.models.BankAccountDataEntry;
import com.hackclub.bank.models.BankAccountDocument;
import com.hackclub.bank.models.BankAccountMetadata;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.hackclub.common.elasticsearch.ESCommand;
import com.hackclub.common.elasticsearch.ESUtils;
import com.hackclub.common.geo.Geocoder;
import picocli.CommandLine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Generates user profiles and syncs them to an elasticsearch cluster
 */
@CommandLine.Command(name = "genprofiles")
public class GenerateProfilesCommand extends ESCommand {
    @CommandLine.Parameters(index = "0", description = "A .csv file with a mapping of all bank accounts")
    private File bankAccountsCsvFile;

    @CommandLine.Parameters(index = "1", description = "A .csv file with a mapping of all bank account financial data for a time period")
    private File bankAccountEntryCsvFile;

    @Override
    public Integer call() throws Exception {
        ElasticsearchClient esClient = ESUtils.createElasticsearchClient(esHostname, esPort, esUsername, esPassword, esFingerprint);

        ESUtils.clearIndex(esClient, esIndex);

        HashMap<String, BankAccountMetadata> bankAccounts = loadBankAccounts();
        ArrayList<BankAccountDataEntry> dataEntries = loadBankData();

        Stream<BankAccountDocument> docs = dataEntries.stream()
                .flatMap(entry ->
                        getAccountForEntry(bankAccounts, entry).stream()
                                .map(acc -> new BankAccountDocument(acc, entry)));

        writeBankAccountsToES(esClient, docs);

        Geocoder.shutdown();
        return 0;
    }

    private Optional<BankAccountMetadata> getAccountForEntry(HashMap<String, BankAccountMetadata> bankAccounts, BankAccountDataEntry entry) {
        for (BankAccountMetadata md : bankAccounts.values()) {
            if (entry.getEmailAddresses().contains(md.getEmailAddress()))
                return Optional.of(md);
        }
        return Optional.empty();
    }

    private HashMap<String, BankAccountMetadata> loadBankAccounts() throws IOException, CsvValidationException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(bankAccountsCsvFile))
                .withSkipLines(1)
                .build();

        String[] columns = "Event Name,Assignee,Org Type,Created At,Status,First Name,Last Name,Email Address,Phone Number,Date of Birth,Formatted Mailing Address,Mailing Address,Event Website,Event Location,Tell us about your event,Have you used Hack Club Bank for any previous events?,Comments,HCB account URL,Address Country,How did you hear about HCB?,Pending,Transparent".split(",");
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);

        String [] nextLine;

        HashMap<String, BankAccountMetadata> allBankAccounts = new HashMap<>();
        while ((nextLine = reader.readNext()) != null)
        {
            BankAccountMetadata account = BankAccountMetadata.fromCsv(nextLine, columnIndices);
            if (account.getEmailAddress().length() > 0)
                allBankAccounts.put(account.getEmailAddress(), account);
        }
        return allBankAccounts;
    }

    private ArrayList<BankAccountDataEntry> loadBankData() throws IOException, CsvValidationException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(bankAccountEntryCsvFile))
                .withSkipLines(1)
                .build();

        String[] columns = "slug,balance,amount_transacted,amount_raised,array_agg".split(",");
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);

        String [] nextLine;

        ArrayList<BankAccountDataEntry> allBankData = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null)
        {
            BankAccountDataEntry data = BankAccountDataEntry.fromCsv(nextLine, columnIndices);
            allBankData.add(data);
        }
        return allBankData;
    }

    private void writeBankAccountsToES(ElasticsearchClient esClient, Stream<BankAccountDocument> docs) {
        // TODO: Batch writes are much faster
        docs.forEach(acc -> {
            try {
                if (acc.getMailingAddress() != null) {
                    try {
                        Geocoder.geocode(acc.getMailingAddress()).ifPresent(acc::setGeolocation);
                    } catch (Throwable t) {
                        System.out.printf("Issue geocoding: %s\n", t.getMessage());
                    }
                }

                esClient.index(i -> i
                        .index(esIndex)
                        .id(acc.getName())
                        .document(acc));
            } catch (Throwable t) {
                System.out.println(String.format("Issue writing account %s to ES - %s", acc.toString(), t.getMessage()));
            }
        });
    }
}
