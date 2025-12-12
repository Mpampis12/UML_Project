package services;
 
import model.User;
import model.Customer;
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

 
    public void registerCustomer(String username, char[] password, String firstName, String lastName, String afm, String email, String phone) throws Exception {
        
         if (getUserByAfm(afm) != null) {
            throw new Exception("Already user with this afm.");
        }
        if (getUserByUsername(username) != null) {
            throw new Exception("Username already used");
        }
 
        Customer newCustomer = new Customer(username, password, firstName, lastName, afm, email, phone);
        
       
        this.users.add(newCustomer);
        System.out.println("Success register costumer: " + username);
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
}