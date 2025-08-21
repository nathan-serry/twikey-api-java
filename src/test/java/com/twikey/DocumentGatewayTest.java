package com.twikey;

import com.twikey.callback.DocumentCallback;
import com.twikey.modal.DocumentRequests;
import com.twikey.modal.DocumentResponse;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
//from com.twikey.modal.DocumentRequests import DocumentRequests

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static com.twikey.modal.DocumentRequests.*;
import static org.junit.Assert.assertNotNull;

public class DocumentGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String ct = System.getenv("CT"); // found @ https://www.twikey.com/r/admin#/c/template

    private Customer customer;

    private Account account;

    private TwikeyClient api;

    @Before
    public void createCustomer() {
        customer = new Customer()
                .setNumber("Java-Sdk-"+System.currentTimeMillis())
                .setEmail("no-reply@example.com")
                .setFirstname("Twikey")
                .setLastname("Support")
                .setStreet("Derbystraat 43")
                .setCity("Gent")
                .setZip("9000")
                .setCountry("BE")
                .setLang("nl")
                .setMobile("32498665995");

        account = new Account("NL46ABNA8910219718","ABNANL2A");

        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testInviteMandateWithoutCustomerDetails() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InviteRequest invite = new InviteRequest(Long.parseLong(ct))
                .setForceCheck(true)
                .setReminderDays(5);
        JSONObject response = api.document().create(invite);
        assertNotNull("Invite URL", response.getString("url"));
        assertNotNull("Document Reference", response.getString("mndtId"));
    }

    @Test
    public void testInviteMandateCustomerDetails() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InviteRequest invite = new InviteRequest(Long.parseLong(ct), customer)
                .setForceCheck(true)
                .setReminderDays(5);
        JSONObject response = api.document().create(invite);
        assertNotNull("Invite URL", response.getString("url"));
        assertNotNull("Document Reference", response.getString("mndtId"));
    }

    @Test
    public void testSignMandate() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        SignRequest invite = new SignRequest(Long.parseLong(ct), SignRequest.SignMethod.IMPORT, customer, account)
                .setForceCheck(true)
                .setReminderDays(5);
        JSONObject response = api.document().sign(invite);
        assertNotNull("Document Reference", response.getString("MndtId"));
    }

    @Test
    public void testAction() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        MandateActionRequest action = new MandateActionRequest(MandateActionRequest.MandateActionType.REMINDER, "CORERECURRENTNL18166")
                .setReminder(1);
        api.document().action(action);
    }

    @Test
    public void testQuery() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        MandateQuery action = MandateQuery
                .fromCustomerNumber("customer123")
                .withIban("BE51561419613262");
        List<DocumentResponse.Document> response = api.document().query(action);
//        assertNotNull("Contracts", response.getJSONArray("Contracts"));
        System.out.println("Response: " + response);
    }

    @Test
    public void testCancel() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        SignRequest invite = new SignRequest(Long.parseLong(ct), SignRequest.SignMethod.IMPORT, customer, account)
                .setForceCheck(true)
                .setReminderDays(5);
        JSONObject response = api.document().sign(invite);
        assertNotNull("Document Reference", response.getString("MndtId"));

        api.document().cancel(response.getString("MndtId"), "hello");
    }

    @Test
    public void testFetch() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        MandateDetailRequest fetch = new MandateDetailRequest("CORERECURRENTNL18166")
                .setForce(true);
        DocumentResponse.Document response = api.document().fetch(fetch);
        assertNotNull("Document Reference", response.getMandateNumber());
    }

    @Test
    public void testUpdateMandate() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        UpdateMandateRequest update = new UpdateMandateRequest("CORERECURRENTNL18166", customer, account);
        api.document().update(update);
    }

    @Test
    public void testCustomerAccess() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        JSONObject response = api.document().customerAccess("CORERECURRENTNL18166");
        assertNotNull("Document Reference", response.getString("token"));
    }

    @Test
    public void testRetrievePdf() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        DocumentResponse.PdfResponse response = api.document().retrievePdf("CORERECURRENTNL17192");
        System.out.println(response);
    }

    @Test
    public void testUploadPdf() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        SignRequest invite = new SignRequest(Long.parseLong(ct), SignRequest.SignMethod.ITSME, customer, account)
                .setForceCheck(true)
                .setReminderDays(5);
        JSONObject response = api.document().sign(invite);
        assertNotNull("Document Reference", response.getString("MndtId"));
        UploadPdfRequest pdfRequest = new UploadPdfRequest(response.getString("MndtId"), "/Users/nathanserry/Downloads/dummy.pdf");
        api.document().uploadPdf(pdfRequest);
    }

    @Test
    public void getMandatesAndDetails() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.document().feed(new DocumentCallback() {
            @Override
            public void newDocument(JSONObject newMandate) {
                assertNotNull("New mandate", newMandate);
            }

            @Override
            public void updatedDocument(JSONObject updatedMandate) {
                assertNotNull("Updated mandate", updatedMandate);
            }

            @Override
            public void cancelledDocument(JSONObject cancelledMandate) {
                assertNotNull("Cancelled mandate", cancelledMandate);
            }
        });
    }
}
