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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonDao {

     private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

     private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(FORMATTER)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), FORMATTER))
            .create();

 
     private static <T> void saveList(List<T> list, String filename) {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            System.out.println("Error saving " + filename + ": " + e.getMessage());
        }
    }

     private static <T> List<T> loadList(String filename, Type typeToken) {
        File file = new File(filename);
        if (!file.exists()) return new ArrayList<>(); 
        try (Reader reader = new FileReader(filename)) {
            return gson.fromJson(reader, typeToken);
        } catch (IOException e) {
            System.out.println("Error loading " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

 
     public void saveCustomers(List<Customer> customers) {
        saveList(customers, "customers.json");
    }

    public List<Customer> loadCustomers() {
        return loadList("customers.json", new TypeToken<List<Customer>>(){}.getType());
    }

     public void saveAdmins(List<Admin> admins) {
        saveList(admins, "admins.json");
    }

    public List<Admin> loadAdmins() {
        return loadList("admins.json", new TypeToken<List<Admin>>(){}.getType());
    }

     public void saveAccounts(List<Account> accounts) {
        saveList(accounts, "accounts.json");
    }

    public List<Account> loadAccounts() {
        return loadList("accounts.json", new TypeToken<List<Account>>(){}.getType());
    }

    // 4. BILLS
    public void saveBills(List<Bill> bills) {
        saveList(bills, "bills.json");
    }

    public List<Bill> loadBills() {
        return loadList("bills.json", new TypeToken<List<Bill>>(){}.getType());
    }

    // 5. STANDING ORDERS
    public void saveStandingOrders(List<StandingOrder> orders) {
        saveList(orders, "standing_orders.json");
    }

    public List<StandingOrder> loadStandingOrders() {
        return loadList("standing_orders.json", new TypeToken<List<StandingOrder>>(){}.getType());
    }
}