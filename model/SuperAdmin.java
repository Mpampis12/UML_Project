package model;

public class SuperAdmin extends User{

    private  static SuperAdmin instance;
    private String roleString;


    public static synchronized SuperAdmin getInstance() {//Synchronized gia na min skasei se m
        if(instance == null ){
            instance = new SuperAdmin();
        }
        return instance;
    }

    private SuperAdmin() {
      
        super();
        super.setUsername("root");  
        super.setFirstName("System");
        super.setLastName("Root");
        super.setAfm("000000000");
        super.setEmail("root@bank.gr");
        super.setPhone("0000000000");
        super.setPassword(User.hashPassword("superAdmin123!".toCharArray()));
        roleString="SuperAdmin";
    }

    @Override
    public String getRole() {
        return roleString;
    }

    @Override
    public String getDetails() {
      return roleString;
    }
    
}
