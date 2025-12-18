package services;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class BankApiService {

    private static final String BASE_URL = "http://147.27.70.44:3020";
    private final Gson gson = new Gson();

    // --- 1. SEPA TRANSFER (Εντός ΕΕ) ---
    public ApiResponse sendSepaTransfer(double amount, String receiverName, String receiverIban, String bic, String bankName) throws Exception {
        String endpoint = "/transfer/sepa";
        
        // Δημιουργία του αντικειμένου Request σύμφωνα με το PDF
        SepaRequest req = new SepaRequest();
        req.amount = amount;
        req.creditor = new Creditor(receiverName, receiverIban);
        req.creditorBank = new CreditorBank(bic, bankName);
        req.execution = new Execution("2025-12-18", "SHA"); // Default τιμές

        String jsonInputString = gson.toJson(req);
        return sendPostRequest(endpoint, jsonInputString);
    }

    // --- 2. SWIFT TRANSFER (Εκτός ΕΕ) ---
    public ApiResponse sendSwiftTransfer(double amount, String receiverName, String receiverIban, String bic, String bankName, String address, String country) throws Exception {
        String endpoint = "/transfer/swift";
        
        SwiftRequest req = new SwiftRequest();
        req.amount = amount;
        req.creditor = new Creditor(receiverName, receiverIban);
        req.creditorBank = new SwiftCreditorBank(bic, bankName, address, country);
        req.execution = new Execution("2025-12-18", "SHA");

        String jsonInputString = gson.toJson(req);
        return sendPostRequest(endpoint, jsonInputString);
    }

    // --- HELPER: SEND POST ---
    private ApiResponse sendPostRequest(String endpoint, String jsonBody) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        // Αποστολή JSON
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Ανάγνωση Απάντησης
        int code = con.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse response JSON
        ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
        
        if (code != 200) {
            throw new Exception("Transfer Failed: " + (apiResponse != null ? apiResponse.message : "Unknown Error"));
        }
        
        return apiResponse;
    }

    // --- INNER CLASSES FOR JSON MODELS (Βάσει του PDF) ---
    
    // Response
    public static class ApiResponse {
        public String status;
        public String message;
        public String transaction_id;
    }

    // Request Parts
    private static class SepaRequest {
        double amount;
        Creditor creditor;
        CreditorBank creditorBank;
        Execution execution;
    }

    private static class SwiftRequest {
        double amount;
        Creditor creditor;
        SwiftCreditorBank creditorBank;
        Execution execution;
    }

    private static class Creditor {
        String name;
        String iban;
        public Creditor(String name, String iban) { this.name = name; this.iban = iban; }
    }

    private static class CreditorBank {
        String bic;
        String name;
        public CreditorBank(String bic, String name) { this.bic = bic; this.name = name; }
    }
    
    private static class SwiftCreditorBank extends CreditorBank {
        String address;
        String country;
        public SwiftCreditorBank(String bic, String name, String address, String country) {
            super(bic, name);
            this.address = address;
            this.country = country;
        }
    }

    private static class Execution {
        String requestedDate;
        String charges; // SHA, BEN, OUR
        public Execution(String date, String charges) { this.requestedDate = date; this.charges = charges; }
    }
}