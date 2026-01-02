package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer extends User { 
    
    private List<String> accountIbans;

    public Customer(String username, char[] password, String firstName, String lastName, String afm, String email, String phone) {
        super(username, password, firstName, lastName, afm, email, phone);
        this.accountIbans = new ArrayList<>();
    }

    public Customer() {
        this.accountIbans = new ArrayList<>();
    }

    public List<String> getAccountIbans() {
        return accountIbans;
    }

    public void setNewAccountIban(String iban) {
        if(!this.accountIbans.contains(iban)){
            this.accountIbans.add(iban);
        }
    }
    
    public void removeAccountIban(String iban) {
        if(this.accountIbans.contains(iban)){
            this.accountIbans.remove(iban);
        }
    }

    @Override
    public String getDetails() {
        return "Customer{" +
                "username='" + getUsername() + '\'' +
                ", afm='" + getAfm() + '\'' +
                ", fullName='" + getFirstName() + " " + getLastName() + '\'' +
                ", role='" + getRole() + '\'' +
                ", accounts=" + accountIbans.size() +
                '}';
    }
    
 }