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
        setSize(1400, 700);
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


    @Override
    public void showCreateAccountConfirmation(User user) {
        CreateAccountPage createPage = new CreateAccountPage(this, user);
        mainPanel.add(createPage, "CREATE_ACC_CONFIRM");
        cardLayout.show(mainPanel, "CREATE_ACC_CONFIRM");
        setTitle("Bank of TUC - Confirm Account Creation");
    }

    // Η μέθοδος αυτή καλείται από τα κουμπιά Payment/Standing Order
    @Override
    public void showTransactionPage(User user, String initialTab) {
        TransactionPage transPage = new TransactionPage(this, user, initialTab);
        mainPanel.add(transPage, "TRANSACTION_PAGE");
        cardLayout.show(mainPanel, "TRANSACTION_PAGE");
        
    }

}