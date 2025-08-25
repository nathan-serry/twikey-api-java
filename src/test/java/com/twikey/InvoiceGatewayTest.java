package com.twikey;

import com.twikey.modal.DocumentRequests;
import com.twikey.modal.InvoiceRequests;
import com.twikey.modal.InvoiceResponse;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNotNull;

public class InvoiceGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String ct = System.getenv("CT"); // found @ https://www.twikey.com/r/admin#/c/template

    private DocumentRequests.Customer customer;

    private TwikeyClient api;

    @Before
    public void createCustomer() {
        customer = new DocumentRequests.Customer()
                .setNumber("customerNum123")
                .setEmail("no-reply@example.com")
                .setFirstname("Twikey")
                .setLastname("Support")
                .setStreet("Derbystraat 43")
                .setCity("Gent")
                .setZip("9000")
                .setCountry("BE")
                .setLang("nl")
                .setMobile("32498665995");

        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testCreateInvoice() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InvoiceRequests.CreateInvoiceRequest request = new InvoiceRequests.CreateInvoiceRequest("Inv-%s".formatted("Java-Sdk-" + System.currentTimeMillis()), 100.0, LocalDate.now().toString(), LocalDate.now().plusMonths(1).toString(), customer);
        InvoiceResponse.Invoice invoiceResponse = api.invoice().create(request);
        assertNotNull("Invoice Id", invoiceResponse.getId());
        assertNotNull("Invoice Url", invoiceResponse.getUrl());
        System.out.println(invoiceResponse);
    }

    @Test
    public void testUpdateInvoice() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InvoiceRequests.UpdateInvoiceRequest updateRequest = new InvoiceRequests.UpdateInvoiceRequest("58073359-7fd0-4683-a60f-8c08096a189e", "2025-09-01", "2025-09-08")
                .setTitle("Invoice August");
        InvoiceResponse.Invoice response = api.invoice().update(updateRequest);
        assertNotNull("Invoice Id", response.getId());
        assertNotNull("Invoice Url", response.getUrl());
    }

    @Test
    public void testDeleteInvoice() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InvoiceRequests.CreateInvoiceRequest createRequest = new InvoiceRequests.CreateInvoiceRequest("Inv-%s".formatted("Java-Sdk-" + System.currentTimeMillis()), 100.0, LocalDate.now().toString(), LocalDate.now().plusMonths(1).toString(), customer);
        InvoiceResponse.Invoice invoiceResponse = api.invoice().create(createRequest);

        api.invoice().delete(invoiceResponse.getId());
    }

    @Test
    public void testInvoiceDetails() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InvoiceRequests.InvoiceDetailRequest request = new InvoiceRequests.InvoiceDetailRequest("58073359-7fd0-4683-a60f-8c08096a189e")
                .includeCustomer(true)
                .includeMeta(true)
                .includeLastPayment(true);
        InvoiceResponse.Invoice response = api.invoice().details(request);
        assertNotNull("Invoice Id", response.getId());
    }

    @Test
    public void testInvoiceAction() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InvoiceRequests.InvoiceActionRequest request = InvoiceRequests.InvoiceActionRequest.paymentPlan("58073359-7fd0-4683-a60f-8c08096a189e", 0.5, 7.0, 6, "CORERECURRENTNL17291");
        api.invoice().action(request);
    }

    @Test
    public void testUBLUpload() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InvoiceRequests.UblUploadRequest request = new InvoiceRequests.UblUploadRequest("/Users/nathanserry/Downloads/Inv-1752246605_ubl (1).xml");
        InvoiceResponse.Invoice repsonse = api.invoice().UBL(request);
        assertNotNull(repsonse);
    }

    @Test
    public void testBatchCreation() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        List<InvoiceRequests.CreateInvoiceRequest> invoices = IntStream.range(0, 5)
                .mapToObj(i -> new InvoiceRequests.CreateInvoiceRequest(
                        "Inv-" + (System.currentTimeMillis() / 1000 + i), // invoice number
                        100.0, // amount
                        LocalDate.now().toString(), // title
                        LocalDate.now().plusMonths(1).toString(), // title
                        customer
                ))
                .toList();
        InvoiceRequests.BulkInvoiceRequest request = new InvoiceRequests.BulkInvoiceRequest(invoices);
        JSONObject repsonse = api.invoice().createBatch(request);
        assertNotNull(repsonse);
    }

    @Test
    public void testBatchDetails() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        JSONObject response = api.invoice().batchDetails("bulk-inv-150-KERuysJl7Q174d");
        assertNotNull(response);
    }

    @Test
    public void getInvoicesAndDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.invoice().feed(updatedInvoice -> assertNotNull("Updated invoice", updatedInvoice), "meta");
        api.invoice().feed(updatedInvoice -> assertNotNull("Updated invoice", updatedInvoice));
    }
}
