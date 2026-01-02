package view;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import control.BankController;
import services.BankSystem;

public class DepositPanel extends JPanel {
    public DepositPanel() {
        setLayout(new GridBagLayout());
        setBackground(StyleHelpers.MUSTARD_BG);
        
        JPanel card = new StyleHelpers.RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridLayout(7, 2, 10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        JTextField ibanField = new JTextField();
        JTextField firstNamTextField = new JTextField();
        JTextField lastNameField = new JTextField();

        JTextField amountField = new JTextField();
        JButton submitBtn = StyleHelpers.createRoundedButton("Deposit Cash");
         JLabel title = new JLabel("Deposit Cash from Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        card.add(title);
        card.add(new JLabel(""));
        card.add(new JLabel("First Name of Account:"));
        card.add(firstNamTextField);
        card.add(new JLabel("Last Name of Account:"));
        card.add(lastNameField);
        card.add(new JLabel("Target IBAN:")); 
        card.add(ibanField);
        card.add(new JLabel("Amount (€):")); 
        card.add(amountField);
        card.add(new JLabel("")); 
        card.add(submitBtn);

        submitBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText());
                 String iban = ibanField.getText();
                 String fName = firstNamTextField.getText();
                    String lName = lastNameField.getText();
                 ArrayList<String> accOwners = new ArrayList<String>();
                accOwners= (ArrayList<String>) BankSystem.getInstance().getAccountManager().getAccount(iban).getOwners() ;
                    for (String ownerAfm : accOwners) {
                        String ownerFName = BankSystem.getInstance().getUserManager().getUserByAfm(ownerAfm).getFirstName();
                        String ownerLName = BankSystem.getInstance().getUserManager().getUserByAfm(ownerAfm).getLastName();
                        if (ownerFName.equals(fName) && ownerLName.equals(lName)) {
                             
                            BankSystem.getInstance().getTransactionManager().deposit(
                                ibanField.getText(),
                                amt,
                                "Deposit via Bank Branch",
                                java.time.LocalDateTime.now()
                            );
                            JOptionPane.showMessageDialog(this, "Deposit Successful!");
                            return; // Exit after successful deposit
                        }
                    }
                 JOptionPane.showMessageDialog(this, "Account owners' names do not match.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        add(card);
    }
}