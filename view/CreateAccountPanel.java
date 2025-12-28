package view;

import javax.swing.*;
import java.awt.*;
import services.BankSystem;
import model.User;
import model.Account;
import model.Customer;

public class CreateAccountPanel extends JPanel {

    public CreateAccountPanel(User user) {
        setLayout(new GridBagLayout());
        setBackground(StyleHelpers.MUSTARD_BG); // Το κίτρινο φόντο του θέματος

        // Κεντρικό Panel (Λευκό & Στρογγυλεμένο)
        JPanel card = new StyleHelpers.RoundedPanel(30, Color.WHITE);
        card.setLayout(new GridLayout(5, 1, 10, 20));
        card.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        card.setPreferredSize(new Dimension(400, 350));

        // Τίτλος
        JLabel lblTitle = new JLabel("Open New Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

        JLabel lblMsg = new JLabel("Select Account Type:", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Επιλογή Τύπου
        String[] types = {"PERSONAL", "BUSINESS"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeCombo.setBackground(Color.WHITE);

        // Κουμπί Επιβεβαίωσης
        JButton btnConfirm = StyleHelpers.createRoundedButton("Confirm & Create");
        
        // --- LOGIC ---
        btnConfirm.addActionListener(e -> {
            try {
                String selectedType = (String) typeCombo.getSelectedItem();
                String afm = user.getAfm();

                // 1. Δημιουργία μέσω του AccountManager (που το αποθηκεύει στη λίστα του)
                Account newAcc = BankSystem.getInstance().getAccountManager().createAccount(selectedType, 0.0, afm);

                // 2. Σύνδεση του λογαριασμού με τον Customer (ενημέρωση λίστας IBANs πελάτη)
                if (user instanceof Customer) {
                    ((Customer) user).setNewAccountIban(newAcc.getIban());
                }

                // 3. Αποθήκευση στη βάση (JSON)
                BankSystem.getInstance().getDaoHandler().saveAllData();

                JOptionPane.showMessageDialog(this, "Account Created Successfully!\nIBAN: " + newAcc.getIban());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Προσθήκη στο Card
        card.add(lblTitle);
        card.add(lblMsg);
        card.add(typeCombo);
        card.add(new JLabel("")); // Spacer
        card.add(btnConfirm);

        add(card);
    }
}