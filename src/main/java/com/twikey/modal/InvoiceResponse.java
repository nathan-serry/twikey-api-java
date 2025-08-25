package com.twikey.modal;


import org.json.JSONObject;

import java.util.List;

public interface InvoiceResponse {
    /**
     * Invoice represents the response from the Twikey API when an invoice is created.
     */
    public class Invoice {

        private String id;
        private String number;
        private String title;
        private String remittance;
        private String ref;
        private Integer ct;
        private Double amount;
        private String date;
        private String duedate;
        private String state;
        private String url;

        // Optional / Extended fields
        private String lastpayment;
        private JSONObject meta;
        private JSONObject customer;

        // --- Getters ---
        public String getId() { return id; }
        public String getNumber() { return number; }
        public String getTitle() { return title; }
        public String getRemittance() { return remittance; }
        public String getRef() { return ref; }
        public Integer getCt() { return ct; }
        public Double getAmount() { return amount; }
        public String getDate() { return date; }
        public String getDuedate() { return duedate; }
        public String getState() { return state; }
        public String getUrl() { return url; }
        public String getLastpayment() { return lastpayment; }
        public JSONObject getMeta() { return meta; }
        public JSONObject getCustomer() { return customer; }

        /**
         * Factory method to build an Invoice object from JSON.
         *
         * @param json JSON object containing the invoice fields.
         * @return Invoice instance populated with API response values.
         */
        public static Invoice fromJson(JSONObject json) {
            Invoice invoice = new Invoice();

            invoice.id = json.optString("id");
            invoice.number = json.optString("number");
            invoice.title = json.optString("title");
            invoice.remittance = json.optString("remittance");
            invoice.ref = json.optString("ref");
            invoice.ct = json.has("ct") ? json.getInt("ct") : null;
            invoice.amount = json.has("amount") ? json.getDouble("amount") : null;
            invoice.date = json.optString("date");
            invoice.duedate = json.optString("duedate");
            invoice.state = json.optString("state");
            invoice.url = json.optString("url");

            // Optional fields
            invoice.lastpayment = json.optString("lastpayment", null);
            invoice.meta = json.has("meta") ? json.getJSONObject("meta") : null;
            invoice.customer = json.has("customer") ? json.getJSONObject("customer") : null;

            return invoice;
        }

        @Override
        public String toString() {
            return "Invoice {" +
                    "id='" + id + '\'' +
                    ", number='" + number + '\'' +
                    ", title='" + title + '\'' +
                    ", remittance='" + remittance + '\'' +
                    ", ref='" + ref + '\'' +
                    ", ct=" + ct +
                    ", amount=" + amount +
                    ", date='" + date + '\'' +
                    ", duedate='" + duedate + '\'' +
                    ", state='" + state + '\'' +
                    ", url='" + url + '\'' +
                    ", lastpayment='" + lastpayment + '\'' +
                    ", meta=" + meta +
                    ", customer=" + customer +
                    '}';
        }
    }


}