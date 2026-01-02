package view;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.stream.Collectors;
import control.BankController;
import model.Account;
import model.User;

public class CreateBillPanel extends JPanel {
    
    private BankController controller;

    public CreateBillPanel(User user) {
        // 1. Βασικό Layout για κεντράρισμα (όπως στο DepositPanel)
        setLayout(new GridBagLayout());
        setBackground(StyleHelpers.MUSTARD_BG);
        
        this.controller = new BankController();

        // Προετοιμασία δεδομένων (Business Accounts)
        java.util.List<Account> myAccounts = controller.getAccountsForUser(user);
        java.util.List<Account> businessAccounts = myAccounts.stream()
            .filter(acc -> acc.getAccountType() == Account.AccountType.BUSINESS)
            .collect(Collectors.toList());

        // 2. Δημιουργία της "Κάρτας" με GridLayout (7 γραμμές, 2 στήλες)
        JPanel card = new StyleHelpers.RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridLayout(7, 2, 10, 10)); // 10px κενό οριζόντια/κάθετα
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 3. Δημιουργία Components
        JLabel title = new JLabel("Issue New Bill");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        // Πεδίο Issuer AFM (κλειδωμένο, δείχνει το ΑΦΜ του χρήστη)
        JTextField issuerAfmField = new JTextField(user.getAfm());

        issuerAfmField.setEditable(false);
        issuerAfmField.setEnabled(false);
        issuerAfmField.setBackground(new Color(245, 245, 245)); // Γκριζαρισμένο

        // Dropdown για επιλογή λογαριασμού πίστωσης
        JComboBox<String> ibanSelector = new JComboBox<>();
        if (businessAccounts.isEmpty()) {
            ibanSelector.addItem("No Business Accounts");
            ibanSelector.setEnabled(false);
        } else {
            for (Account acc : businessAccounts) {
                ibanSelector.addItem(acc.getIban());
            }
        }
        
        JTextField payerAfmField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField descField = new JTextField();
        
        JButton submitBtn = StyleHelpers.createRoundedButton("Issue Bill");

        // 4. Προσθήκη στην κάρτα (σειρά-σειρά)
        
        // Row 1: Τίτλος
        card.add(title);
        card.add(new JLabel("")); // Κενό κελί για να γεμίσει τη γραμμή

        // Row 2: Issuer AFM
        card.add(new JLabel("Issuer AFM:"));
        card.add(issuerAfmField);

        // Row 3: Credit Account
        card.add(new JLabel("Credit Account (IBAN):"));
        card.add(ibanSelector);

        // Row 4: Target Payer
        card.add(new JLabel("Target Payer AFM:"));
        card.add(payerAfmField);

        // Row 5: Amount
        card.add(new JLabel("Amount (€):"));
        card.add(amountField);

        // Row 6: Description
        card.add(new JLabel("Description:"));
        card.add(descField);

        // Row 7: Button (δεξιά)
        card.add(new JLabel("")); // Κενό κελί αριστερά
        card.add(submitBtn);

        submitBtn.addActionListener(e -> {
            try {
                if (!ibanSelector.isEnabled()) throw new Exception("No valid business account.");
                
                String targetIban = (String) ibanSelector.getSelectedItem();
                String payer = payerAfmField.getText();
                String desc = descField.getText();
                
                if (amountField.getText().isEmpty()) throw new Exception("Enter Amount");
                double amount = Double.parseDouble(amountField.getText());

                // Κλήση του Controller
                String newRf = controller.createBill(targetIban, user.getAfm(), amount, desc, payer);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(newRf), null);
             
                JOptionPane.showMessageDialog(this, 
                    "Bill Created Successfully!\nRF Code: " + newRf+" (copied to clipboard)");
                
                // Καθαρισμός πεδίων
                amountField.setText("");
                descField.setText("");
                payerAfmField.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Προσθήκη της κάρτας στο κεντρικό panel
        add(card);
    }
}