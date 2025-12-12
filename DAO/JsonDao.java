package DAO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

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

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
             .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DATETIME_FORMATTER)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), DATETIME_FORMATTER))
             .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DATE_FORMATTER)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString(), DATE_FORMATTER))
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
            return new DatabaseData();
        }
    }
}