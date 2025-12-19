package view;

import javax.swing.*;
import java.awt.*;
import services.BankSystem;
import model.User;

public class CreateAccountPage extends JPanel {

    public CreateAccountPage(BankView view, User user) {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        JPanel box = new JPanel(new GridLayout(4, 1, 10, 20));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        box.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Open New Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel lblMsg = new JLabel("Do you really want to open a new account?", SwingConstants.CENTER);
        
        // Επιλογή τύπου (Προαιρετικό, αλλιώς default Personal)
        String[] types = {"PERSONAL", "BUSINESS"};
        JComboBox<String> typeCombo = new JComboBox<>(types);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnConfirm = new JButton("Confirm");
        JButton btnCancel = new JButton("Cancel");

        btnConfirm.setBackground(new Color(40, 167, 69)); // Green
        btnConfirm.setForeground(Color.WHITE);
        
        btnCancel.setBackground(new Color(220, 53, 69)); // Red
        btnCancel.setForeground(Color.WHITE);

        // ACTIONS
        btnCancel.addActionListener(e -> view.showDashboard(user));

        btnConfirm.addActionListener(e -> {
            try {
                String selectedType = (String) typeCombo.getSelectedItem();
                // Κλήση στο Service/Factory
                services.AccountFactory.createAccount(selectedType, 0.0, user.getAfm()); 
                // Εδώ κανονικά πρέπει να καλέσεις τον AccountManager για να το προσθέσει στη λίστα!
                // π.χ. Account newAcc = AccountFactory...; 
                // BankSystem.getInstance().getAccountManager().addAccount(newAcc);
                // Αλλά ας υποθέσουμε ότι το κάνει ο Controller ή το Factory (ανάλογα την υλοποίηση σου).
                // ΣΗΜΑΝΤΙΚΟ: Σώσε τα δεδομένα
                BankSystem.getInstance().getDaoHandler().saveAllData();
                
                JOptionPane.showMessageDialog(this, "Account Created Successfully!");
                view.showDashboard(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);

        box.add(lblTitle);
        box.add(lblMsg);
        box.add(typeCombo);
        box.add(btnPanel);

        add(box);
    }
}