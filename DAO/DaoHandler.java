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

        List<User> allUsers = bank.getUserManager().getUsers();
        List<Customer> customers = new ArrayList<>();
        List<Admin> admins = new ArrayList<>();

        for (User u : allUsers) {
            if (u instanceof Customer) {
                customers.add((Customer) u);
            } else if (u instanceof Admin && !(u instanceof SuperAdmin)) {
                 admins.add((Admin) u);
            }
        }

        jsonDao.saveCustomers(customers);
        jsonDao.saveAdmins(admins);
        jsonDao.saveAccounts(bank.getAccountManager().getAllAccounts());
        jsonDao.saveBills(bank.getBillManager().getAllBills());
        jsonDao.saveStandingOrders(bank.getStandingOrderManager().getOrders());

        System.out.println("Data saved successfully.");
    }

    public void loadAllData() {
        BankSystem bank = BankSystem.getInstance();

         List<Customer> customers = jsonDao.loadCustomers();
        List<Admin> admins = jsonDao.loadAdmins();
        
        List<User> allUsers = new ArrayList<>();
         allUsers.add(SuperAdmin.getInstance());
        
        if (customers != null) allUsers.addAll(customers);
        if (admins != null) allUsers.addAll(admins);
        
        bank.getUserManager().setUsers(allUsers);

         List<Account> accounts = jsonDao.loadAccounts();
        if (accounts != null) bank.getAccountManager().setAccounts(accounts);
 
        List<Bill> bills = jsonDao.loadBills();
        if (bills != null) bank.getBillManager().setBills(bills);  

 
        List<StandingOrder> orders = jsonDao.loadStandingOrders();
        if (orders != null) bank.getStandingOrderManager().setOrders(orders);  

        System.out.println("Data loaded successfully.");
    }
}