package control;

import services.BankSystem;
import services.TransactionManager;
import DAO.DaoHandler;

public class BankController {

    private TransactionManager transactionManager;

    public BankController() {
        // Παίρνουμε τον TransactionManager μέσα από το Singleton BankSystem
        this.transactionManager = BankSystem.getInstance().getTransactionManager();
    }

    public void handleDeposit(String iban, double amount) throws Exception {
        // 1. Δημιουργία της εντολής
        BankCommandPattern deposit = new DepositCommand(transactionManager, iban, amount, "Deposit via App");
        
        // 2. Εκτέλεση
        deposit.execute();
        
        // 3. Αποθήκευση αλλαγών
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

    // Βοηθητική μέθοδος για να σώζουμε τα δεδομένα μετά από κάθε πράξη
    private void saveData() {
        try {
             // Χρησιμοποιούμε τον μοναδικό handler από το BankSystem
             BankSystem.getInstance().getDaoHandler().saveAllData();
        } catch (Exception e) {
            System.out.println("Error autosaving data: " + e.getMessage());
        }
    }
}