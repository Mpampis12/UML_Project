package view;

import model.User;
import model.Account;
import model.StandingOrder;
import model.StandingOrder.StandingOrderPurpose;
import model.Iban;
import control.BankController;
import services.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TransactionPage extends JPanel {

    private BankBridge navigation;
    private User user;
    private String type; // "PAYMENT" or "STANDING_ORDER"
    private BankController controller;

    // Χρώματα
    private static final Color MUSTARD_BG = new Color(228, 196, 101);
    private static final Color BUTTON_YELLOW = new Color(255, 180, 0);

    public TransactionPage(BankBridge navigation, User user, String type) {
        this.navigation = navigation;
        this.user = user;
        this.type = type;
        this.controller = new BankController();

        setLayout(new BorderLayout());

        // --- HEADER (Copy από Dashboard για ομοιομορφία) ---
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            Image bg = new ImageIcon("services/background.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 150)); // Πιο κοντό Header
        
        JLabel titleLabel = new JLabel(type.equals("PAYMENT") ? "Pay a Bill" : "New Standing Order");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 30, 0, 0));
        
        JButton backBtn = new JButton("Back");
        backBtn.setBackground(BUTTON_YELLOW);
        backBtn.addActionListener(e -> navigation.showDashboard(user));
        
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRight.setOpaque(false);
        topRight.add(backBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(topRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN FORM ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(MUSTARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 1. SELECT ACCOUNT
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Account:"), gbc);
        
        JComboBox<String> accountBox = new JComboBox<>();
        List<Account> accounts = controller.getAccountsForUser(user);
        for(Account acc : accounts) {
            accountBox.addItem(acc.getIban());
        }
        gbc.gridx = 1;
        formPanel.add(accountBox, gbc);

        // 2. FIELDS BASED ON TYPE
        JTextField rfField = new JTextField(15);
        JTextField amountField = new JTextField(10);
        JTextField targetIbanField = new JTextField(15); // Για Standing Order Transfer
        
        if (type.equals("PAYMENT")) {
            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("RF Code:"), gbc);
            gbc.gridx = 1;
            formPanel.add(rfField, gbc);
            
            // Το ποσό συνήθως το παίρνει αυτόματα από το Bill, αλλά ας το βάλουμε για testing αν θες
            // ή θα το αφήσουμε να το βρει ο Manager. Στο BillManager.payBill παίρνει το ποσό από το Bill object.
            
        } else {
            // STANDING ORDER UI
            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("Target IBAN (or RF):"), gbc);
            gbc.gridx = 1;
            formPanel.add(targetIbanField, gbc);
            
            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("Amount (€):"), gbc);
            gbc.gridx = 1;
            formPanel.add(amountField, gbc);
            
            // Θα μπορούσες να βάλεις και Frequency εδώ
        }

        // 3. SUBMIT BUTTON
        JButton submitBtn = new JButton("Submit Transaction");
        submitBtn.setBackground(new Color(0, 102, 204));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setPreferredSize(new Dimension(200, 40));
        
        submitBtn.addActionListener(e -> {
            String selectedIban = (String) accountBox.getSelectedItem();
            
            if (type.equals("PAYMENT")) {
                try {
                    controller.payBill(rfField.getText(), selectedIban, user.getAfm());
                    JOptionPane.showMessageDialog(this, "Bill Paid Successfully!");
                    navigation.showDashboard(user);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Payment Failed: " + ex.getMessage());
                }
            } else {
                // LOGIC GIA STANDING ORDER
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    String target = targetIbanField.getText();
                    
                    // Απλοϊκή δημιουργία Standing Order (Transfer)
                    // ΠΡΟΣΟΧΗ: Εδώ πρέπει να φτιάξεις σωστά το StandingOrder Object
                    // Αυτό είναι παράδειγμα:
                    StandingOrder so = new StandingOrder(new Iban(selectedIban), new Iban(target),amount,"Monthly Rent", StandingOrderPurpose.TRANSFER);
                    
                    controller.createStandingOrder(so);
                    JOptionPane.showMessageDialog(this, "Standing Order Created!");
                    navigation.showDashboard(user);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.CENTER;
        formPanel.add(submitBtn, gbc);

        add(formPanel, BorderLayout.CENTER);
    }
}