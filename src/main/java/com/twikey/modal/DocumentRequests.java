package com.twikey.modal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.twikey.modal.DocumentRequests.InviteRequest.putIfNotNull;

public interface DocumentRequests {

    /**
     * Account represents a debtor account of that mandate, that is to be created
     *
     * <p>Attributes:</p>
     *
     * <ul>
     *   <li>iban (String): IBAN of the customer.</li>
     *   <li>bic (String): BIC of the bank.</li>
     * </ul>
     */
    record Account(String iban, String bic) {
    }


    /**
     * Customer represents a person or company for whom a mandate or invitation
     * will be created in Twikey.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>lastname (String): Last name of the customer.</li>
     *   <li>firstname (String): First name of the customer.</li>
     *   <li>email (String): Email address.</li>
     *   <li>lang (String): Preferred language code (e.g., "nl", "fr", "en").</li>
     *   <li>mobile (String): Mobile phone number in international format.</li>
     *   <li>street (String): Street and house number.</li>
     *   <li>city (String): City name.</li>
     *   <li>zip (String): Postal code.</li>
     *   <li>country (String): ISO country code (e.g., "BE").</li>
     *   <li>customerNumber (String): Internal customer reference ID.</li>
     *   <li>companyName (String): Company name (if applicable).</li>
     *   <li>coc (String): Company registration number.</li>
     * </ul>
     */
    class Customer {
        private String lastname, firstname, email, lang, mobile, street, city, zip, country, customerNumber, companyName, coc;

        public Customer(){}

        public Customer setLastname(String lastname) { this.lastname = lastname; return this; }
        public Customer setFirstname(String firstname) { this.firstname = firstname; return this; }
        public Customer setEmail(String email) { this.email = email; return this; }
        public Customer setLang(String lang) { this.lang = lang; return this; }
        public Customer setMobile(String mobile) { this.mobile = mobile; return this; }
        public Customer setStreet(String street) { this.street = street; return this; }
        public Customer setCity(String city) { this.city = city; return this; }
        public Customer setZip(String zip) { this.zip = zip; return this; }
        public Customer setCountry(String country) { this.country = country; return this; }
        public Customer setNumber(String ref) { this.customerNumber = ref; return this; }
        public Customer setCompanyName(String companyName) { this.companyName = companyName; return this; }
        public Customer setCoc(String coc) { this.coc = coc; return this; }

        public String getLastname() { return lastname; }
        public String getFirstname() { return firstname; }
        public String getEmail() { return email; }
        public String getLang() { return lang; }
        public String getMobile() { return mobile; }
        public String getStreet() { return street; }
        public String getCity() { return city; }
        public String getZip() { return zip; }
        public String getCountry() { return country; }
        public String getCustomerNumber() { return customerNumber; }
        public String getCompanyName() { return companyName; }
        public String getCoc() { return coc; }

        public Map<String,String> asFormParameters(){
            Map<String,String> params = new HashMap<>();
            params.put("customerNumber", getCustomerNumber());
            params.put("email", getEmail());
            params.put("firstname", getFirstname());
            params.put("lastname", getLastname());
            params.put("l", getLang());
            params.put("address", getStreet());
            params.put("city", getCity());
            params.put("zip", getZip());
            params.put("country", getCountry());
            params.put("mobile", getMobile());
            if(getCompanyName() != null){
                params.put("companyName", getCompanyName());
                params.put("coc", getCoc());
            }
            return params;
        }
    }


    /**
     * InviteRequest holds the full set of fields that can be used
     * to initiate a mandate invitation via the Twikey API.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>ct (long): Contract template ID (required).</li>
     *   <li>mandateNumber (String): Custom mandate number.</li>
     *   <li>vatno (String): VAT number (if business).</li>
     *   <li>contractNumber (String): External contract reference.</li>
     *   <li>campaign (String): Campaign identifier.</li>
     *   <li>prefix (String): Honorific title (e.g., Mr., Ms.).</li>
     *   <li>ed (String): Execution date.</li>
     *   <li>reminderDays (Integer): Days before reminder.</li>
     *   <li>sendInvite (Boolean): Send invite immediately.</li>
     *   <li>token (String): Resume token.</li>
     *   <li>requireValidation (Boolean): Whether IBAN validation is required.</li>
     *   <li>document (String): Document reference.</li>
     *   <li>transactionAmount (Double): One-off transaction amount.</li>
     *   <li>transactionMessage (String): Transaction message.</li>
     *   <li>transactionRef (String): Transaction reference.</li>
     *   <li>plan (String): Payment plan ID.</li>
     *   <li>subscriptionStart (String): Subscription start date.</li>
     *   <li>subscriptionRecurrence (String): Recurrence rule.</li>
     *   <li>subscriptionStopAfter (Integer): Stop after N cycles.</li>
     *   <li>subscriptionAmount (Double): Subscription amount.</li>
     *   <li>subscriptionMessage (String): Subscription message.</li>
     *   <li>subscriptionRef (String): Subscription reference.</li>
     *   <li>Customer fields: lastname, firstname, email, lang, mobile, street, city, zip, country, customerNumber, companyName, coc.</li>
     * </ul>
     */
    class InviteRequest<T extends InviteRequest<T>> {
        private final long ct;
        private final Customer customer;
        private Account account;
        private String l, mandateNumber, contractNumber,
                campaign, prefix, ed, token, document, transactionMessage, transactionRef,
                plan, subscriptionStart, subscriptionRecurrence, subscriptionMessage, subscriptionRef;
        private Boolean check, sendInvite, requireValidation;
        private Integer reminderDays, subscriptionStopAfter;
        private Double transactionAmount, subscriptionAmount;

        /**
         * @param ct Template
         * @param customer Optional customer (can be null)
         * @param account Optional account (can be null)
         */
        public InviteRequest(long ct, Customer customer, Account account) {
            this.ct = ct;
            this.customer = customer;
            this.account = account;
        }

        public InviteRequest(long ct, Customer customer) {
            this(ct, customer, null);
        }

        public InviteRequest(long ct) {
            this(ct, null, null);
        }

        /**
         * Convert this request to a flat Map<String,String> suitable for the API.
         */
        public Map<String, String> toRequest() {
            Map<String, String> result = new HashMap<>();
            result.put("ct", String.valueOf(ct));
            if(account != null) {
                putIfNotNull(result, "iban", account.iban());
                putIfNotNull(result, "bic", account.bic());
            }
            if(customer != null) {
                putIfNotNull(result, "customerNumber", customer.getCustomerNumber());
                putIfNotNull(result, "email", customer.getEmail());
                putIfNotNull(result, "firstname", customer.getFirstname());
                putIfNotNull(result, "lastname", customer.getLastname());
                putIfNotNull(result, "mobile", customer.getMobile());
                putIfNotNull(result, "address", customer.getStreet());
                putIfNotNull(result, "city", customer.getCity());
                putIfNotNull(result, "zip", customer.getZip());
                putIfNotNull(result, "country", customer.getCountry());
                putIfNotNull(result, "companyName", customer.getCompanyName());
                putIfNotNull(result, "vatno", customer.getCoc());
            }
            putIfNotNull(result, "l", l);
            putIfNotNull(result, "mandateNumber", mandateNumber);
            putIfNotNull(result, "contractNumber", contractNumber);
            putIfNotNull(result, "campaign", campaign);
            putIfNotNull(result, "prefix", prefix);
            putIfNotNull(result, "ed", ed);
            putIfNotNull(result, "token", token);
            putIfNotNull(result, "document", document);
            putIfNotNull(result, "transactionMessage", transactionMessage);
            putIfNotNull(result, "transactionRef", transactionRef);
            putIfNotNull(result, "plan", plan);
            if(subscriptionStart != null) {
                putIfNotNull(result, "subscriptionStart", subscriptionStart);
                putIfNotNull(result, "subscriptionRecurrence", subscriptionRecurrence);
                putIfNotNull(result, "subscriptionMessage", subscriptionMessage);
                putIfNotNull(result, "subscriptionRef", subscriptionRef);
                putIfNotNull(result, "subscriptionAmount", subscriptionAmount);
                putIfNotNull(result, "subscriptionStopAfter", subscriptionStopAfter);
            }
            putIfNotNull(result, "check", check);
            putIfNotNull(result, "sendInvite", sendInvite);
            putIfNotNull(result, "requireValidation", requireValidation);
            putIfNotNull(result, "reminderDays", reminderDays);
            putIfNotNull(result, "transactionAmount", transactionAmount);

            return result;
        }

        public static void putIfNotNull(Map<String, String> map, String key, Object value) {
            if (value != null) {
                if (value instanceof Boolean) {
                    map.put(key, ((Boolean) value) ? "true" : "false");
                } else {
                    map.put(key, String.valueOf(value));
                }
            }
        }

        private final static Set<String> acceptedLanguages = Set.of("nl", "fr", "en", "pt", "es", "it");

        public T setLang(String language) {
            if(acceptedLanguages.contains(language)){
                this.l = language;
            }
            return (T) this;
        }

        public T setMandateNumber(String mandateNumber) {
            this.mandateNumber = mandateNumber;
            return (T) this;
        }

        public T setContractNumber(String contractNumber) {
            this.contractNumber = contractNumber;
            return (T) this;
        }

        public T setCampaign(String campaign) {
            this.campaign = campaign;
            return (T) this;
        }

        public T setPrefix(String prefix) {
            this.prefix = prefix;
            return (T) this;
        }

        public T setExpiryd(long epoch) {
            this.ed = ed;
            return (T) this;
        }

        public T setToken(String token) {
            this.token = token;
            return (T) this;
        }

        public T setDocument(String document) {
            this.document = document;
            return (T) this;
        }

        public T setTransactionMessage(String transactionMessage) {
            this.transactionMessage = transactionMessage;
            return (T) this;
        }

        public T setTransactionRef(String transactionRef) {
            this.transactionRef = transactionRef;
            return (T) this;
        }

        public T setSubscriptionStart(String subscriptionStart) {
            this.subscriptionStart = subscriptionStart;
            return (T) this;
        }

        public T setSubscriptionRecurrence(String subscriptionRecurrence) {
            this.subscriptionRecurrence = subscriptionRecurrence;
            return (T) this;
        }

        public T setSubscriptionMessage(String subscriptionMessage) {
            this.subscriptionMessage = subscriptionMessage;
            return (T) this;
        }

        public T setSubscriptionRef(String subscriptionRef) {
            this.subscriptionRef = subscriptionRef;
            return (T) this;
        }

        public T setForceCheck(Boolean check) {
            this.check = check;
            return (T) this;
        }

        public T setRequireValidation(Boolean requireValidation) {
            this.requireValidation = requireValidation;
            return (T) this;
        }

        public T setReminderDays(Integer reminderDays) {
            this.reminderDays = reminderDays;
            return (T) this;
        }

        public T setSubscriptionStopAfter(Integer subscriptionStopAfter) {
            this.subscriptionStopAfter = subscriptionStopAfter;
            return (T) this;
        }

        public T setTransactionAmount(Double transactionAmount) {
            this.transactionAmount = transactionAmount;
            return (T) this;
        }

        public T setSubscriptionAmount(Double subscriptionAmount) {
            this.subscriptionAmount = subscriptionAmount;
            return (T) this;
        }
    }


    class SignRequest extends InviteRequest<SignRequest> {
        private String method;
        private String digsig;
        private String key;
        private String signDate;
        private String place;
        private Boolean bankSignature = true; // default true

        public SignRequest(long ct) {
            super(ct);
        }

        public SignRequest(long ct, Customer customer) {
            super(ct, customer);
        }

        public SignRequest(long ct, Customer customer, Account account) {
            super(ct, customer, account);
        }

        public SignRequest setMethod(String method) {
            this.method = method;
            return this;
        }

        public SignRequest setDigsig(String digsig) {
            this.digsig = digsig;
            return this;
        }

        public SignRequest setKey(String key) {
            this.key = key;
            return this;
        }

        public SignRequest setSignDate(String signDate) {
            this.signDate = signDate;
            return this;
        }

        public SignRequest setPlace(String place) {
            this.place = place;
            return this;
        }

        public SignRequest setBankSignature(Boolean bankSignature) {
            this.bankSignature = bankSignature;
            return this;
        }

        @Override
        public Map<String, String> toRequest() {
            Map<String, String> result = super.toRequest();
            putIfNotNull(result, "method", method);
            putIfNotNull(result, "digsig", digsig);
            putIfNotNull(result, "key", key);
            putIfNotNull(result, "signDate", signDate);
            putIfNotNull(result, "place", place);
            putIfNotNull(result, "bankSignature", bankSignature);
            return result;
        }
    }


    class MandateActionRequest {
        private final String mandateNumber;
        private final String type;
        private String reminder;

        /**
         * @param type one of invite, reminder, access, automaticCheck, manualCheck
         */
        public MandateActionRequest(String type,  String mandateNumber) {
            this.type = type;
            this.mandateNumber = mandateNumber;
        }

        public MandateActionRequest setReminder(int reminder) {
            if (reminder < 1 || reminder > 4) {
                throw new IllegalArgumentException("Reminder must be between 1 and 4");
            }
            this.reminder = String.valueOf(reminder);
            return this;
        }

        public Map<String, String> toRequest() {
            Map<String, String> map = new HashMap<>();
            map.put("type", type);
            map.put("mndtId", mandateNumber);
            if (reminder != null) {
                map.put("reminder", reminder);
            }
            return map;
        }
    }


    /**
     * MandateQuery represents a search request for mandates in the Twikey API.
     * <p>
     * This class allows querying contracts by IBAN, customer number, or email,
     * and supports filtering by state and pagination.
     * <p>
     * At least one of iban, customerNumber, or email is required. The state
     * parameter is optional. Pagination can be controlled via the page parameter.
     * <p>
     */
    class MandateQuery {
        private  String iban;
        private  String customerNumber;
        private  String email;
        private String state;
        private Integer page;

        private MandateQuery(String iban, String customerNuber, String email) {
            this.iban = iban;
            this.customerNumber = customerNuber;
            this.email = email;
        }
        public static MandateQuery fromIban(String iban) {return new MandateQuery(iban, null, null);}
        public static MandateQuery fromCustomerNumber(String customerNumber) {return new MandateQuery(null, customerNumber, null);}
        public static MandateQuery fromEmail(String email) {return new MandateQuery(email, null, null);}

        public MandateQuery withIban(String iban) {this.iban=iban;return this;}
        public MandateQuery withCustomerNumber(String customerNumber) {this.customerNumber=customerNumber;return this;}
        public MandateQuery withEmail(String email) {this.email=email;return this;}

        /**
         * Converts the query into a Map suitable for GET query parameters.
         *
         * @return Map of non-null query parameters
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            putIfNotNull(params, "iban", iban);
            putIfNotNull(params, "customerNumber", customerNumber);
            putIfNotNull(params, "email", email);
            putIfNotNull(params, "state", state);
            putIfNotNull(params, "page", page);
            return params;
        }

        /**
         * Filter by contract state.
         * Possible values: SIGNED, PREPARED, CANCELLED
         */
        public MandateQuery setState(String state) {
            this.state = state;
            return this;
        }

        /**
         * Set the page number for paginated results (0-based).
         */
        public MandateQuery setPage(Integer page) {
            this.page = page;
            return this;
        }
    }



}
