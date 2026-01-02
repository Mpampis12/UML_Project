package DAO;

import com.google.gson.*;
import model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonDao {

    private static final String FILE_PATH = "DAO/Database.json";  

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --- CUSTOM ADAPTER ΓΙΑ ΤΟΝ CUSTOMER ---
    // Αυτό επιτρέπει στο GSON να καταλαβαίνει πότε να φτιάχνει Individual και πότε Business
    private static final JsonSerializer<Customer> customerSerializer = (src, typeOfSrc, context) -> {
        JsonObject jsonObject = context.serialize(src, src.getClass()).getAsJsonObject();
        // Προσθέτουμε ένα πεδίο "type" για να ξέρουμε τι είναι όταν το ξαναδιαβάσουμε
        jsonObject.addProperty("type", src instanceof Business ? "BUSINESS" : "INDIVIDUAL");
        return jsonObject;
    };

    private static final JsonDeserializer<Customer> customerDeserializer = (json, typeOfT, context) -> {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = "INDIVIDUAL"; // Default για συμβατότητα με τα παλιά δεδομένα

        // 1. Έλεγχος για το νέο πεδίο "type"
        if (jsonObject.has("type")) {
            type = jsonObject.get("type").getAsString();
        } 
        // 2. Έλεγχος για το παλιό πεδίο "userRoleString" (Legacy Support)
        else if (jsonObject.has("userRoleString")) {
            String role = jsonObject.get("userRoleString").getAsString();
            if ("BUSINESS".equals(role)) type = "BUSINESS";
        }

        // Δημιουργία της σωστής κλάσης
        if ("BUSINESS".equals(type)) {
            return context.deserialize(json, Business.class);
        } else {
            return context.deserialize(json, Individual.class);
        }
    };
    // ----------------------------------------

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            // Εγγραφή των adapters για ημερομηνίες
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DATETIME_FORMATTER)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), DATETIME_FORMATTER))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DATE_FORMATTER)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString(), DATE_FORMATTER))
            
            // Εγγραφή των adapters για τον Πολυμορφισμό του Customer
            .registerTypeAdapter(Customer.class, customerSerializer)
            .registerTypeAdapter(Customer.class, customerDeserializer)
            
            .create();

    
    public static class DatabaseData {
        public List<Customer> customers = new ArrayList<>();
        public List<Admin> admins = new ArrayList<>();
        public List<Account> accounts = new ArrayList<>();
        public List<Bill> bills = new ArrayList<>();
        public List<StandingOrder> standingOrders = new ArrayList<>();
    }

 
    public void saveDatabase(DatabaseData data) {
        try {
            File directory = new File("DAO");
            if (!directory.exists()) {
                directory.mkdir();
            }

            try (Writer writer = new FileWriter(FILE_PATH)) {
                gson.toJson(data, writer);
                System.out.println("All data saved to " + FILE_PATH);
            }
        } catch (IOException e) {
            System.out.println("Error saving database: " + e.getMessage());
            e.printStackTrace();
        }
    }

  
    public DatabaseData loadDatabase() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("Database file not found. Creating new empty database.");
            return new DatabaseData(); 
        }

        try (Reader reader = new FileReader(FILE_PATH)) {
            DatabaseData data = gson.fromJson(reader, DatabaseData.class);
            if (data == null) return new DatabaseData();
            return data;
        } catch (IOException e) {
            System.out.println("Error loading database: " + e.getMessage());
            e.printStackTrace();
            return new DatabaseData(); // Επιστροφή άδειας βάσης σε περίπτωση λάθους
        }
    }
}