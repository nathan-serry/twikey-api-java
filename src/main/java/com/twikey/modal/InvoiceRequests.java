package com.twikey.modal;

import java.util.*;

public interface InvoiceRequests {
    /**
     * CreateInvoiceRequest holds the full set of fields used to create an invoice via the Twikey API.
     */
    public class CreateInvoiceRequest {
        public static class LineItem {
            private String code, description, uom, vatcode;
            private Integer quantity;
            private Double unitprice, vatsum;

            public LineItem setCode(String code) {
                this.code = code;
                return this;
            }

            public LineItem setDescription(String description) {
                this.description = description;
                return this;
            }

            public LineItem setQuantity(Integer quantity) {
                this.quantity = quantity;
                return this;
            }

            public LineItem setUom(String uom) {
                this.uom = uom;
                return this;
            }

            public LineItem setUnitprice(Double unitprice) {
                this.unitprice = unitprice;
                return this;
            }

            public LineItem setVatcode(String vatcode) {
                this.vatcode = vatcode;
                return this;
            }

            public LineItem setVatsum(Double vatsum) {
                this.vatsum = vatsum;
                return this;
            }

            public Map<String, Object> toMap() {
                Map<String, Object> map = new HashMap<>();
                putIfNotNull(map, "code", code);
                putIfNotNull(map, "description", description);
                putIfNotNull(map, "quantity", quantity);
                putIfNotNull(map, "uom", uom);
                putIfNotNull(map, "unitprice", unitprice);
                putIfNotNull(map, "vatcode", vatcode);
                putIfNotNull(map, "vatsum", vatsum);
                return map;
            }

            private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
                if (value != null) map.put(key, value);
            }
        }

        private String id, title, remittance, ref, locale, pdf, pdfUrl, redirectUrl,
                email, relatedInvoiceNumber, cc;
        private final String number;
        private final String date;
        private final String duedate;
        private String ct;
        private final Double amount;
        private Boolean manual;
        private final DocumentRequests.Customer customer;
        private List<LineItem> lines;

        public CreateInvoiceRequest(String number, Double amount, String date, String duedate, DocumentRequests.Customer customer) {
            this.number = number;
            this.amount = amount;
            this.date = date;
            this.duedate = duedate;
            this.customer = customer;
        }

        // Fluent setters
        public CreateInvoiceRequest setId(String id) {
            this.id = id;
            return this;
        }
        public CreateInvoiceRequest setTitle(String title) {
            this.title = title;
            return this;
        }
        public CreateInvoiceRequest setRemittance(String remittance) {
            this.remittance = remittance;
            return this;
        }
        public CreateInvoiceRequest setRef(String ref) {
            this.ref = ref;
            return this;
        }
        public CreateInvoiceRequest setCt(String ct) {
            this.ct = ct;
            return this;
        }
        public CreateInvoiceRequest setLocale(String locale) {
            this.locale = locale;
            return this;
        }
        public CreateInvoiceRequest setManual(Boolean manual) {
            this.manual = manual;
            return this;
        }
        public CreateInvoiceRequest setPdf(String pdf) {
            this.pdf = pdf;
            return this;
        }
        public CreateInvoiceRequest setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
            return this;
        }
        public CreateInvoiceRequest setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }
        public CreateInvoiceRequest setEmail(String email) {
            this.email = email;
            return this;
        }
        public CreateInvoiceRequest setRelatedInvoiceNumber(String relatedInvoiceNumber) {
            this.relatedInvoiceNumber = relatedInvoiceNumber;
            return this;
        }
        public CreateInvoiceRequest setCc(String cc) {
            this.cc = cc;
            return this;
        }
        public CreateInvoiceRequest setLines(List<LineItem> lines) {
            this.lines = lines;
            return this;
        }

        /**
         * Converts this request to a Map suitable for API submission.
         */
        public Map<String, String> toRequest() {
            Map<String, String> map = new HashMap<>();
            putIfNotNull(map, "id", id);
            putIfNotNull(map, "number", number);
            putIfNotNull(map, "title", title);
            putIfNotNull(map, "remittance", remittance);
            putIfNotNull(map, "ref", ref);
            putIfNotNull(map, "ct", ct);
            putIfNotNull(map, "amount", String.valueOf(amount));
            putIfNotNull(map, "date", date);
            putIfNotNull(map, "duedate", duedate);
            putIfNotNull(map, "locale", locale);
            putIfNotNull(map, "manual", String.valueOf(manual));
            putIfNotNull(map, "pdf", pdf);
            putIfNotNull(map, "pdfUrl", pdfUrl);
            putIfNotNull(map, "redirectUrl", redirectUrl);
            putIfNotNull(map, "email", email);
            putIfNotNull(map, "relatedInvoiceNumber", relatedInvoiceNumber);
            putIfNotNull(map, "cc", cc);
            if(customer != null) {
                putIfNotNull(map, "customerNumber", customer.getCustomerNumber());
                putIfNotNull(map, "email", customer.getEmail());
                putIfNotNull(map, "firstname", customer.getFirstname());
                putIfNotNull(map, "lastname", customer.getLastname());
                putIfNotNull(map, "mobile", customer.getMobile());
                putIfNotNull(map, "address", customer.getStreet());
                putIfNotNull(map, "city", customer.getCity());
                putIfNotNull(map, "zip", customer.getZip());
                putIfNotNull(map, "country", customer.getCountry());
                putIfNotNull(map, "companyName", customer.getCompanyName());
                putIfNotNull(map, "vatno", customer.getCoc());
            }
            if (lines != null && !lines.isEmpty()) {
                List<Map<String, Object>> lineMaps = new ArrayList<>();
                for (LineItem line : lines) lineMaps.add(line.toMap());
                map.put("lines", lineMaps.toString());
            }
            return map;
        }

        private static void putIfNotNull(Map<String, String> map, String key, String value) {
            if (value != null) map.put(key, value);
        }
    }
}