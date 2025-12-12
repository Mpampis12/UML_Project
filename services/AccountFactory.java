package services;

import model.Account;
import model.Account.AccountType;

 

public class AccountFactory {

    public static Account createAccount(String accType, double balance, String ownerAfm) throws Exception{
        
        AccountType type;
        if(accType.equalsIgnoreCase("PERSONAL")){
            type = AccountType.PERSONAL;
        }else if (accType.equalsIgnoreCase("BUSINESS")) {
            type = AccountType.BUSINESS;
        } else {
            throw new Exception("Invalid Account Type: " + accType);
        }
        


        return new Account(balance, ownerAfm, type);

        
    }
}
