package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private Iban iban;
    private double balance;
    private LocalDateTime creationDate;
    private List<String> owners;//AFM 
    private List<Transaction> transactionList;
    private AccountType type;

   public enum AccountType{
        PERSONAL,
        BUSINESS
    }
    

    public Account(double balance, String primaryOwnerAfm,AccountType type) {
        this();  
        this.iban = Iban.generate();
        this.balance = balance;
        this.owners.add(primaryOwnerAfm);  
        this.type=type;
    }

    public Account() {
            this.transactionList = new ArrayList<>();
            this.owners = new ArrayList<>();
            this.creationDate =LocalDateTime.now();

    }

    public Account(String existingIban, double balance, String primaryOwnerAfm,String type) throws Exception {
        this();
        this.iban = new Iban(existingIban); 
        this.balance = balance;
        this.owners.add(primaryOwnerAfm);
        if(type.equals("PERSONAL"))
            this.type=AccountType.PERSONAL;
        else if(type.equals( "BUSINESS"))
            this.type = AccountType.BUSINESS;
        else
            throw new Exception("Wrong Type of Account");
    }

    public void deposit(double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Wrong Type of Account");
        }
        this.balance += amount;
    }


    public void withdraw(double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Wrong Type of amount");
        }
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new Exception("Not enough money bruhh!");
        }
    }

    @Override
    public String toString() {
        return "Account{" + "iban='" + iban + '\'' + ", balance=" + balance + "€}";
    }

    public void addOwner(String afmString) {
        if (!owners.contains(afmString)) {
            owners.add(afmString);
        }
    }


    public void addTransaction(Transaction t) {
        this.transactionList.add(t);
    }

    public AccountType getAccountType(){
        return type;
    }


    public String getIban() {
        return iban.toString();
    }



    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<String> getOwners() {
        return owners;
    }



    public List<Transaction> getTransaction() {
        return transactionList;
    }

}
 
