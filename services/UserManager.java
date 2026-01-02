package services;
 
import model.User;
import model.Account;
import model.Individual; 
import model.Business;
import model.SuperAdmin;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern; // Import για Regex

import control.BankController;

public class UserManager {

    private List<User> users;

    // Regex Patterns για ασφάλεια
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^\\d{10}$"; // Μόνο 10 ψηφία
    private static final String AFM_REGEX = "^\\d{9}$";    // Μόνο 9 ψηφία
    private static final String NAME_REGEX = "^[a-zA-Z\\u0370-\\u03FF\\s]+$"; // Ελληνικά/Αγγλικά γράμματα μόνο

    public UserManager() {
        this.users = new ArrayList<>();
        this.users.add(SuperAdmin.getInstance());
    }

    public User login(String username, char[] password) {
        String inputHash = User.hashPassword(password);  
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(inputHash)) {
                return u;
            }
        }
        return null;  
    }

     private void validateCommonDetails(String username, char[] password, String fName, String lName, String email) throws Exception {
        if (username == null || username.trim().isEmpty()) throw new Exception("Username cannot be empty.");
        if (fName == null || fName.trim().isEmpty() || !fName.matches(NAME_REGEX)) throw new Exception("Invalid First Name (Letters only).");
        if (lName == null || lName.trim().isEmpty() || !lName.matches(NAME_REGEX)) throw new Exception("Invalid Last Name (Letters only).");
        
         if (!User.isValidPassword(password)) {
            throw new Exception("Password weak! Needs 10+ chars, Uppercase, Lowercase, Digit, Special Char.");
        }

        if (email == null || !email.matches(EMAIL_REGEX)) throw new Exception("Invalid Email Format.");
        
        if (getUserByUsername(username) != null) throw new Exception("Username already exists.");
    }

    // --- REGISTER METHODS ---
 
    public void registerCustomer(String username, char[] password, String firstName, String lastName, String afm, String email, String phone) throws Exception {
       
        validateCommonDetails(username, password, firstName, lastName, email);

         if (afm == null || !afm.matches(AFM_REGEX)) throw new Exception("Invalid AFM (Must be 9 digits).");
        if (phone == null || !phone.matches(PHONE_REGEX)) throw new Exception("Invalid Phone (Must be 10 digits).");
        
        if (getUserByAfm(afm) != null) throw new Exception("User with this AFM already exists.");

         Individual newCustomer = new Individual(username, password, firstName, lastName, afm, email, phone);
        
        Account defAccount = BankSystem.getInstance().getAccountManager().createAccount("PERSONAL", 0.0, afm);
        if (defAccount != null) {
            newCustomer.setNewAccountIban(defAccount.getIban());
        }

        this.users.add(newCustomer);
        BankSystem.getInstance().getDaoHandler().saveAllData();
        System.out.println("Success register Individual: " + username);
    }

     public void registerCustomerBuisness(String username, char[] password, String firstName, String lastName, String afm, String email, String phone ) throws Exception {
         validateCommonDetails(username, password, firstName, lastName, email);

         if (afm == null || !afm.matches(AFM_REGEX)) throw new Exception("Invalid AFM (Must be 9 digits).");
        if (phone == null || !phone.matches(PHONE_REGEX)) throw new Exception("Invalid Phone (Must be 10 digits).");
        
        if (getUserByAfm(afm) != null) throw new Exception("Business with this AFM already exists.");

         Business newCustomer = new Business(username, password, firstName, lastName, afm, email, phone);
        
        Account defAccount = BankSystem.getInstance().getAccountManager().createAccount("BUSINESS", 0.0, afm);
        if (defAccount != null) {
            newCustomer.setNewAccountIban(defAccount.getIban());
        }

        this.users.add(newCustomer);
        BankSystem.getInstance().getDaoHandler().saveAllData();
        System.out.println("Success register Business: " + username);
    }

    // Εγγραφή Admin (Δεν έχει AFM/Phone ως υποχρεωτικά στο signature, αλλά τα ελέγχουμε αν χρειαστεί)
    public void registerAdmin(String username, char[] password, String firstName, String lastName, String email) throws Exception {
        validateCommonDetails(username, password, firstName, lastName, email);
        
        // Οι admins παίρνουν dummy AFM/Phone με βάση τον κώδικά σου, οπότε δεν ελέγχουμε AFM εδώ.
        model.Admin newAdmin = new model.Admin(username, password, firstName, lastName, "000000000", email, "0000000000");
        this.users.add(newAdmin);
        BankSystem.getInstance().getDaoHandler().saveAllData();
        System.out.println("Success register Admin: " + username);
    }
   
 
    public User getUserByAfm(String afm) {
        for (User u : users) {
            if (u.getAfm() != null && u.getAfm().equals(afm)) return u;
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    // Update User (Εδώ προσθέτουμε έλεγχο και στην επεξεργασία)
    public void updateUser(User user, String fName, String lName, String email, String phone,String userName,String password,String AFM) throws Exception {
        if (fName == null || fName.trim().isEmpty() || !fName.matches(NAME_REGEX)) throw new Exception("Invalid First Name.");
        if (lName == null || lName.trim().isEmpty() || !lName.matches(NAME_REGEX)) throw new Exception("Invalid Last Name.");
        if (email == null || !email.matches(EMAIL_REGEX)) throw new Exception("Invalid Email.");
        if (phone == null || !phone.matches(PHONE_REGEX)) throw new Exception("Invalid Phone.");
        if (AFM == null || !AFM.matches(AFM_REGEX)) throw new Exception("Invalid AFM.");
        if(AFM != user.getAfm()){
             for(Account acc : BankSystem.getInstance().getAccountManager().getAccountsByOwner(user.getAfm())){
                 acc.setprimaryOwner(AFM); 
             }
            }

        user.setFirstName(fName);
        user.setLastName(lName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setUsername(userName);
        if((password!=null && password.length()>0&&!password.equals("-")))
            BankSystem.getInstance().getUserManager().getUserByUsername(userName).setPassword(User.hashPassword(password.toCharArray()));
        user.setAfm(AFM);   
    
    BankSystem.getInstance().getDaoHandler().saveAllData();
    }
    
     public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) {
        this.users = users;
        if (!this.users.contains(SuperAdmin.getInstance())) {
            this.users.add(SuperAdmin.getInstance());
        }
    }
    public List<User> getCustomers() {
        List<User> customers = new ArrayList<>();
        for (User u : users) {
            if (u instanceof model.Customer) customers.add(u);
        }
        return customers;
    }
    public List<User> getAdmins() {
        List<User> admins = new ArrayList<>();
        for (User u : users) {
            if (u instanceof model.Admin) admins.add(u);
        }
        return admins;
    }
}