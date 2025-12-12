package model;

public class Admin extends User{

    private String userRoleString;
 

	public Admin() {
        userRoleString = "Admin";
    }


    public Admin(String username, char[] password, String firstName, String lastName, String afm, String email,
            String phone) {
        super(username, password, firstName, lastName, afm, email, phone);
        userRoleString = "Admin";
    }


    @Override
	public String getRole() {
		return userRoleString;
	}

    @Override
    public String getDetails() {
       String detailString="Admin{" + getUsername() + "}";
       return detailString;
    }
}
