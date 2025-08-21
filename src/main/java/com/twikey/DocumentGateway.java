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

    public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
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
    public JSONObject create(DocumentRequests.InviteRequest invite) throws IOException, TwikeyClient.UserException, InterruptedException {
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
                return new JSONObject(new JSONTokener(response.body()));
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
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
    public JSONObject sign(DocumentRequests.SignRequest invite) throws IOException, TwikeyClient.UserException, InterruptedException {
        Map<String, String> requestMap = invite.toRequest();
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/sign");
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
                return new JSONObject(new JSONTokener(response.body()));
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
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
     * TODO
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
     * TODO
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
     * TODO
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
     * TODO
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
     * TODO
     */
    public JSONObject customerAccess(String mandateNumber) throws IOException, TwikeyClient.UserException, InterruptedException {
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
            return new JSONObject(new JSONTokener(response.body()));
        } else {
            String apiError = response.headers()
                    .firstValue("apierror")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
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
     * TODO
     */
    public void uploadPdf(DocumentRequests.UploadPdfRequest pdfRequest) throws IOException, TwikeyClient.UserException, InterruptedException {
        URL myurl = twikeyClient.getUrl("/mandate/pdf?mndtId=%s&bankSignature=%s".formatted(pdfRequest.mndtId(), pdfRequest.bankSignature()));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .headers("Content-Type", FORM_URLENCODED)
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
    public void feed(DocumentCallback mandateCallback) throws IOException, TwikeyClient.UserException, InterruptedException {
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
                            mandateCallback.cancelledDocument(obj);
                        } else if (obj.has("AmdmntRsn")) {
                            mandateCallback.updatedDocument(obj);
                        } else {
                            mandateCallback.newDocument(obj);
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
