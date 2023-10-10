package com.hackclub.bank.models;

import java.util.HashMap;

public class CsvDocument {
    private double amount;
    private String uniqueBankIdentifier;
    private String email;
    private String fullName;

    //id,stripe_transaction_id,stripe_transaction,amount_cents,date_posted,created_at,updated_at,stripe_authorization_id,unique_bank_identifier,id,event_id,stripe_cardholder_id,stripe_id,stripe_brand,stripe_exp_month,stripe_exp_year,last4,card_type,stripe_status,stripe_shipping_address_city,stripe_shipping_address_country,stripe_shipping_address_line1,stripe_shipping_address_postal_code,stripe_shipping_address_line2,stripe_shipping_address_state,stripe_shipping_name,created_at,updated_at,purchased_at,spending_limit_interval,spending_limit_amount,activated,replacement_for_id,name,is_platinum_april_fools_2023,subledger_id,id,user_id,stripe_id,stripe_billing_address_line1,stripe_billing_address_line2,stripe_billing_address_city,stripe_billing_address_country,stripe_billing_address_postal_code,stripe_billing_address_state,stripe_name,stripe_email,stripe_phone_number,cardholder_type,created_at,updated_at,id,created_at,updated_at,email,full_name,phone_number,admin_at,slug,pretend_is_not_admin,sessions_reported,phone_number_verified,use_sms_auth,webauthn_id,session_duration_seconds,birthday,seasonal_themes_enabled,locked_at,running_balance_enabled,receipt_report_option,preferred_name,access_level
    public static CsvDocument fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        CsvDocument data = new CsvDocument();

        data.amount = Double.parseDouble(nextLine[columnIndices.get("amount_cents")]);
        data.email = nextLine[columnIndices.get("email")];
        data.fullName = nextLine[columnIndices.get("full_name")];
        data.uniqueBankIdentifier = nextLine[columnIndices.get("unique_bank_identifier")];
        return data;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUniqueBankIdentifier() {
        return uniqueBankIdentifier;
    }

    public void setUniqueBankIdentifier(String uniqueBankIdentifier) {
        this.uniqueBankIdentifier = uniqueBankIdentifier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
