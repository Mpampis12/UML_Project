package view;

import javax.swing.*;
import java.awt.*;

import model.Account;
import model.User;

public class BankView extends JFrame implements BankBridge {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
     private LoginPage loginPageScreen;  
    private DashboardPage dashboardPageScreen;
    private RegisterPage registerPageScreen;
    
    public BankView() {
        super("Bank of TUC");
        setSize(1000, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  
        
        
        
        cardLayout = new CardLayout();
        Image bgImage = new ImageIcon("services/background2.jpg").getImage();

        
        
        mainPanel = new JPanel(cardLayout) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        
 
        
        loginPageScreen = new LoginPage(this);
        registerPageScreen= new RegisterPage(this);
        
        mainPanel.add(loginPageScreen, "LOGIN");
        mainPanel.add(registerPageScreen,"REGISTER");
        
        add(mainPanel);
        setVisible(true);
        
        showLogin();
    }


    @Override
    public void showLogin() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    @Override
    public void showDashboard(User user) {
        
        dashboardPageScreen = new DashboardPage(this, user);
        
         mainPanel.add(dashboardPageScreen, "DASHBOARD");
        
         cardLayout.show(mainPanel, "DASHBOARD");
        setTitle("Bank of TUC - Dashboard: " + user.getUsername());
    }

        @Override
    public void showRegister() {
        cardLayout.show(mainPanel, "REGISTER");
    }

    @Override
    public void showHistory(User user) {
         JOptionPane.showMessageDialog(this, "History Page under construction");
    }

    @Override
    public void showTransactions(User user) {
         JOptionPane.showMessageDialog(this, "Transaction Page under construction");
    }
    @Override
    public void showAccountDetails(User user, Account account) {
        // Δημιουργούμε τη σελίδα λεπτομερειών
        AccountDetailsPage detailsScreen = new AccountDetailsPage(this, user, account);
        
        // Την προσθέτουμε στο CardLayout
        mainPanel.add(detailsScreen, "DETAILS");
        
        // Την εμφανίζουμε
        cardLayout.show(mainPanel, "DETAILS");
        setTitle("Bank of TUC - Account Details: " + account.getIban());
    }
}