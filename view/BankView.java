package view;

import javax.swing.*;
import java.awt.*;
import model.User;

public class BankView extends JFrame implements BankBridge {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Οι σελίδες μας (τις κρατάμε ως Components)
    private LoginPage loginPageScreen; // Άλλαξα το όνομα για να μην μπερδεύεται με την κλάση
    private DashboardPage dashboardPageScreen;
    
    public BankView() {
        super("Bank of TUC");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Κέντρο της οθόνης
        
        // Setup CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // 1. Αρχικοποίηση Login Page
        // Περνάμε το 'this' (BankBridge) για να μπορεί να καλέσει το showDashboard
        loginPageScreen = new LoginPage(this);
        
        // Προσθήκη στο Panel με όνομα "LOGIN"
        mainPanel.add(loginPageScreen, "LOGIN");
        
        add(mainPanel);
        setVisible(true);
        
        // Ξεκινάμε δείχνοντας το Login
        showLogin();
    }

    @Override
    public void showLogin() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    @Override
    public void showDashboard(User user) {
        // Δημιουργούμε το Dashboard δυναμικά κάθε φορά που μπαίνει ο χρήστης
        // ώστε να έχει τα φρέσκα δεδομένα του συγκεκριμένου User
        dashboardPageScreen = new DashboardPage(this, user);
        
        // Το προσθέτουμε στο mainPanel
        mainPanel.add(dashboardPageScreen, "DASHBOARD");
        
        // Το δείχνουμε
        cardLayout.show(mainPanel, "DASHBOARD");
        setTitle("Bank of TUC - Dashboard: " + user.getUsername());
    }

    @Override
    public void showHistory(User user) {
        // Θα υλοποιηθεί στο επόμενο βήμα
        JOptionPane.showMessageDialog(this, "History Page under construction");
    }

    @Override
    public void showTransactions(User user) {
        // Θα υλοποιηθεί στο επόμενο βήμα
        JOptionPane.showMessageDialog(this, "Transaction Page under construction");
    }
}