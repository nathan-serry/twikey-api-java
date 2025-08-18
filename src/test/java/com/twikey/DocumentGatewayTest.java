package com.twikey;

import com.twikey.callback.DocumentCallback;
import com.twikey.modal.DocumentRequests;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
//from com.twikey.modal.DocumentRequests import DocumentRequests

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class DocumentGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String mndtId = System.getenv("MANDATE_NUMBER");

    private final String ct = System.getenv("CT"); // found @ https://www.twikey.com/r/admin#/c/template

    private DocumentRequests.Customer customer;

    private DocumentRequests.Account account;

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

        account = new DocumentRequests.Account("NL46ABNA8910219718","ABNANL2A");

        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testInviteMandateWithoutCustomerDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        DocumentRequests.InviteRequest invite = new DocumentRequests.InviteRequest(69, customer)
                .setForceCheck(true)
                .setReminderDays(5);

        Map<String, String> requestMap = invite.toRequest();
        JSONObject response = api.document().create(requestMap);
        assertNotNull("Invite URL", response.getString("url"));
        assertNotNull("Document Reference", response.getString("mndtId"));
        System.out.println(response.toString());
    }

    @Test
    public void testInviteMandateCustomerDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        DocumentRequests.InviteRequest invite = new DocumentRequests.InviteRequest(69, customer)
                .setForceCheck(true)
                .setReminderDays(5);

        Map<String, String> requestMap = invite.toRequest();
        JSONObject response = api.document().create(requestMap);
        assertNotNull("Invite URL", response.getString("url"));
        assertNotNull("Document Reference", response.getString("mndtId"));
    }

    @Test
    public void testSignMandate() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        DocumentRequests.SignRequest invite = new DocumentRequests.SignRequest(69, customer, account)
                .setMethod("import")
                .setForceCheck(true)
                .setReminderDays(5);

        Map<String, String> requestMap = invite.toRequest();
        JSONObject response = api.document().sign(requestMap);
        assertNotNull("Document Reference", response.getString("MndtId"));
    }

    @Test
    public void testAction() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        DocumentRequests.MandateActionRequest action = new DocumentRequests.MandateActionRequest("reminder", "GAS583")
                .setReminder(1);

        Map<String, String> requestMap = action.toRequest();
        api.document().action(requestMap);
    }

    @Test
    public void testQuery() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        DocumentRequests.MandateQuery action = DocumentRequests.MandateQuery
                .fromCustomerNumber("customerNum123")
                .withIban("NL46ABNA8910219718");
        Map<String, String> requestMap = action.toRequest();
        JSONObject response = api.document().query(requestMap);
        assertNotNull("Contracts", response.getString("Contracts"));
    }

    @Test
    public void testCancel() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.document().cancel("GAS583", "hello");
    }


    @Test
    public void getMandatesAndDetails() throws IOException, TwikeyClient.UserException {
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
