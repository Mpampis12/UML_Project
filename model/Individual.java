package model;

public class Individual extends Customer {

    public Individual(String username, char[] password, String firstName, String lastName, String afm, String email, String phone) {
        super(username, password, firstName, lastName, afm, email, phone);
    }
    
    // Default Constructor for JSON/DAO tools
    public Individual() { super(); }

    @Override
    public String getRole() {
        return "INDIVIDUAL";
    }
}