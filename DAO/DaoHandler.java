package DAO;

import model.*;
import services.BankSystem;
import java.util.ArrayList;
import java.util.List;

public class DaoHandler {

    private JsonDao jsonDao;

    public DaoHandler() {
        this.jsonDao = new JsonDao();
    }

    public void saveAllData() {
        BankSystem bank = BankSystem.getInstance();
        
       
        JsonDao.DatabaseData dbData = new JsonDao.DatabaseData();
 
        List<User> allUsers = bank.getUserManager().getUsers();
        for (User u : allUsers) {
            if (u instanceof Customer) {
                dbData.customers.add((Customer) u);
            } else if (u instanceof Admin && !(u instanceof SuperAdmin)) {
                dbData.admins.add((Admin) u);
            }
        }
 
        dbData.accounts = bank.getAccountManager().getAllAccounts();
        dbData.bills = bank.getBillManager().getAllBills();
        dbData.standingOrders = bank.getStandingOrderManager().getOrders();

       
        jsonDao.saveDatabase(dbData);
    }

    public void loadAllData() {
        BankSystem bank = BankSystem.getInstance();

      
        JsonDao.DatabaseData dbData = jsonDao.loadDatabase();

     
        List<User> allUsers = new ArrayList<>();
        allUsers.add(SuperAdmin.getInstance()); 
        
        if (dbData.customers != null) allUsers.addAll(dbData.customers);
        if (dbData.admins != null) allUsers.addAll(dbData.admins);
        
        bank.getUserManager().setUsers(allUsers);

     
        if (dbData.accounts != null) {
            bank.getAccountManager().setAccounts(dbData.accounts);
        } else {
            bank.getAccountManager().setAccounts(new ArrayList<>());
        }
 
        if (dbData.bills != null) {
            bank.getBillManager().setBills(dbData.bills);
        } else {
            bank.getBillManager().setBills(new ArrayList<>());
        }
 
        if (dbData.standingOrders != null) {
            bank.getStandingOrderManager().setOrders(dbData.standingOrders);
        } else {
            bank.getStandingOrderManager().setOrders(new ArrayList<>());
        }

        System.out.println("Data loaded successfully from DAO/Database.json.");
    }
}