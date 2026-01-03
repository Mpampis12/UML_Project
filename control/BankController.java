package control;

import services.BankSystem;
import services.ConfigManager;
import services.TransactionManager;
import model.User;
import model.Account;
import model.Bill;
import model.Customer;
import model.StandingOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankController {
    
    private ConfigManager config = ConfigManager.getInstance();
    private TransactionManager transactionManager;

    public ConfigManager getConfig() {
        return config;
    }

    public BankController() {
        // Παίρνουμε τον TransactionManager μέσα από το Singleton BankSystem
        this.transactionManager = BankSystem.getInstance().getTransactionManager();
    }

    // --- 1. AUTHENTICATION (Login / Register) ---
    
    public User login(String username, char[] password) {
        return BankSystem.getInstance().getUserManager().login(username, password);
    }

    public void registerUser(String username, char[] password, String firstName, String lastName, String afm, String email, String phone) throws Exception {
        BankSystem.getInstance().getUserManager().registerCustomer(username, password, firstName, lastName, afm, email, phone);
    }
    public void createAdmin(String username, char[] password, String fName, String lName, String email) throws Exception {
        BankSystem.getInstance().getUserManager().registerAdmin(username, password, fName, lName, email);
    }

     
 
    public void createCustomerByType(String username, char[] password, String fName, String lName, String afm, String email, String phone, String type) throws Exception {
        if(type.equals("BUSINESS")) {
            BankSystem.getInstance().getUserManager().registerCustomerBuisness(username, password, fName, lName, afm, email, phone);
            return;
        }
        else{
        BankSystem.getInstance().getUserManager().registerCustomer(username, password, fName, lName, afm, email, phone);
        }
    }

 
    public List<Account> getAccountsForUser(User user) {
        return BankSystem.getInstance().getAccountManager().getAccountsByOwner(user.getAfm());
    }

    public User getOwner(String afm) {
        return BankSystem.getInstance().getUserManager().getUserByAfm(afm);
    }

     
    public void handleDeposit(String iban, double amount ) throws Exception {
        BankCommandPattern deposit = new DepositCommand(transactionManager, iban, amount, "Deposit via App");
        deposit.execute();
        saveData();
    }

    public void handleWithdraw(String iban, double amount) throws Exception {
        BankCommandPattern withdraw = new WithDrawCommand(transactionManager, iban, amount, "Withdrawal via App");
        withdraw.execute();
        saveData();
    }

    public void handleTransfer(String sourceIban, String targetIban, double amount) throws Exception {
        BankCommandPattern transfer = new TransferCommand(transactionManager, sourceIban, targetIban, amount, "Transfer via App");
        transfer.execute();
        saveData();
    }

 public void deleteStandingOrder(StandingOrder order) {
        BankSystem.getInstance().getStandingOrderManager().deleteOrder(order);
        saveData();
    }
public String createBill(String targetIban, String businessAfm, double amount, String description, String payerAfm, String expireDateStr) throws Exception {
        
        // Δημιουργία τυχαίου RF Code
        String rfCode = "RF" + Math.abs(UUID.randomUUID().getMostSignificantBits());
        rfCode = rfCode.substring(0, 12); 

        // Μετατροπή String σε LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate  expireDate;
        try {
            expireDate = LocalDate.parse(expireDateStr, formatter);
        } catch (Exception e) {
            throw new Exception("Invalid Date Format. Please use dd/MM/yyyy");
        }

        if (expireDate.isBefore(BankSystem.getInstance().getTimeSimulator().getCurrentDate().toLocalDate())) {
            throw new Exception("Expiration date cannot be in the past.");
        }

        Bill newBill = new Bill(rfCode, targetIban, amount, description, businessAfm, expireDate.atStartOfDay());
        
        if (payerAfm != null && !payerAfm.isEmpty()) {
            newBill.setPayerAfm(payerAfm); 
        }

        BankSystem.getInstance().getBillManager().addBill(newBill);
        saveData(); 
        return rfCode;
    }
    public  Bill getBillByRF(String rf) throws Exception {
      Bill bill = services.BankSystem.getInstance().getBillManager().getBillByRf(rf);
    if (bill == null) {
        throw new Exception("Bill with RF " + rf + " not found.");
    }
    if (bill.getBillStatus().equals("PAID")) {
        throw new Exception("This bill is already paid.");
    }
    return bill;
}

    public void payBill(String rfCode, String payerIban, String payerAfm) throws Exception {
        
        BankSystem.getInstance().getBillManager().payBill(rfCode, payerIban, payerAfm, transactionManager);
        saveData();
    }

    // --- 5. STANDING ORDERS ---
    public void createStandingOrder(StandingOrder order) {
        BankSystem.getInstance().getStandingOrderManager().addOrder(order);
        saveData();
    }

    // --- UTILS ---
    public void saveData() {
        try {
             BankSystem.getInstance().getDaoHandler().saveAllData();
        } catch (Exception e) {
            System.out.println("Error autosaving data: " + e.getMessage());
        }
    }

    public void handleExternalTransfer(String type, String sourceIban, double amount, 
                                        String name, String targetIban, String bic, 
                                        String bankName, String address, String country) throws Exception {
            
            services.BankApiService api = BankSystem.getInstance().getBankApiService();
            services.BankApiService.ApiResponse response;
            String commStr = String.valueOf(config.getPropertyAsDouble("busness.fee.monthly"));
            double commPercent = (commStr != null) ? Double.parseDouble(commStr) : 5.0; // Default 5% αν δεν βρεθεί
            
            double commissionFee = amount * (commPercent / 100.0);
            double totalDeduction = amount + commissionFee;

            // Έλεγχος υπολοίπου (γιατί τώρα αφαιρούμε περισσότερα)
            Account sourceAcc = BankSystem.getInstance().getAccountManager().getAccount(sourceIban);
            if (sourceAcc.getBalance() < totalDeduction) {
                throw new Exception("Insufficient balance for transfer + " + commPercent + "% commission (" + commissionFee + "€).");
            }

            // 1. Καλούμε το API
            if (type.equals("SEPA")) {
                response = api.sendSepaTransfer(amount, name, targetIban, bic, bankName);
            } else {
                response = api.sendSwiftTransfer(amount, name, targetIban, bic, bankName, address, country);
            }

           
            transactionManager.withdraw(sourceIban, amount, type + " Transfer to " + name + " (ID: " + response.transaction_id + ")",BankSystem.getInstance().getTimeSimulator().getCurrentDate() );
            
            System.out.println("External Transfer Success: " + response.message);
            // 3. Χρέωση της προμήθειας (ξεχωριστή συναλλαγή για να φαίνεται)
            if (commissionFee > 0) {
                transactionManager.withdraw(sourceIban, commissionFee, "Bank Commission Fee (" + commPercent + "%)", BankSystem.getInstance().getTimeSimulator().getCurrentDate());
                System.out.println("External Transfer Success with Commission: " + commissionFee);
            }
            saveData();
        }
        public List<StandingOrder> getStandingOrdersForUser(User user) {
         List<StandingOrder> allOrders = BankSystem.getInstance().getStandingOrderManager().getOrders();
        
         List<Account> userAccounts = getAccountsForUser(user);
        
         List<StandingOrder> myOrders = new  ArrayList<>();  

        for (StandingOrder so : allOrders) {
             for (Account acc : userAccounts) {
                if (acc.getIban().equals(so.getSource().toString())) {
                    
                    myOrders.add(so);
                    break;  
            }
        }
    }
        return myOrders; 
    }
    public  List<Account> searchAccounts(String query) {
        List<Account> foundAccounts = new  ArrayList<>();
         List<User> allUsers = BankSystem.getInstance().getUserManager().getUsers(); // Υποθέτουμε ότι υπάρχει getCustomers/getUsers
        
        // 1. Βρες χρήστες που ταιριάζουν
        for (User u : allUsers) {
            String fullName = (u.getFirstName() + " " + u.getLastName()).toLowerCase();
            if (u.getAfm().equals(query) || fullName.contains(query.toLowerCase())) {
                // 2. Πάρε τους λογαριασμούς τους
                foundAccounts.addAll(getAccountsForUser(u));
            }
        }
        return foundAccounts;
    }

    // Προσθήκη Συνδικαιούχου
    public void addOwnerToAccount(String iban, String newOwnerAfm) throws Exception {
        Account acc = BankSystem.getInstance().getAccountManager().getAccount(iban);
        if (acc == null) throw new Exception("Account not found");

        User newOwner = BankSystem.getInstance().getUserManager().getUserByAfm(newOwnerAfm);
        if (newOwner == null) throw new Exception("User with AFM " + newOwnerAfm + " not found.");

        if (acc.getOwners().contains(newOwnerAfm)) {
            throw new Exception("User is already an owner of this account.");
        }
        BankController ctrl = new BankController();
        Customer seconOwnUserer = (Customer) ctrl.getOwner(newOwnerAfm);
        if (seconOwnUserer == null) {
            throw new Exception("No user found with AFM: " + newOwnerAfm);
        } else {
        seconOwnUserer.setNewAccountIban(iban);

        acc.addOwner(newOwnerAfm);
        saveData();
    }
    }
    public List<Bill> getBillsByBusiness(String businessAfm) {
        List<Bill> allBills = BankSystem.getInstance().getBillManager().getAllBills();
        List<Bill> myBills = new java.util.ArrayList<>();
        for (Bill b : allBills) {
            if (b.getBuisinessAfm().equals(businessAfm)) {
                myBills.add(b);
            }
        }
        return myBills;
    }

    public void deleteBill(String rfCode) {
        List<Bill> bills = BankSystem.getInstance().getBillManager().getAllBills();
        bills.removeIf(b -> b.getRfCode().equals(rfCode));
        saveData();
    }
    
    public void updateBill(String rfCode, String targetIban, double amount, String desc, String payerAfm, String expireDateStr) throws Exception {
        Bill bill = BankSystem.getInstance().getBillManager().getBillByRf(rfCode);
        if (bill == null) throw new Exception("Bill not found");
        if (bill.getBillStatus() == Bill.Status.PAID) throw new Exception("Cannot edit a PAID bill.");

        bill.setTargetIban(targetIban);
        bill.setAmount(amount);
        bill.setDescription(desc);
        bill.setPayerAfm(payerAfm);
        
        // Update Date
         
        bill.setExpireDate(LocalDate.parse(expireDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay());
        
        saveData();
    }
}
    