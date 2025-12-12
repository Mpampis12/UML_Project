package view;

import model.User;
import model.Account;
import services.BankSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class DashboardPage extends JPanel {

    private BankBridge navigation;
    private User user;

    public DashboardPage(BankBridge navigation, User user) {
        this.navigation = navigation;
        this.user = user;

        setLayout(new BorderLayout());

        // --- HEADER (Πάνω μέρος) ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFirstName() + " " + user.getLastName(), SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel roleLabel = new JLabel("Role: " + user.getRole(), SwingConstants.CENTER);
        roleLabel.setForeground(Color.LIGHT_GRAY);

        headerPanel.add(welcomeLabel);
        headerPanel.add(roleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- CENTER (Λίστα Λογαριασμών) ---
        // Χρησιμοποιούμε TextArea για απλότητα, ή JList για πιο ωραία εμφάνιση
        JTextArea accountsArea = new JTextArea();
        accountsArea.setEditable(false);
        accountsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        // Φόρτωση λογαριασμών
        StringBuilder sb = new StringBuilder();
        sb.append("--- MY ACCOUNTS ---\n\n");
        
        List<Account> myAccounts = BankSystem.getInstance().getAccountManager().getAccountsByOwner(user.getAfm());
        
        if (myAccounts.isEmpty()) {
            sb.append("No accounts found.");
        } else {
            for (Account acc : myAccounts) {
                sb.append(String.format("IBAN: %s\nType: %s\nBalance: %.2f €\n\n", 
                          acc.getIban(), acc.getAccountType(), acc.getBalance()));
            }
        }
        accountsArea.setText(sb.toString());
        
        add(new JScrollPane(accountsArea), BorderLayout.CENTER);

        // --- BOTTOM (Κουμπιά Ενεργειών) ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        JButton historyBtn = new JButton("History");
        JButton paymentBtn = new JButton("Pay Bill");
        JButton logoutBtn = new JButton("Logout");

        // Χρωματισμός Logout
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);

        // Προσθήκη στο Panel
        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(transferBtn);
        buttonPanel.add(paymentBtn);
        buttonPanel.add(historyBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- ACTIONS ---
        
        // Logout Logic
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                navigation.showLogin();
            }
        });

        // Placeholder actions for now
        depositBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Deposit feature coming soon!"));
        withdrawBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Withdraw feature coming soon!"));
        transferBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Transfer feature coming soon!"));
    }
}