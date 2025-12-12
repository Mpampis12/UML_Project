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
    public List<Account> getAllAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

}
