package model;

public class Business extends Customer {

    public Business(String username, char[] password, String firstName, String lastName, String afm, String email, String phone) {
        super(username, password, firstName, lastName, afm, email, phone);
    }

    // Default Constructor
    public Business() { super(); }

    @Override
    public String getRole() {
        return "BUSINESS";
    }
}