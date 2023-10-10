package com.hackclub.bank.commands;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.hackclub.bank.Main;
import com.hackclub.bank.models.BankAccountDataEntry;
import com.hackclub.bank.models.BankAccountDocument;
import com.hackclub.bank.models.BankAccountMetadata;
import com.hackclub.bank.models.CsvDocument;
import com.hackclub.common.elasticsearch.ESCommand;
import com.hackclub.common.elasticsearch.ESUtils;
import com.hackclub.common.file.BlobStore;
import com.hackclub.common.geo.Geocoder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import picocli.CommandLine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@CommandLine.Command(name = "uploadcsv")
public class UploadCsvCommand extends ESCommand {
    @CommandLine.ParentCommand
    private Main mainCmd;

    @CommandLine.Parameters(index = "0", description = "A .csv file that we want to transform to json and upload to ES")
    private URI genericCsvFile;

    @Override
    public Integer call() throws Exception {
        ElasticsearchClient esClient = ESUtils.createElasticsearchClient(esHostname, esPort, esUsername, esPassword, esFingerprint);
        ESUtils.clearIndex(esClient, esIndex);
        writeBankAccountsToES(esClient, loadBankData().stream());

        return 0;
    }

    private ArrayList<CsvDocument> loadBankData() throws IOException, CsvValidationException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(BlobStore.load(genericCsvFile)))
                .withSkipLines(1)
                .build();

        String[] columns = "id,stripe_transaction_id,stripe_transaction,amount_cents,date_posted,created_at,updated_at,stripe_authorization_id,unique_bank_identifier,id,event_id,stripe_cardholder_id,stripe_id,stripe_brand,stripe_exp_month,stripe_exp_year,last4,card_type,stripe_status,stripe_shipping_address_city,stripe_shipping_address_country,stripe_shipping_address_line1,stripe_shipping_address_postal_code,stripe_shipping_address_line2,stripe_shipping_address_state,stripe_shipping_name,created_at,updated_at,purchased_at,spending_limit_interval,spending_limit_amount,activated,replacement_for_id,name,is_platinum_april_fools_2023,subledger_id,id,user_id,stripe_id,stripe_billing_address_line1,stripe_billing_address_line2,stripe_billing_address_city,stripe_billing_address_country,stripe_billing_address_postal_code,stripe_billing_address_state,stripe_name,stripe_email,stripe_phone_number,cardholder_type,created_at,updated_at,id,created_at,updated_at,email,full_name,phone_number,admin_at,slug,pretend_is_not_admin,sessions_reported,phone_number_verified,use_sms_auth,webauthn_id,session_duration_seconds,birthday,seasonal_themes_enabled,locked_at,running_balance_enabled,receipt_report_option,preferred_name,access_level".split(",");
        HashMap<String, Integer> columnIndices = ESUtils.getIndexMapping(columns);

        String [] nextLine;

        ArrayList<CsvDocument> allBankData = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null)
        {
            CsvDocument data = CsvDocument.fromCsv(nextLine, columnIndices);
            allBankData.add(data);
        }
        return allBankData;
    }

    private void writeBankAccountsToES(ElasticsearchClient esClient, Stream<CsvDocument> docs) {
        // TODO: Batch writes are much faster
        AtomicInteger cnt = new AtomicInteger(0);
        docs.forEach(acc -> {
            try {
                esClient.index(i -> i
                        .index(esIndex)
                        .id(String.valueOf(cnt.incrementAndGet()))
                        .document(acc));
            } catch (Throwable t) {
                System.out.println(String.format("Issue writing account %s to ES - %s", acc.toString(), t.getMessage()));
            }
        });
    }

}
