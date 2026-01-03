package services;

import model.Account;
import model.Transaction;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import control.BankController; 

public class TransactionManager {

    private BankController controller;

    public TransactionManager() {
     // controller = BankController.getInstance();
    }

    public void deposit(String iban, double amount, String description, LocalDateTime date) throws Exception {
        // ΔΙΟΡΘΩΣΗ: Παίρνουμε τον AccountManager μέσω του Singleton BankSystem
        
        Account account = BankController.getInstance().getAccount(iban);
        
        if (account == null) {
            throw new Exception("Account with IBAN " + iban + " not found.");
        }

        account.deposit(amount);

        String txId = generateTransactionId();
        
        // Χρήση Builder (Pattern #4)
        Transaction transaction = new Transaction.Builder(txId, "DEPOSIT", amount)
                .setSourceIban(iban)
                .setDescription(description)
                .setTimestamp(date)
                .build();

        account.addTransaction(transaction);
        
        System.out.println("Successful Deposit " + amount + "€ to " + iban);
        controller.saveData();
        
    }

    public void withdraw(String iban, double amount, String description, LocalDateTime date) throws Exception {
        // ΔΙΟΡΘΩΣΗ
       Account account = BankController.getInstance().getAccount(iban);
        
        if (account == null) {
            throw new Exception("Account not found.");
        }

        account.withdraw(amount);

        String txId = generateTransactionId();
        
        Transaction transaction = new Transaction.Builder(txId, "WITHDRAWAL", amount)
                .setSourceIban(iban)
                .setDescription(description)
                .setTimestamp(date)
                .build();

        account.addTransaction(transaction);
        controller.saveData();
        System.out.println("Successful withdrawal " + amount + "€ from " + iban);
    }

    public void transfer(String sourceIban, String targetIban, double amount, String description, LocalDateTime date) throws Exception {
        // ΔΙΟΡΘΩΣΗ
        
        Account sourceAcc = BankController.getInstance().getAccount(sourceIban);
        Account targetAcc = BankController.getInstance().getAccount(targetIban);
         
        if (sourceAcc == null) throw new Exception("Source Account not found.");
        if (targetAcc == null) throw new Exception("Target Account not found.");
        if (sourceIban.equals(targetIban)) throw new Exception("You cannot transfer to the same account.");

        sourceAcc.withdraw(amount); 
        targetAcc.deposit(amount);

        String txId = generateTransactionId();

        // Transaction Out (για τον πομπό)
        Transaction tOut = new Transaction.Builder(txId, "TRANSFER", amount)
                .setSourceIban(sourceIban)
                .setTargetIban(targetIban)
                .setDescription("Transfer to " + targetIban + ": " + description)
                .setTimestamp(date)
                .build();
        sourceAcc.addTransaction(tOut);

        // Transaction In (για τον δέκτη - προαιρετικά διαφορετικό ID ή το ίδιο)
        // Εδώ χρησιμοποιούμε το ίδιο ID για ιχνηλασιμότητα, αλλά είναι ξεχωριστό αντικείμενο στη μνήμη αν χρειαστεί
        Transaction tIn = new Transaction.Builder(txId, "TRANSFER", amount)
                .setSourceIban(sourceIban)
                .setTargetIban(targetIban)
                .setDescription("Transfer from " + sourceIban + ": " + description)
                .setTimestamp(date)
                .build();
        targetAcc.addTransaction(tIn); 

        controller.saveData();
        System.out.println("Successful Transfer " + amount + "€ from " + sourceIban + " to " + targetIban);
    }

    private String generateTransactionId() {
        return "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public ArrayList<Transaction> getTransactionByAfm(String afm) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        
        
        for (Account acc : BankController.getInstance().getAccountsByOwner(afm)) {
            transactions.addAll(acc.getTransaction());
        }
        return transactions;
    }
}