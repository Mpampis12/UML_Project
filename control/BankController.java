package control;

import services.BankSystem;
import services.TransactionManager;
import model.User;
import model.Account;
import model.Bill;
import model.StandingOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BankController {

    private TransactionManager transactionManager;

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

     
    public void handleDeposit(String iban, double amount) throws Exception {
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
public String createBill(String targetIban, String businessAfm, double amount, String description, String payerAfm) {
        // Δημιουργία τυχαίου RF Code
        String rfCode = "RF" + Math.abs(UUID.randomUUID().getMostSignificantBits());
        rfCode = rfCode.substring(0, 12); 

        LocalDate expireDate = LocalDate.now().plusDays(30);

        // Περνάμε το targetIban στον Constructor
        Bill newBill = new Bill(rfCode, targetIban, amount, description, businessAfm, expireDate);
        
        if (payerAfm != null && !payerAfm.isEmpty()) {
            newBill.setPayerAfm(payerAfm); 
        }

        BankSystem.getInstance().getBillManager().addBill(newBill);
        saveData(); // Αποθήκευση στη βάση
        return rfCode;
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

            // 1. Καλούμε το API
            if (type.equals("SEPA")) {
                response = api.sendSepaTransfer(amount, name, targetIban, bic, bankName);
            } else {
                response = api.sendSwiftTransfer(amount, name, targetIban, bic, bankName, address, country);
            }

            // 2. Αν πέτυχε το API, αφαιρούμε τα λεφτά από τον δικό μας λογαριασμό
            // Χρησιμοποιούμε το withdraw του transactionManager
            transactionManager.withdraw(sourceIban, amount, type + " Transfer to " + name + " (ID: " + response.transaction_id + ")");
            
            System.out.println("External Transfer Success: " + response.message);
            saveData();
        }
        public List<StandingOrder> getStandingOrdersForUser(User user) {
 
            return BankSystem.getInstance().getStandingOrderManager().getOrders(); 
    }
    }