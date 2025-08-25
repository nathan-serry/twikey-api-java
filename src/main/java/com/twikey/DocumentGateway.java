package com.twikey;

import com.twikey.callback.DocumentCallback;
import com.twikey.modal.DocumentRequests;
import com.twikey.modal.DocumentResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.twikey.TwikeyClient.getPostDataString;
import static java.time.temporal.ChronoUnit.SECONDS;

public class DocumentGateway {

    public static final String FORM_URLENCODED = "application/json";
    private final TwikeyClient twikeyClient;

    protected DocumentGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * <ul>
     * <li>iban	International Bank Account Number of the debtor</li>
     * <li>bic	Bank Identifier Code of the IBAN</li>
     * <li>mandateNumber	Mandate Identification number (if not generated)</li>
     * <li>contractNumber	The contract number which can override the one defined in the template.</li>
     * <li>campaign	Campaign to include this url in</li>
     * <li>prefix	Optional prefix to use in the url (default companyname)</li>
     * <li>check	If a mandate already exists, don't prepare a new one (based on email, customerNumber or mandatenumber and + template type(=ct))</li>
     * <li>reminderDays	Send a reminder if contract was not signed after number of days</li>
     * <li>sendInvite	Send out invite email directly</li>
     * <li>document	Add a contract in base64 format</li>
     * <li>amount	In euro for a transaction via a first payment or post signature via an SDD transaction</li>
     * <li>token	(optional) token to be returned in the exit-url (lenght &lt; 100)</li>
     * <li>requireValidation	Always start with the registration page, even with all known mandate details</li>
     * </ul>
     *
     * @param invite Class converted to map containing any of the parameters in the above table
     * @throws IOException   When no connection could be made
     * @throws TwikeyClient.UserException When Twikey returns a user error (400)
     * @return Url to redirect the customer to or to send in an email
     * @throws IOException A network error occurred
     * @throws TwikeyClient.UserException A Twikey generated user error occurred
     */
    public DocumentResponse.MandateCreationResponse create(DocumentRequests.InviteRequest invite) throws Exception, TwikeyClient.UserException {
        Map<String, String> requestMap = invite.toRequest();
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invite");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", FORM_URLENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(requestMap)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
                /* {
                  "mndtId": "COREREC01",
                  "url": "http://twikey.to/myComp/ToYG",
                  "key": "ToYG"
                } */
                return DocumentResponse.MandateCreationResponse.fromJson(response.body());
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#invite-a-customer">Twikey API - Mandate Invite</a></p>
     *
     * Same parameters as invite, but some extra might be required depending on the method
     * <ul>
     * <li><b>method (required)</b>:	Method to sign (sms/digisign/import/itsme/emachtiging/paper,...)</li>
     * <li>digsig:	Wet signature (PDF encoded as base64) required if method is digisign</li>
     * <li>key:	shortcode from the invite url. Use this parameter instead of 'mandateNumber' to directly sign a prepared mandate.</li>
     * <li>bic:	Required for methods emachtiging, iDeal and iDIn</li>
     * <li>signDate:	Date of signature (xsd:dateTime), optional for sms as it uses date of reply</li>
     * <li>place:	Place of signature</li>
     * </ul>
     *
     * @param invite Class converted to map containing any of the parameters in the above table
     * @return Url to redirect the customer to or to send in an email
     * @throws IOException A network error occurred
     * @throws TwikeyClient.UserException A Twikey generated user error occurred
     */
    public DocumentResponse.MandateCreationResponse sign(DocumentRequests.SignRequest invite) throws Exception, TwikeyClient.UserException {
        Map<String, String> requestMap = invite.toRequest();
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/sign");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(requestMap)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
                /* {
                  "mndtId": "COREREC01",
                  "url": "http://twikey.to/myComp/ToYG",
                  "key": "ToYG"
                } */
                return DocumentResponse.MandateCreationResponse.fromJson(response.body());
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * Trigger a specific action on an existing mandate.
     *
     * <p>See the official documentation:
     * <a href="https://www.twikey.com/api/#mandate-actions">Twikey API - Mandate Actions</a></p>
     *
     * <p>This endpoint allows initiating predefined actions related to a mandate,
     * such as sending an invitation or reminder, or toggling B2B validation behavior.
     * The action type must be explicitly provided in the request.</p>
     *
     * @param action  The mandate action request containing required fields:
     *                 <ul>
     *                     <li>mndtId - The unique identifier of the mandate</li>
     *                     <li>type   - The action to perform (e.g., invite, reminder)</li>
     *                 </ul>
     * @throws TwikeyClient.UserException if the API returns an error or the request fails
     */
    public void action(DocumentRequests.MandateActionRequest action) throws IOException, TwikeyClient.UserException, InterruptedException {
        Map<String, String> requestMap = action.toRequest();
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/mandate/%s/action".formatted(String.valueOf(requestMap.get("mndtId"))));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .timeout(Duration.of(10, SECONDS))
                .header("Content-Type", FORM_URLENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(requestMap)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * Retrieve contract (mandate) details by IBAN, customer number, email,
     * or a combination of query parameters.
     *
     * <p>See the official documentation:
     * <a href="https://www.twikey.com/api/#mandate-query">Twikey API - Query Mandate</a>
     *
     * <p>This endpoint allows searching for mandates based on specific identifiers.
     * The result contains a list of contracts (mandates) that match the provided parameters.</p>
     *
     * @param action  The query parameters such as:
     *                <ul>
     *                    <li>iban</li>
     *                    <li>customerNumber</li>
     *                    <li>email</li>
     *                    <li>state</li>
     *                    <li>page</li>
     *                </ul>
     *                <p>At least one of <code>iban</code>, <code>customerNumber</code>,
     *                or <code>email</code> is required.</p>
     *
     * @return A list of {@link DocumentResponse.Document} objects containing mandate details
     *         that match the query.
     *
     * @throws Exception              if the request fails
     * @throws TwikeyClient.UserException if the API returns a user-related error
     */
    public List<DocumentResponse.Document> query(DocumentRequests.MandateQuery action) throws Exception, TwikeyClient.UserException {
        Map<String, String> requestMap = action.toRequest();
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/mandate/query?"+getPostDataString(requestMap));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .headers("Content-Type", FORM_URLENCODED)
                .headers("User-Agent", twikeyClient.getUserAgent())
                .headers("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(new JSONTokener(response.body()));
        if (response.statusCode() == 200) {
            return DocumentResponse.Document.fromQuery(json);
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    public void cancel(String mandateNumber, String reason) throws IOException, TwikeyClient.UserException, InterruptedException {
        this.cancel(mandateNumber, reason, false);
    }

    /**
     * See <a href="https://www.twikey.com/api/#cancel-agreements">Twikey API - Cancel Mandate</a>
     * <p>
     * Sends a DELETE request to cancel a mandate on the Twikey API.
     * <p>
     * This method allows the creditor to cancel/delete a resource by providing the unique
     * ID and a reason for cancellation. This ensures Twikey’s records are
     * updated and, if applicable, forwards the cancellation to the debtor's bank.
     * Cancellation can originate from the creditor, the creditor’s bank, or the debtor’s bank.
     *
     * @param mandateNumber The unique identifier of the mandate to cancel (mndtId).
     * @param reason The reason for cancelling the mandate. Can be a custom message or an R-message code.
     * @param notify When set to true, the customer will be notified by email. (optional)
     *
     * @throws IOException If a network error occurs during the request.
     * @throws TwikeyClient.UserException If the API returns an error.
     * @throws InterruptedException If the request is interrupted.
     */
    public void cancel(String mandateNumber, String reason, boolean notify) throws IOException, TwikeyClient.UserException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl(String.format("/mandate?mndtId=%s&rsn=%s&notify=%s",
                URLEncoder.encode(mandateNumber, StandardCharsets.UTF_8),
                URLEncoder.encode(reason, StandardCharsets.UTF_8),
                notify));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", FORM_URLENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#fetch-mandate-details">Twikey API - Fetch Mandate</a>
     * <p>
     * Retrieves the details of a specific mandate by ID
     * <p>
     * This method queries the Twikey API for the latest details related to the mandate, invoice, etc. for the
     * provided identifier. Typically used for querying status based on ID, reference, or mandate.
     *
     * @param fetch An object representing information for identifying the mandate.
     *
     * @return A structured response object representing the server’s reply.
     *
     * @throws Exception If the API call fails or the identifier is invalid.
     * @throws TwikeyClient.UserException If the API returns an error.
     */
    public DocumentResponse.Document fetch(DocumentRequests.MandateDetailRequest fetch) throws Exception, TwikeyClient.UserException {
        Map<String, String> requestMap = fetch.toRequest();
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/mandate/detail?" + getPostDataString(requestMap));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .headers("Content-Type", FORM_URLENCODED)
                .headers("User-Agent", twikeyClient.getUserAgent())
                .headers("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            if (response.headers().firstValue("x-state").isPresent()) {
                return DocumentResponse.Document.fromJson(json, response.headers().firstValue("x-state").get());
            }
            else  {
                return DocumentResponse.Document.fromJson(json, null);
            }
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#update-mandate-details">Twikey API - Update Mandate</a>
     * <p>
     * Send a POST request to update existing mandate details.
     * <p>
     * This endpoint allows modifying mandate information such as customer data,
     * mandate configuration, or linked references. Only provide parameters for fields you
     * wish to update. Some fields may have special behavior or limitations depending on the object state.
     *
     * @param update An object representing the payload to send.
     *
     * @throws IOException If there is an error during the request.
     * @throws TwikeyClient.UserException If the API returns an error.
     * @throws InterruptedException If the request is interrupted.
     */
    public void update(DocumentRequests.UpdateMandateRequest update) throws IOException, TwikeyClient.UserException, InterruptedException {
        Map<String, String> requestMap = update.toRequest();
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/mandate/update");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .timeout(Duration.of(10, SECONDS))
                .header("Content-Type", FORM_URLENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(requestMap)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#customer-access">Twikey API - Customer Mandate Access</a>
     * <p>
     * Create a new customer access link via a POST request to the API.
     *
     * @param mandateNumber An object representing the payload to send.
     *
     * @return CustomerAccessResponse A structured response object representing the server’s reply.
     *
     * @throws IOException If there is an error during the request.
     * @throws TwikeyClient.UserException If the API returns an error.
     * @throws InterruptedException If the request is interrupted.
     */
    public DocumentResponse.CustomerAccessResponse customerAccess(String mandateNumber) throws IOException, TwikeyClient.UserException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/customeraccess");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", FORM_URLENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString("mndtId=%s".formatted(mandateNumber)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return DocumentResponse.CustomerAccessResponse.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#retrieve-pdf">Twikey API - Retrieve Mandate Pdf</a>
     * <p>
     * Retrieve the PDF of a mandate via a GET request to the API.
     *
     * @param mandateNumber A unique identifier for a mandate.
     *
     * @return PdfResponse A structured response object representing the server’s reply.
     *
     * @throws IOException If there is an error during the request.
     * @throws TwikeyClient.UserException If the API returns an error.
     * @throws InterruptedException If the request is interrupted.
     */
    public DocumentResponse.PdfResponse retrievePdf(String mandateNumber) throws IOException, TwikeyClient.UserException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/mandate/pdf?mndtId=" + mandateNumber);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .headers("Content-Type", FORM_URLENCODED)
                .headers("User-Agent", twikeyClient.getUserAgent())
                .headers("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() == 200) {
            String disposition = response.headers().firstValue("content-disposition").get();
            String[] parts = disposition.split("=");
            String filename = null;
            if (parts.length == 2) {
                filename = parts[1].trim().replace("\"", "");
            }

            return new DocumentResponse.PdfResponse(response.body(), filename);
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#upload-pdf">Twikey API - Upload Mandate Pdf</a>
     * <p>
     * Upload a new mandate via a PDF using a POST request to the API.
     *
     * <p>This endpoint allows signing a mandate by sending the PDF document.
     * The provided request object should include all necessary information
     * such as the file to be uploaded and optional metadata.</p>
     *
     * @param pdfRequest An object representing the payload for the request containing the file.
     *
     * @throws IOException If there is an error during the request.
     * @throws TwikeyClient.UserException If the API returns an error.
     * @throws InterruptedException If the request is interrupted.
     */
    public void uploadPdf(DocumentRequests.UploadPdfRequest pdfRequest) throws IOException, TwikeyClient.UserException, InterruptedException {
        URL myurl = twikeyClient.getUrl("/mandate/pdf?mndtId=%s&bankSignature=%s".formatted(pdfRequest.mndtId(), pdfRequest.bankSignature()));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .headers("Content-Type", "application/pdf")
                .headers("User-Agent", twikeyClient.getUserAgent())
                .headers("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of(pdfRequest.pdfPath())))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * Get updates about all mandates (new/updated/cancelled)
     *
     * @param mandateCallback Callback for every change
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(DocumentCallback mandateCallback) throws Exception, TwikeyClient.UserException {
        URL myurl = twikeyClient.getUrl("/mandate");
        boolean isEmpty;
        do{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(myurl.toString()))
                    .headers("Content-Type", FORM_URLENCODED)
                    .headers("User-Agent", twikeyClient.getUserAgent())
                    .headers("Authorization", twikeyClient.getSessionToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
            con.setRequestProperty("Content-Type", FORM_URLENCODED);
            con.setRequestProperty("User-Agent", twikeyClient.getUserAgent());
            con.setRequestProperty("Authorization", twikeyClient.getSessionToken());
            int responseCode = response.statusCode();

            if (responseCode == 200) {
                JSONObject json = new JSONObject(new JSONTokener(response.body()));

                JSONArray messagesArr = json.getJSONArray("Messages");
                isEmpty = messagesArr.isEmpty();
                if (!isEmpty) {
                    for (int i = 0; i < messagesArr.length(); i++) {
                        JSONObject obj = messagesArr.getJSONObject(i);
                        if (obj.has("CxlRsn")) {
                            mandateCallback.cancelledDocument(obj.getString("OrgnlMndtId"), obj.getJSONObject("CxlRsn").getString("Rsn"), obj.getJSONObject("CxlRsn").getJSONObject("Orgtr").getJSONObject("CtctDtls").getString("EmailAdr"), obj.getString("EvtTime"));
                        } else if (obj.has("AmdmntRsn")) {
                            DocumentResponse.Document document = DocumentResponse.Document.fromJson(obj, null);
                            mandateCallback.updatedDocument(document, obj.getString("OrgnlMndtId"), obj.getJSONObject("AmdmntRsn").getString("Rsn"), obj.getJSONObject("AmdmntRsn").getJSONObject("Orgtr").getJSONObject("CtctDtls").getString("EmailAdr"), obj.getString("EvtTime"));
                        } else {
                            DocumentResponse.Document document = DocumentResponse.Document.fromJson(obj, null);
                            mandateCallback.newDocument(document, obj.getString("EvtTime"));
                        }
                    }
                }
            } else {
                String apiError = con.getHeaderField("ApiError");
                throw new TwikeyClient.UserException(apiError);
            }
        } while (!isEmpty);
    }
}
