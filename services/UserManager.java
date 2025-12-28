package services;
 
import model.User;
import model.Account;
import model.Customer;
import model.Individual; // Νέο import
import model.Business;   // Νέο import
import model.SuperAdmin;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private List<User> users;

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

    // Εγγραφή Ιδιώτη (Individual)
    public void registerCustomer(String username, char[] password, String firstName, String lastName, String afm, String email, String phone) throws Exception {
        if (getUserByAfm(afm) != null) throw new Exception("Already user with this afm.");
        if (getUserByUsername(username) != null) throw new Exception("Username already used");
 
        // Δημιουργία INDIVIDUAL
        Individual newCustomer = new Individual(username, password, firstName, lastName, afm, email, phone);
        
        Account defAccount = BankSystem.getInstance().getAccountManager().createAccount("PERSONAL", 0.0, afm);
        if (defAccount != null) {
            newCustomer.setNewAccountIban(defAccount.getIban());
        }

        this.users.add(newCustomer);
        BankSystem.getInstance().getDaoHandler().saveAllData();
        System.out.println("Success register Individual: " + username);
    }

    // Εγγραφή Επιχείρησης (Business)
    public void registerCustomerBuisness(String username, char[] password, String firstName, String lastName, String afm, String email, String phone) throws Exception {
        if (getUserByAfm(afm) != null) throw new Exception("Already user with this afm.");
        if (getUserByUsername(username) != null) throw new Exception("Username already used");
 
        // Δημιουργία BUSINESS
        Business newCustomer = new Business(username, password, firstName, lastName, afm, email, phone);
        
        Account defAccount = BankSystem.getInstance().getAccountManager().createAccount("BUSINESS", 0.0, afm);
        if (defAccount != null) {
            newCustomer.setNewAccountIban(defAccount.getIban());
        }

        this.users.add(newCustomer);
        BankSystem.getInstance().getDaoHandler().saveAllData();
        System.out.println("Success register Business: " + username);
    }

    public void registerAdmin(String username, char[] password, String firstName, String lastName, String email) throws Exception {
        if (getUserByUsername(username) != null) throw new Exception("Username already exists.");
        
        model.Admin newAdmin = new model.Admin(username, password, firstName, lastName, "000000000", email, "0000000000");
        this.users.add(newAdmin);
        BankSystem.getInstance().getDaoHandler().saveAllData();
    }
   
    public User getUserByAfm(String afm) {
        for (User u : users) {
            if (u.getAfm().equals(afm)) return u;
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    public List<User> getUsers() { return users; }
    
    public void setUsers(List<User> users) {
        this.users = users;
        if (!this.users.contains(SuperAdmin.getInstance())) {
            this.users.add(SuperAdmin.getInstance());
        }
    }
    
    // Επιστρέφει όλους τους Customers (Individuals + Businesses)
    public List<User> getCustomers() {
        List<User> customers = new ArrayList<>();
        for (User u : users) {
            if (u instanceof model.Customer) {
                customers.add(u);
            }
        }
        return customers;
    }

    public List<User> getAdmins() {
        List<User> admins = new ArrayList<>();
        for (User u : users) {
            if (u instanceof model.Admin) {
                admins.add(u);
            }
        }
        return admins;
    }

    public void updateUser(User user, String fName, String lName, String email, String phone) {
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setEmail(email);
        user.setPhone(phone);
        BankSystem.getInstance().getDaoHandler().saveAllData();
    }
}