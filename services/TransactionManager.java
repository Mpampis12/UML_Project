package services;

import model.Account;
import model.Transaction;


import java.util.UUID; 

public class TransactionManager {

    private AccountManager accountManager;

    public TransactionManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * ΕΚΤΕΛΕΣΗ ΚΑΤΑΘΕΣΗΣ
     */
    public void deposit(String iban, double amount, String description) throws Exception {
       
        Account account = accountManager.getAccount(iban);
        if (account == null) {
            throw new Exception("Account with IBAN " + iban + " not found.");
        }

       
        account.deposit(amount);

     
        String txId = generateTransactionId();
        
        Transaction transaction = new Transaction.Builder(txId, "DEPOSIT", amount)
                .setSourceIban(iban)
                .setDescription(description)
                .build();


        account.addTransaction(transaction);
        
        System.out.println("Succesfull Deposit" + amount + "€ to " + iban);
    }


    public void withdraw(String iban, double amount, String description) throws Exception {
        Account account = accountManager.getAccount(iban);
        if (account == null) {
            throw new Exception("ΟAccount not found.");
        }

        account.withdraw(amount);

        String txId = generateTransactionId();
        
        Transaction transaction = new Transaction.Builder(txId, "WITHDRAWAL", amount)
                .setSourceIban(iban)
                .setDescription(description)
                .build();

        account.addTransaction(transaction);
        
        System.out.println("Succesfull withdrawal" + amount + "€ from " + iban);
    }


    public void transfer(String sourceIban, String targetIban, double amount, String description) throws Exception {
        Account sourceAcc = accountManager.getAccount(sourceIban);
        Account targetAcc = accountManager.getAccount(targetIban);

        if (sourceAcc == null) throw new Exception("Account  not found.");
        if (targetAcc == null) throw new Exception("Accepter not found.");
        if (sourceIban.equals(targetIban)) throw new Exception("You cannot transfer on the same account.");

        sourceAcc.withdraw(amount); 
        targetAcc.deposit(amount);

        String txId = generateTransactionId();

        Transaction transaction = new Transaction.Builder(txId, "TRANSFER", amount)
                .setSourceIban(sourceIban)
                .setTargetIban(targetIban)
                .setDescription(description)
                .build();

        sourceAcc.addTransaction(transaction);
        targetAcc.addTransaction(transaction); 

        System.out.println("Επιτυχής μεταφορά " + amount + "€ από " + sourceIban + " σε " + targetIban);
    }

    private String generateTransactionId() {
        return "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}