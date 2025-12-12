package model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User{
    
    private List<String> accountIbans;
    private String userRoleString;

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

    public Customer(String username, char[] password, String firstName, String lastName, String afm, String email,
            String phone) {
        super(username, password, firstName, lastName, afm, email, phone);
        this.accountIbans = new ArrayList<>();
        userRoleString = "CUSTOMER";
    }

    public Customer() {
        this.accountIbans = new ArrayList<>();
    }
    
    @Override
    public String getRole(){
        return userRoleString;
    }

    @Override
    public String getDetails() {
        String details="Customer{" +"username='" + getUsername() + '\'' +", afm='" + getAfm() + '\'' +
                ", fullName='" + getFirstName() + " " + getLastName() + '\'' +
                ", accounts=" + accountIbans.size() +'}';
        return details;
    }

    

}
