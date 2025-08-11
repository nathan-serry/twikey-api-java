package com.twikey.modal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface DocumentRequests {

    /**
     * InviteRequest holds the full set of fields that can be used
     * to initiate a mandate invitation via the Twikey API.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>ct (String): Contract template ID.</li>
     *   <li>l (String): Language code (e.g., "en", "nl").</li>
     *   <li>iban (String): IBAN of the customer.</li>
     *   <li>bic (String): BIC/SWIFT code of the bank.</li>
     *   <li>mandateNumber (String): Custom mandate number (optional).</li>
     *   <li>customerNumber (String): Internal customer reference.</li>
     *   <li>email (String): Email address of the invitee.</li>
     *   <li>lastName (String): Last name of the invitee.</li>
     *   <li>firstName (String): First name of the invitee.</li>
     *   <li>mobile (String): Mobile phone number (international format).</li>
     *   <li>address (String): Street address.</li>
     *   <li>city (String): City name.</li>
     *   <li>zip (String): Postal code.</li>
     *   <li>country (String): Country code (e.g., "BE").</li>
     *   <li>companyName (String): Name of the company (if business mandate).</li>
     *   <li>vatno (String): VAT number.</li>
     *   <li>contractNumber (String): External contract number.</li>
     *   <li>campaign (String): Campaign identifier for tracking.</li>
     *   <li>prefix (String): Honorific or title (e.g., Mr., Ms.).</li>
     *   <li>check (Boolean): Whether Twikey should verify the IBAN.</li>
     *   <li>ed (String): Execution date for the mandate.</li>
     *   <li>reminderDays (Integer): Number of days before sending reminder.</li>
     *   <li>sendInvite (Boolean): Whether to send the invitation immediately.</li>
     *   <li>token (String): Optional token to pre-fill or resume an invite.</li>
     *   <li>requireValidation (Boolean): Whether IBAN validation is required.</li>
     *   <li>document (String): Document reference or identifier.</li>
     *   <li>transactionAmount (Double): One-time transaction amount.</li>
     *   <li>transactionMessage (String): Message for the transaction.</li>
     *   <li>transactionRef (String): Reference for the transaction.</li>
     *   <li>plan (String): Identifier of a predefined payment plan.</li>
     *   <li>subscriptionStart (String): Start date for the subscription (YYYY-MM-DD).</li>
     *   <li>subscriptionRecurrence (String): Recurrence rule (e.g., "monthly").</li>
     *   <li>subscriptionStopAfter (Integer): Number of times the subscription should run.</li>
     *   <li>subscriptionAmount (Double): Amount to be charged in each cycle.</li>
     *   <li>subscriptionMessage (String): Description or message for the subscription.</li>
     *   <li>subscriptionRef (String): Reference for the subscription.</li>
     * </ul>
     */
    public class InviteRequest{
        // Fields
        private final long ct;
        private String l, iban, bic, mandateNumber, customerNumber, email, lastName, firstName,
                mobile, address, city, zip, country, companyName, vatno, contractNumber,
                campaign, prefix, ed, token, document, transactionMessage, transactionRef,
                plan, subscriptionStart, subscriptionRecurrence, subscriptionMessage, subscriptionRef;

        private Boolean check, sendInvite, requireValidation;
        private Integer reminderDays, subscriptionStopAfter;
        private Double transactionAmount, subscriptionAmount;

        public InviteRequest(long ct) {
            this.ct = ct;
        }

        // Field mapping
        private static final Map<String, String> FIELD_MAP = new HashMap<>();
        static {
            FIELD_MAP.put("ct", "ct");
            FIELD_MAP.put("l", "l");
            FIELD_MAP.put("iban", "iban");
            FIELD_MAP.put("bic", "bic");
            FIELD_MAP.put("email", "email");
            FIELD_MAP.put("firstName", "firstname");
            FIELD_MAP.put("lastName", "lastname");
            FIELD_MAP.put("mandateNumber", "mandateNumber");
            FIELD_MAP.put("customerNumber", "customerNumber");
            FIELD_MAP.put("mobile", "mobile");
            FIELD_MAP.put("address", "address");
            FIELD_MAP.put("city", "city");
            FIELD_MAP.put("zip", "zip");
            FIELD_MAP.put("country", "country");
            FIELD_MAP.put("companyName", "companyName");
            FIELD_MAP.put("vatno", "vatno");
            FIELD_MAP.put("contractNumber", "contractNumber");
            FIELD_MAP.put("campaign", "campaign");
            FIELD_MAP.put("prefix", "prefix");
            FIELD_MAP.put("check", "check");
            FIELD_MAP.put("ed", "ed");
            FIELD_MAP.put("reminderDays", "reminderDays");
            FIELD_MAP.put("sendInvite", "sendInvite");
            FIELD_MAP.put("token", "token");
            FIELD_MAP.put("requireValidation", "requireValidation");
            FIELD_MAP.put("document", "document");
            FIELD_MAP.put("transactionAmount", "transactionAmount");
            FIELD_MAP.put("transactionMessage", "transactionMessage");
            FIELD_MAP.put("transactionRef", "transactionRef");
            FIELD_MAP.put("plan", "plan");
            FIELD_MAP.put("subscriptionStart", "subscriptionStart");
            FIELD_MAP.put("subscriptionRecurrence", "subscriptionRecurrence");
            FIELD_MAP.put("subscriptionStopAfter", "subscriptionStopAfter");
            FIELD_MAP.put("subscriptionAmount", "subscriptionAmount");
            FIELD_MAP.put("subscriptionMessage", "subscriptionMessage");
            FIELD_MAP.put("subscriptionRef", "subscriptionRef");
        }

        /**
         * Converts this object into a request map suitable for sending to the API.
         * Boolean values are converted to "true"/"false" strings.
         *
         * @return Map with non-null fields mapped to API keys
         */
        public Map<String, String> toRequest() {
            Map<String, String> result = new HashMap<>();
            FIELD_MAP.forEach((field, key) -> {
                try {
                    var fld = this.getClass().getDeclaredField(field);
                    fld.setAccessible(true);
                    Object value = fld.get(this);
                    if (value != null && !"".equals(value)) {
                        if (value instanceof Boolean) {
                            result.put(key, ((Boolean) value) ? "true" : "false");
                        } else {
                            result.put(key, String.valueOf(value));
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException ignored) {
                }
            });
            return result;
        }

        public long getCt() {
            return ct;
        }

        private final static Set<String> acceptedLanguages = Set.of("nl", "fr", "en", "pt", "es", "it");

        public InviteRequest setLang(String language) {
            if(acceptedLanguages.contains(language)){
                this.l = language;
            }
            return this;
        }

        public InviteRequest setIban(String iban) {
            this.iban = iban;
            return this;
        }

        public InviteRequest setBic(String bic) {
            this.bic = bic;
            return this;
        }

        public InviteRequest setMandateNumber(String mandateNumber) {
            this.mandateNumber = mandateNumber;
            return this;
        }

        public InviteRequest setCustomerNumber(String customerNumber) {
            this.customerNumber = customerNumber;
            return this;
        }

        public InviteRequest setEmail(String email) {
            this.email = email;
            return this;
        }

        public InviteRequest setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public InviteRequest setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public InviteRequest setMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public InviteRequest setAddress(String address) {
            this.address = address;
            return this;
        }

        public InviteRequest setCity(String city) {
            this.city = city;
            return this;
        }

        public InviteRequest setZip(String zip) {
            this.zip = zip;
            return this;
        }

        public InviteRequest setCountry(String country) {
            this.country = country;
            return this;
        }

        public InviteRequest setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public InviteRequest setVatno(String vatno) {
            this.vatno = vatno;
            return this;
        }

        public InviteRequest setContractNumber(String contractNumber) {
            this.contractNumber = contractNumber;
            return this;
        }

        public InviteRequest setCampaign(String campaign) {
            this.campaign = campaign;
            return this;
        }

        public InviteRequest setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public InviteRequest setExpiryd(long epoch) {
            this.ed = ed;
            return this;
        }

        public InviteRequest setToken(String token) {
            this.token = token;
            return this;
        }

        public InviteRequest setDocument(String document) {
            this.document = document;
            return this;
        }

        public InviteRequest setTransactionMessage(String transactionMessage) {
            this.transactionMessage = transactionMessage;
            return this;
        }

        public InviteRequest setTransactionRef(String transactionRef) {
            this.transactionRef = transactionRef;
            return this;
        }

        public InviteRequest setSubscriptionStart(String subscriptionStart) {
            this.subscriptionStart = subscriptionStart;
            return this;
        }

        public InviteRequest setSubscriptionRecurrence(String subscriptionRecurrence) {
            this.subscriptionRecurrence = subscriptionRecurrence;
            return this;
        }

        public InviteRequest setSubscriptionMessage(String subscriptionMessage) {
            this.subscriptionMessage = subscriptionMessage;
            return this;
        }

        public InviteRequest setSubscriptionRef(String subscriptionRef) {
            this.subscriptionRef = subscriptionRef;
            return this;
        }

        public InviteRequest setForceCheck(Boolean check) {
            this.check = check;
            return this;
        }

        public InviteRequest setRequireValidation(Boolean requireValidation) {
            this.requireValidation = requireValidation;
            return this;
        }

        public InviteRequest setReminderDays(Integer reminderDays) {
            this.reminderDays = reminderDays;
            return this;
        }

        public InviteRequest setSubscriptionStopAfter(Integer subscriptionStopAfter) {
            this.subscriptionStopAfter = subscriptionStopAfter;
            return this;
        }

        public InviteRequest setTransactionAmount(Double transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public InviteRequest setSubscriptionAmount(Double subscriptionAmount) {
            this.subscriptionAmount = subscriptionAmount;
            return this;
        }
    }

}
