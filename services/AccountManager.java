package services;

import java.util.ArrayList;
import java.util.List;

import model.Account;

public class AccountManager {
    
    private List<Account> accounts;
    
    public AccountManager() {
        this.accounts = new ArrayList<>();
    }

    public void createAccount(String type, double balance, String ownerAfm) {
        try {
            Account newAccount = AccountFactory.createAccount(type, balance, ownerAfm);
            this.accounts.add(newAccount);
            System.out.println("Account created " + newAccount.getIban());
        } catch (Exception e) {
            System.out.println("Error on creating account: " + e.getMessage());
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

}
