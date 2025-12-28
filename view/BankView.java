package view;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

import model.Account;
import model.User;
import services.BankSystem;
import services.TimeSimulator;

public class BankView extends JFrame implements BankBridge {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel dateLabel; 


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
        
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(40, 40, 40)); // Dark Gray Header
        topBar.setPreferredSize(new Dimension(1100, 30));

        JLabel appNameLabel = new JLabel("  Bank of TUC System");
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        dateLabel = new JLabel("Loading Date...  ");
        dateLabel.setForeground(new Color(255, 200, 0)); // Yellow text
        dateLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topBar.add(appNameLabel, BorderLayout.WEST);
        topBar.add(dateLabel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH); // Προσθήκη πάνω-πάνω στο Frame
 
        
        loginPageScreen = new LoginPage(this);
        registerPageScreen= new RegisterPage(this);
        
        mainPanel.add(loginPageScreen, "LOGIN");
        mainPanel.add(registerPageScreen,"REGISTER");
        
        add(mainPanel);
        setVisible(true);

        initTimeSimulator();
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

    private void initTimeSimulator() {
        TimeSimulator timer = BankSystem.getInstance().getTimeSimulator();
        
        updateDateLabel(timer.getCurrentDate());
        timer.setDateChangeListener(newDate -> {
            SwingUtilities.invokeLater(() -> updateDateLabel(newDate));
        });

        Thread timeThread = new Thread(timer);
        timeThread.start();
    }

    private void updateDateLabel(java.time.LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy");
        dateLabel.setText(date.format(formatter) + "  ");
    }


    @Override
    public void showCreateAccountConfirmation(User user) {
        CreateAccountPanel createPage = new CreateAccountPanel(user);
        mainPanel.add(createPage, "CREATE_ACC_CONFIRM");
        cardLayout.show(mainPanel, "CREATE_ACC_CONFIRM");
        setTitle("Bank of TUC - Confirm Account Creation");
    }

 
}