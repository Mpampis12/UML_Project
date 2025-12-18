package services;

import java.util.ArrayList;
import java.util.List;

import DAO.DaoHandler;

import java.time.LocalDateTime;
import model.Account;
import model.Transaction;

public class AccountManager {
    
    private List<Account> accounts;
    
    public AccountManager() {
        this.accounts = new ArrayList<>();
    }

     public Account createAccount(String type, double balance, String ownerAfm) {
        try {
            Account newAccount = AccountFactory.createAccount(type, balance, ownerAfm);
            this.accounts.add(newAccount);
            System.out.println("Account created " + newAccount.getIban());
            return newAccount;  
        } catch (Exception e) {
            System.out.println("Error on creating account: " + e.getMessage());
            return null;
        }
    }
    public Account getAccount(String iban) {
        for (Account acc : accounts) {
            if (acc.getIban().equals(iban)) {
                return acc;
            }
        }
        return null;
    }

    public List<Account> getAccountsByOwner(String afm) {
        List<Account> userAccounts = new ArrayList<>();
        for (Account acc : accounts) {
             if (acc.getOwners().contains(afm)) {
                userAccounts.add(acc);
            }
        }
        return userAccounts;
    }

    public void applyInterests() {
        double interestRate = 0.001 / 365; 
        
        for (Account acc : accounts) {
            // Υπολογισμός τόκου
            double interest = acc.getBalance() * interestRate;
            try {
                 acc.setBalance(acc.getBalance() + interest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Interests applied to all accounts.");
    }

  
    public void chargeBusinessFees() {
        double fee = 5.0;
        
        for (Account acc : accounts) {
            if (acc.getAccountType() == model.Account.AccountType.BUSINESS) {
                try {
                    acc.withdraw(fee); 
                   System.out.println("Charged fee to Business Account: " + acc.getIban());
                } catch (Exception e) {
                    System.out.println("Could not charge fee to " + acc.getIban() + ": " + e.getMessage());
                }
            }
        }
    }
    public List<Account> getAllAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }


        public void applyEndOfMonthPolicy() {
        System.out.println("--- Εφαρμογή Πολιτικής (Τιμές από Config) ---");

        // Ανάγνωση τιμών από το αρχείο
        ConfigManager config = ConfigManager.getInstance();
        double annualRate = config.getPropertyAsDouble("interest.rate.annual");
        double monthlyFee = config.getPropertyAsDouble("maintenance.fee.monthly");
        double monthlyRate = annualRate / 12.0;

        for (Account acc : accounts) {
            double currentBalance = acc.getBalance();

            // 1. Υπολογισμός Τόκων (Δυναμικά από το config)
            if (currentBalance > 0) {
                double interest = currentBalance * monthlyRate;
                // Στρογγυλοποίηση σε 2 δεκαδικά
                interest = Math.round(interest * 100.0) / 100.0;
                
                acc.setBalance(acc.getBalance() + interest);

                Transaction interestTx = new Transaction.Builder()
                        .setSourceIban(acc.getIban().toString())
                        .setTargetIban(acc.getIban().toString())
                        .setAmount(interest)
                        .setDescription("INTEREST: " + (annualRate*100) + "%)")
                        .setTimestamp(LocalDateTime.now())
                        .build();
                
                //TransactionManager.getInstance().addTransaction(interestTx);
            }

            // 2. Χρέωση Τέλους Συντήρησης (Δυναμικά από το config)
            acc.setBalance(acc.getBalance() - monthlyFee);

            Transaction feeTx = new Transaction.Builder()
                    .setSourceIban(acc.getIban().toString())
                    .setTargetIban("BANK_TREASURY")
                    .setAmount(monthlyFee)
                    .setDescription("Μηνιαίο Κόστος")
                    .setTimestamp(LocalDateTime.now())
                    .build();
            
            //TransactionManager.getInstance().addTransaction(feeTx);
        }
        
        DaoHandler.getInstance().saveAllData();
    }
}
