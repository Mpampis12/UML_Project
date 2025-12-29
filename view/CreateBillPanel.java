package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.stream.Collectors;
import control.BankController;
import model.Account;
import model.User;
import view.StyleHelpers.*; 

public class CreateBillPanel extends JPanel {
    
    private BankController controller;

    public CreateBillPanel(User user) {
        this.controller = new BankController();
        setLayout(new GridBagLayout()); // Αυτό είναι για το κεντράρισμα του Panel στη μέση, είναι OK
        setBackground(StyleHelpers.MUSTARD_BG);

        String businessAfm = user.getAfm();
        java.util.List<Account> myAccounts = controller.getAccountsForUser(user);
        java.util.List<Account> businessAccounts = myAccounts.stream()
            .filter(acc -> acc.getAccountType() == Account.AccountType.BUSINESS)
            .collect(Collectors.toList());

        RoundedPanel panel = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        panel.setBorder(new EmptyBorder(90, 40, 90, 40));
        panel.setLayout(new BorderLayout(0, 20));

        JLabel title = new JLabel("Issue New Bill");
        title.setFont(StyleHelpers.FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);
        
        // Χρήση BoxLayout (Κάθετα)
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);

        JComboBox<String> ibanSelector = new JComboBox<>();
        if (businessAccounts.isEmpty()) {
            ibanSelector.addItem("No Business Accounts Found");
            ibanSelector.setEnabled(false);
        } else {
            for (Account acc : businessAccounts) ibanSelector.addItem(acc.getIban());
        }
        ibanSelector.setFont(StyleHelpers.FONT_PLAIN);
        ibanSelector.setBackground(Color.WHITE);

        JTextField fPayerAfm = new RoundedTextField(15);
        JTextField fAmount = new RoundedTextField(15);
        JTextField fDesc = new RoundedTextField(15);
        JLabel fBusinessAfm = StyleHelpers.createLabel(businessAfm); 

        formContainer.add(createRow("Credit Account (IBAN):", ibanSelector));
        formContainer.add(createRow("Issuer AFM:", fBusinessAfm));
        formContainer.add(createRow("Target Payer AFM:", fPayerAfm));
        formContainer.add(createRow("Amount (€):", fAmount));
        formContainer.add(createRow("Description:", fDesc));
        
        panel.add(formContainer, BorderLayout.CENTER);

        JButton submitBtn = StyleHelpers.createRoundedButton("Issue Bill");
        
        // Panel για το κουμπί (για να μην τεντώσει)
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnP.setOpaque(false);
        btnP.add(submitBtn);
        panel.add(btnP, BorderLayout.SOUTH);
        submitBtn.addActionListener(e -> {
            try {
                if (!ibanSelector.isEnabled()) throw new Exception("No valid business account to credit.");
                String targetIban = (String) ibanSelector.getSelectedItem();
                String payer = fPayerAfm.getText();
                String desc = fDesc.getText();
                if (fAmount.getText().isEmpty()) throw new Exception("Enter Amount");
                double amount = Double.parseDouble(fAmount.getText());

                String newRf = controller.createBill(targetIban, user.getAfm(), amount, desc, payer);

                JOptionPane.showMessageDialog(this, 
                    "Bill Created!\nRF: " + newRf + "\nCredited to: " + targetIban);
                
                fAmount.setText("");
                fDesc.setText("");
                fPayerAfm.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        add(panel);
    }
    // Αντιγραφή της μεθόδου createRow κι εδώ
    private JPanel createRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setOpaque(false);
        JLabel lbl = StyleHelpers.createLabel(labelText);
        lbl.setPreferredSize(new Dimension(160, 30)); // Λίγο πιο φαρδύ label εδώ
        row.add(lbl);
        row.add(field);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return row;
    }
}