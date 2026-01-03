package view;

import javax.swing.*;

import control.BankController;

import java.awt.*;
import services.BankSystem;
import model.User;
import model.Account;
import model.Customer;
import model.Individual;

public class CreateAccountPanel extends JPanel {

    private BankController controller;
    public CreateAccountPanel(User user) {
        setLayout(new GridBagLayout());
        setBackground(StyleHelpers.MUSTARD_BG); // Το κίτρινο φόντο του θέματος
        this.controller = BankController.getInstance();
        // Κεντρικό Panel (Λευκό & Στρογγυλεμένο)
        JPanel card = new StyleHelpers.RoundedPanel(30, Color.WHITE);
        card.setLayout(new GridLayout(5, 1, 10, 20));
        card.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        card.setPreferredSize(new Dimension(400, 350));

        // Τίτλος
        JLabel lblTitle = new JLabel("Open New Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

 
        
        // Κουμπί Επιβεβαίωσης
        JButton btnConfirm = StyleHelpers.createRoundedButton("Confirm & Create");
        
        // --- LOGIC ---
        btnConfirm.addActionListener(e -> {
            try {
                String selectedType ;
                if(user instanceof Individual){
                      selectedType = "PERSONAL";
                }
                else{
                      selectedType =  "BUSINESS";
                }
               String afm = user.getAfm();

                 
                 Account newAcc = controller.createAccountForUser(user, selectedType, 0.0,afm);

                 if (user instanceof Customer) {
                    ((Customer) user).setNewAccountIban(newAcc.getIban());
                }

                 controller.saveData();
                JOptionPane.showMessageDialog(this, "Account Created Successfully!\nIBAN: " + newAcc.getIban());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Προσθήκη στο Card
        card.add(lblTitle);
 
        card.add(btnConfirm);

        add(card);
    }
}