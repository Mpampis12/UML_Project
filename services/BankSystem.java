package services;

import java.time.LocalDate;
import java.time.LocalDateTime;

import DAO.DaoHandler;

public class BankSystem {

    private static BankSystem instance;

    // Όλοι οι Managers
    private UserManager userManager;
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private BillManager billManager;
    private StandingOrderManager standingOrderManager;
    private TimeSimulator timeSimulator;
    private DaoHandler daoHandler;
    private BankApiService bankApiService;
    

    
    
    private BankSystem() {
        this.userManager = new UserManager();
        this.accountManager = new AccountManager();
        this.billManager = new BillManager();
        this.standingOrderManager = new StandingOrderManager();
        this.timeSimulator = new TimeSimulator();
    
        this.transactionManager = new TransactionManager(this.accountManager);
        this.daoHandler= DAO.DaoHandler.getInstance();
        this.bankApiService = new BankApiService();
    }
    
    public BankApiService getBankApiService() {  
        return bankApiService;
    }   
    
    public static synchronized BankSystem getInstance() {
        if (instance == null) {
            instance = new BankSystem();
        }
        return instance;
    }
    
    public DaoHandler getDaoHandler() {
       return daoHandler;
   }
     public void performDailyTasks(LocalDateTime date) {
          System.out.println("--- Standing Orders ---");
         standingOrderManager.executeDailyOrders(date, transactionManager, billManager);
           System.out.println("--- Εφαρμογή Πολιτικής (Config) ---");
         if (date.getDayOfMonth() == 1) {
            accountManager.chargeBusinessFees();
            
        }
        
        
    }

    public UserManager getUserManager() { return userManager; }
    public AccountManager getAccountManager() { return accountManager; }
    public TransactionManager getTransactionManager() { return transactionManager; }
    public BillManager getBillManager() { return billManager; }
    public StandingOrderManager getStandingOrderManager() { return standingOrderManager; }
    public TimeSimulator getTimeSimulator() { return timeSimulator; }
}