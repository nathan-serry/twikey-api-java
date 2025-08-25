package com.twikey;

import com.twikey.callback.InvoiceCallback;
import com.twikey.modal.InvoiceRequests;
import com.twikey.modal.InvoiceResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Map;

import static com.twikey.TwikeyClient.getPostDataString;

public class InvoiceGateway {

    private final TwikeyClient twikeyClient;

    protected InvoiceGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * @param create A map containing all the information to create that invoice
     * @return jsonobject <pre>{
     *                       "id": "fec44175-b4fe-414c-92aa-9d0a7dd0dbf2",
     *                       "number": "Inv20200001",
     *                       "title": "Invoice July",
     *                       "ct": 1988,
     *                       "amount": "100.00",
     *                       "date": "2020-01-31",
     *                       "duedate": "2020-02-28",
     *                       "status": "BOOKED",
     *                       "manual": true,
     *                       "url": "https://yourpage.beta.twikey.com/invoice.html?fec44175-b4fe-414c-92aa-9d0a7dd0dbf2"
     *                   }</pre>
     * @throws IOException   When no connection could be made
     * @throws com.twikey.TwikeyClient.UserException When Twikey returns a user error (400)
     */
    public InvoiceResponse.Invoice create(InvoiceRequests.CreateInvoiceRequest create) throws IOException, TwikeyClient.UserException, InterruptedException {
        JSONObject requestMap = create.toRequest();

        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invoice");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", "application/json")
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(requestMap)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return InvoiceResponse.Invoice.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public InvoiceResponse.Invoice update(InvoiceRequests.UpdateInvoiceRequest update) throws IOException, TwikeyClient.UserException, InterruptedException {
        JSONObject requestMap = update.toRequest();

        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invoice/%s".formatted(requestMap.get("id")));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", "application/json")
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .PUT(HttpRequest.BodyPublishers.ofString(String.valueOf(requestMap)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return InvoiceResponse.Invoice.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public void delete(String delete) throws IOException, TwikeyClient.UserException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invoice/%s".formatted(delete));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", "application/json")
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public InvoiceResponse.Invoice details(InvoiceRequests.InvoiceDetailRequest details) throws IOException, TwikeyClient.UserException, InterruptedException {
        Map<String, String> params = details.toRequest();

        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invoice/%s?%s".formatted(params.get("invoice"), params.get("include")));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", "application/json")
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return InvoiceResponse.Invoice.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public void action(InvoiceRequests.InvoiceActionRequest action) throws IOException, TwikeyClient.UserException, InterruptedException {
        Map<String, String> params = action.toRequest();

        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invoice/%s/action".formatted(params.get("id")));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(params)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public InvoiceResponse.Invoice UBL(InvoiceRequests.UblUploadRequest Ubl) throws IOException, TwikeyClient.UserException, InterruptedException {
        Map<String, String> headers = Ubl.toHeaders();

        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invoice/ubl");
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of(Ubl.getXmlPath())));
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                            builder.header(entry.getKey(), entry.getValue());
                        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return InvoiceResponse.Invoice.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    public JSONObject createBatch(InvoiceRequests.BulkInvoiceRequest batch) throws IOException, TwikeyClient.UserException, InterruptedException {
        JSONArray jsonArray = batch.toRequest();
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invoice/bulk");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", "application/json")
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(jsonArray)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new JSONObject(new JSONTokener(response.body()));
        }  else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }

    public JSONObject batchDetails(String batchId) throws IOException, TwikeyClient.UserException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URL myurl = twikeyClient.getUrl("/invoice/bulk?batchId=%s".formatted(batchId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(myurl.toString()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new JSONObject(new JSONTokener(response.body()));
        }  else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse(null);
            throw new TwikeyClient.UserException(apiError);
        }
    }


    /**
     * Get updates about all mandates (new/updated/cancelled)
     *
     * @param invoiceCallback Callback for every change
     * @param sideloads items to include in the sideloading @link <a href="https://www.twikey.com/api/#invoice-feed">www.twikey.com/api/#invoice-feed</a>
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(InvoiceCallback invoiceCallback,String... sideloads) throws IOException, TwikeyClient.UserException {

        URL myurl = twikeyClient.getUrl("/invoice",sideloads);
        boolean isEmpty;
        do {
            HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("User-Agent", twikeyClient.getUserAgent());
            con.setRequestProperty("Authorization", twikeyClient.getSessionToken());

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    JSONObject json = new JSONObject(new JSONTokener(br));

                    JSONArray invoicesArr = json.getJSONArray("Invoices");
                    isEmpty = invoicesArr.isEmpty();
                    if (!invoicesArr.isEmpty()) {
                        for (int i = 0; i < invoicesArr.length(); i++) {
                            JSONObject obj = invoicesArr.getJSONObject(i);
                            invoiceCallback.invoice(obj);
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
