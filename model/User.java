package model;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.regex.Pattern;

public abstract class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String afm;  
    private String email;
    private String phone;


    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*?]).{10,}$";
    
  
    public User() {

    }

    public User(String username, char[] password, String firstName, String lastName, String afm, String email,String phone) {
        this.username = username;
        this.password = hashPassword(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.afm = afm;
        this.email = email;
        this.phone = phone;
    }

        
    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;

    }

    public String getPassword() {

        return password;

    }
    
    public void setPassword(String password) {

        this.password = password;

    }
    public String getFirstName() {

        return firstName;

    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;

    }

    public String getLastName() {

        return lastName;

    }
    
    public void setLastName(String lastName) {

        this.lastName = lastName;

    }

    public String getAfm() {

        return afm;

    }

    public boolean setAfm(String afm) {

        if(afm.toCharArray().length==9){

            this.afm = afm;
            return true;

        }
        else{

            this.afm = null;

            return false;

        }
    }
    public String getEmail() {

        return email;

    }

    public boolean setEmail(String email) {

        if(email.contains("@")&&(email.contains(".gr")||email.contains(".com"))){
                this.email = email;
                return true;
        }else{
            System.out.println("Wrong Email");
            return false;
        }

    }

    public String getPhone() {

        return phone;

    }

    public boolean setPhone(String phone) {

        if(phone.length()==10){
        this.phone = phone;

        return true;

        }else{

            System.out.println("Wrong phone");
            return false;

        }
    }

    public abstract String getRole();

    public abstract String getDetails();
    

    public static boolean isValidPassword(char[] password) {

        if (password == null) return false;

        
        CharBuffer buffer = CharBuffer.wrap(password);
        return Pattern.matches(PASSWORD_REGEX, buffer);
    }
    
    public static String hashPassword(char[] password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
             ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
            byte[] encodedhash = digest.digest(byteBuffer.array());
            
     
            Arrays.fill(byteBuffer.array(), (byte) 0);

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Error crypto", e);
        }
    }
    
}