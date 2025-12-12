package view;

import services.BankSystem;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JPanel {
    
    private JTextField userField;
    private JPasswordField passField;
    private BankBridge navigation; // Αναφορά στο κεντρικό παράθυρο

   
    public LoginPage(BankBridge navigation) {
        this.navigation = navigation;
        
        // Ρυθμίσεις εμφάνισης (Layout)
        setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Κενά γύρω από τα κουμπιά
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Τίτλος
        JLabel title = new JLabel("Welcome to Bank of TUC");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);
        
        // Πεδίο Username
        gbc.gridwidth = 1; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        
        userField = new JTextField(15);
        gbc.gridx = 1;
        add(userField, gbc);
        
        // Πεδίο Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        
        passField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passField, gbc);
        
        // Κουμπί Login
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Να μην πιάσει όλο το πλάτος
        add(loginBtn, gbc);
        
        // Λειτουργία Κουμπιού (Action Listener)
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }
    
    private void performLogin() {
        String username = userField.getText();
        char[] password = passField.getPassword();
        
        // Καλούμε τον UserManager από το Singleton BankSystem
        // Προσοχή: Βεβαιώσου ότι έχεις SuperAdmin ή Customer στο σύστημα
        User user = BankSystem.getInstance().getUserManager().login(username, password);
        
        if (user != null) {
            // Καθαρισμός πεδίων
            userField.setText("");
            passField.setText("");
            
            // Επιτυχία! Ζητάμε από το BankView να μας πάει στο Dashboard
            navigation.showDashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}