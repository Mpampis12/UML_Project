package control;

import services.BankSystem;
import services.ConfigManager;
import services.TransactionManager;
import services.transfer.*; // Import για το Bridge Pattern
import services.StandingOrderFactory; // Import για το Factory Pattern
import services.TimeSimulator;
import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankController {
    private BankSystem bankSystem;
    private TransactionManager transactionManager;
    
    private static BankController instance;
    public static BankController getInstance(){
        if(instance==null){
            instance = new BankController();
        }
        return instance;
    }


    private BankController() {
        this.bankSystem = BankSystem.getInstance();
        this.transactionManager = bankSystem.getTransactionManager();
    }

    // ============================================================
    // 1. AUTHENTICATION & USER MANAGEMENT
    // ============================================================
    
    public User login(String username, char[] password) {
        return bankSystem.getUserManager().login(username, password);
    }

    public void registerUser(String username, char[] password, String fName, String lName, String afm, String email, String phone) throws Exception {
        bankSystem.getUserManager().registerCustomer(username, password, fName, lName, afm, email, phone);
    }

    public void createAdmin(String username, char[] password, String fName, String lName, String email) throws Exception {
        bankSystem.getUserManager().registerAdmin(username, password, fName, lName, email);
    }

    public void createCustomerByType(String username, char[] password, String fName, String lName, String afm, String email, String phone, String type) throws Exception {
        if(type.equals("BUSINESS")) {
            bankSystem.getUserManager().registerCustomerBuisness(username, password, fName, lName, afm, email, phone);
        } else {
            bankSystem.getUserManager().registerCustomer(username, password, fName, lName, afm, email, phone);
        }
    }

    public List<User> getAdmins() {
        return bankSystem.getUserManager().getAdmins();
    }

    public List<User> getCustomers() {
        return bankSystem.getUserManager().getCustomers();
    }

    public User getOwner(String afm) {
        return bankSystem.getUserManager().getUserByAfm(afm);
    }

    // ============================================================
    // 2. ACCOUNT MANAGEMENT
    // ============================================================

    public List<Account> getAccountsForUser(User user) {
        return bankSystem.getAccountManager().getAccountsByOwner(user.getAfm());
    }

    public List<Account> searchAccounts(String query) {
        List<Account> foundAccounts = new ArrayList<>();
        List<User> allUsers = bankSystem.getUserManager().getUsers();
        
        for (User u : allUsers) {
            String fullName = (u.getFirstName() + " " + u.getLastName()).toLowerCase();
            if (u.getAfm().equals(query) || fullName.contains(query.toLowerCase())) {
                foundAccounts.addAll(getAccountsForUser(u));
            }
        }
        return foundAccounts;
    }

    public void addOwnerToAccount(String iban, String newOwnerAfm) throws Exception {
        Account acc = bankSystem.getAccountManager().getAccount(iban);
        if (acc == null) throw new Exception("Account not found");

        User newOwner = getOwner(newOwnerAfm);
        if (newOwner == null) throw new Exception("User with AFM " + newOwnerAfm + " not found.");

        if (acc.getOwners().contains(newOwnerAfm)) {
            throw new Exception("User is already an owner of this account.");
        }
        
        if (newOwner instanceof Customer) {
             ((Customer) newOwner).setNewAccountIban(iban);
             acc.addOwner(newOwnerAfm);
             saveData();
        } else {
             throw new Exception("Only Customers can be co-owners.");
        }
    }

    // ============================================================
    // 3. TRANSACTIONS (FACTORY & COMMAND PATTERNS)
    // ============================================================

    public void handleDeposit(String iban, double amount,String description) throws Exception {
        // Pattern: Factory (CommandFactory)
        BankCommandPattern deposit;
        if(description.isBlank()){
              deposit = CommandFactory.createCommand("DEPOSIT", transactionManager, iban, null, amount, "Deposit via App");
        }else{
               deposit = CommandFactory.createCommand("DEPOSIT", transactionManager, iban, null, amount, description);
        
        }
        deposit.execute(); // Pattern: Command
        saveData();
    }
    public void handleWithdraw(String iban, double amount,String description) throws Exception {
        BankCommandPattern withdraw;
        
        if(description.isEmpty())

             withdraw = CommandFactory.createCommand("WITHDRAW", transactionManager, iban, null, amount, "Withdrawal via App");

        else
            {
            withdraw = CommandFactory.createCommand("WITHDRAW", transactionManager, iban, null, amount, description);
        }
        withdraw.execute();
        saveData();
    }

    public void handleTransfer(String sourceIban, String targetIban, double amount) throws Exception {
        BankCommandPattern transfer = CommandFactory.createCommand("TRANSFER", transactionManager, sourceIban, targetIban, amount, "Transfer via App");
        transfer.execute();
        saveData();
    }

    // ============================================================
    // 4. EXTERNAL TRANSFERS (BRIDGE PATTERN)
    // ============================================================

    public void handleExternalTransfer(String type, String sourceIban, double amount, 
                                        String name, String targetIban, String bic, 
                                        String bankName, String address, String country) throws Exception {
            
        // 1. Υπολογισμός Προμήθειας (ConfigManager)
        String commStr = String.valueOf(services.ConfigManager.getInstance().getPropertyAsDouble("transaction.commission.percent"));
        double commPercent = (commStr != null) ? Double.parseDouble(commStr) : 5.0; // Default 5%
        
        double commissionFee = amount * (commPercent / 100.0);
        Account sourceAcc = bankSystem.getAccountManager().getAccount(sourceIban);
        
        if (sourceAcc.getBalance() < (amount + commissionFee)) {
            System.out.println("Source Balance: " + sourceAcc.getBalance() + ", Required: " + (amount + commissionFee));
            throw new Exception("Insufficient balance for transfer + fee.");
        }

        // 2. Pattern: BRIDGE - Επιλογή Υλοποίησης (Implementation)
        TransferMechanism mechanism;
        if (type.equals("SEPA")) {
            mechanism = new SepaTransferMechanism();
        } else if (type.equals("SWIFT")) {
            mechanism = new SwiftTransferMechanism();
        } else {
            mechanism = new InternalTransferMechanism();
        }

        // 3. Pattern: BRIDGE - Εκτέλεση μέσω Abstraction
        TransferAbstraction transfer = new ElectronicTransfer(mechanism);
        transfer.makeTransfer(amount, sourceIban, targetIban, name, bic, bankName, address, country);
        
        System.out.println("External Transfer Executed via Bridge: " + type);

        // 4. Χρέωση Προμήθειας (ως ξεχωριστό Command)
        if (commissionFee > 0) {
            BankCommandPattern feeCmd = CommandFactory.createCommand("WITHDRAW", transactionManager, sourceIban, null, commissionFee, "Bank Commission Fee (" + commPercent + "%)");
            feeCmd.execute();
        }
        
        saveData();
    }

    // ============================================================
    // 5. BILLS MANAGEMENT
    // ============================================================

    public String createBill(String targetIban, String businessAfm, double amount, String description, String payerAfm, String expireDateStr) throws Exception {
        String rfCode = "RF" + Math.abs(UUID.randomUUID().getMostSignificantBits());
        rfCode = rfCode.substring(0, 12); 

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate expireDate;
        try {
            expireDate = LocalDate.parse(expireDateStr, formatter);
        } catch (Exception e) {
            throw new Exception("Invalid Date Format. Please use dd/MM/yyyy");
        }

        if (expireDate.isBefore(bankSystem.getTimeSimulator().getCurrentDate().toLocalDate())) {
            throw new Exception("Expiration date cannot be in the past.");
        }

        Bill newBill = new Bill(rfCode, targetIban, amount, description, businessAfm, expireDate.atStartOfDay());
        if (payerAfm != null && !payerAfm.isEmpty()) {
            newBill.setPayerAfm(payerAfm); 
        }

        bankSystem.getBillManager().addBill(newBill);
        saveData(); 
        return rfCode;
    }

    public Bill getBillByRF(String rf) throws Exception {
        Bill bill = bankSystem.getBillManager().getBillByRf(rf);
        if (bill == null) throw new Exception("Bill with RF " + rf + " not found.");
        return bill;
    }

    public void payBill(String rfCode, String payerIban, String payerAfm) throws Exception {
        bankSystem.getBillManager().payBill(rfCode, payerIban, payerAfm, transactionManager);
        saveData();
    }

    public List<Bill> getBillsByBusiness(String businessAfm) {
        List<Bill> allBills = bankSystem.getBillManager().getAllBills();
        List<Bill> myBills = new ArrayList<>();
        for (Bill b : allBills) {
            if (b.getBuisinessAfm().equals(businessAfm)) {
                myBills.add(b);
            }
        }
        return myBills;
    }

    public void deleteBill(String rfCode) {
        List<Bill> bills = bankSystem.getBillManager().getAllBills();
        bills.removeIf(b -> b.getRfCode().equals(rfCode));
        saveData();
    }
    
    public void updateBill(String rfCode, String targetIban, double amount, String desc, String payerAfm, String expireDateStr) throws Exception {
        Bill bill = getBillByRF(rfCode);
        if (bill.getBillStatus() == Bill.Status.PAID) throw new Exception("Cannot edit a PAID bill.");

        bill.setTargetIban(targetIban);
        bill.setAmount(amount);
        bill.setDescription(desc);
        bill.setPayerAfm(payerAfm);
        bill.setExpireDate(LocalDate.parse(expireDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay());
        
        saveData();
    }

    // ============================================================
    // 6. STANDING ORDERS (FACTORY PATTERN)
    // ============================================================

    public void createStandingOrder(String source, String target, double amount, String desc, int day, java.time.LocalDateTime expireDate) {
        StandingOrder so;
        
        // Pattern: FACTORY - Η επιλογή του αντικειμένου γίνεται εδώ, όχι στο UI
        if (target.startsWith("RF") || target.length() < 15) { 
            so = StandingOrderFactory.createBillPaymentOrder(source, target, amount, desc, day, expireDate);
        } else {
            so = StandingOrderFactory.createTransferOrder(source, target, amount, desc, day, expireDate);
        }

        bankSystem.getStandingOrderManager().addOrder(so);
        saveData();
    }

    public void deleteStandingOrder(StandingOrder order) {
        bankSystem.getStandingOrderManager().deleteOrder(order);
        saveData();
    }

    public List<StandingOrder> getStandingOrdersForUser(User user) {
        List<StandingOrder> allOrders = bankSystem.getStandingOrderManager().getOrders();
        List<Account> userAccounts = getAccountsForUser(user);
        List<StandingOrder> myOrders = new ArrayList<>();  

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

    // ============================================================
    // 7. DATA PERSISTENCE
    // ============================================================
    
    public void saveData() {
        try {
              DAO.DaoHandler.getInstance().saveAllData();
        } catch (Exception e) {
            System.out.println("Error autosaving data: " + e.getMessage());
        }
    }

    public void handleDateChange(LocalDateTime newDate) {
        bankSystem.performDailyTasks(newDate);
    }

    public TimeSimulator getTimeSimulator() {
      return bankSystem.getTimeSimulator();
    }

    public Account createAccountForUser(User user, String selectedType, double d, String afm) throws Exception {
        
       Account newAcc = bankSystem.getAccountManager().createAccount(selectedType, 0.0, afm);
       return newAcc;
    }

    public ArrayList<String> getOwnersByIban(String iban) {
          return  (ArrayList<String>)bankSystem.getAccountManager().getAccount(iban).getOwners() ;
    }

    public void updateUser(User user,String fName ,String lName ,String email ,String phone ,String username ,String password ,String afm ) {
        try {
            bankSystem.getUserManager().updateUser(user, fName , lName , email , phone ,username ,password ,afm );
        } catch (Exception e) {
             
            e.printStackTrace();
        }
    }

    public List<User> getUsers() {
        return bankSystem.getUserManager().getUsers();

    }


    public Account getAccount(String iban) {
        return BankSystem.getInstance().getAccountManager().getAccount(iban);
    }


    public List<Account> getAccountsByOwner(String afm) {
       return BankSystem.getInstance().getAccountManager().getAccountsByOwner(afm);
    }
}