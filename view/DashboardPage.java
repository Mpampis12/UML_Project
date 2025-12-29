package view;

import model.*;
import services.BankSystem;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPage extends JPanel {

    private BankBridge navigation;
    private User user;
    private JPanel mainContentPanel; 
    private CardLayout cardLayout;   

    public DashboardPage(BankBridge navigation, User user) {
        this.navigation = navigation;
        this.user = user;
        setLayout(new BorderLayout());

        // --- HEADER (Standard) ---
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            Image bg = new ImageIcon("services/background.jpg").getImage();
            @Override protected void paintComponent(Graphics g) {
                if(bg!=null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 150));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Welcome Box
        JPanel welcomeBox = new StyleHelpers.RoundedPanel(20, StyleHelpers.BUTTON_YELLOW);
        welcomeBox.add(new JLabel("Welcome, " + user.getFirstName() + " (" + user.getRole() + ")"));
        
        JButton logoutBtn = StyleHelpers.createRoundedButton("Log Out");
        logoutBtn.addActionListener(e -> navigation.showLogin());

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        leftHeader.setOpaque(false);
        leftHeader.add(welcomeBox);
        leftHeader.add(logoutBtn);

        // --- NAVIGATION BUTTONS (DYNAMIC) ---
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setOpaque(false);

        // --- CONTENT PANEL ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(StyleHelpers.MUSTARD_BG);

        // --- ROLE BASED UI CONSTRUCTION ---
        
        if (user instanceof SuperAdmin) {
            // === SUPER ADMIN ===
            JButton manageAdminsBtn = StyleHelpers.createRoundedButton("Manage Admins");
            JButton createAdminBtn = StyleHelpers.createRoundedButton("Create Admin");
            
            navPanel.add(manageAdminsBtn);
            navPanel.add(createAdminBtn);
            
            // Panels
            List<User> admins = BankSystem.getInstance().getUserManager().getAdmins();
            mainContentPanel.add(new UserManagementPanel(admins), "MANAGE_ADMINS");
            mainContentPanel.add(new EmbeddedRegisterPanel("ADMIN"), "CREATE_ADMIN"); // Χρειάζεται μικρή προσαρμογή στο Embedded για Admin πεδία αν διαφέρουν

            manageAdminsBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "MANAGE_ADMINS"));
            createAdminBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "CREATE_ADMIN"));
            
            manageAdminsBtn.doClick();

        } else if (user instanceof Admin) {
            // === ADMIN ===
            JButton manageCustBtn = StyleHelpers.createRoundedButton("Manage Customers");
            JButton newIndivBtn = StyleHelpers.createRoundedButton("New Individual");
            JButton newBizBtn = StyleHelpers.createRoundedButton("New Business");
            JButton depositBtn = StyleHelpers.createRoundedButton("Deposit Cash");
            JButton withdrawBtn = StyleHelpers.createRoundedButton("Withdraw Cash");

            navPanel.add(manageCustBtn);
            navPanel.add(newIndivBtn);
            navPanel.add(newBizBtn);
            navPanel.add(depositBtn);
            navPanel.add(withdrawBtn);

            // Panels
            List<User> customers = BankSystem.getInstance().getUserManager().getCustomers();
            mainContentPanel.add(new UserManagementPanel(customers), "MANAGE_CUST");
            mainContentPanel.add(new EmbeddedRegisterPanel("PERSONAL"), "NEW_INDIV");
            mainContentPanel.add(new EmbeddedRegisterPanel("BUSINESS"), "NEW_BIZ");
            mainContentPanel.add(new DepositPanel(), "DEPOSIT");
            mainContentPanel.add(new WithdrawPanel(), "WITHDRAW");

            manageCustBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "MANAGE_CUST"));
            newIndivBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "NEW_INDIV"));
            newBizBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "NEW_BIZ"));
            depositBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "DEPOSIT"));
            withdrawBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "WITHDRAW"));
            
            manageCustBtn.doClick();

        } else if (user instanceof Customer) {
            // === CUSTOMER ===
            JButton homeBtn = StyleHelpers.createRoundedButton("Home");
            JButton transferBtn = StyleHelpers.createRoundedButton("Transfer");
            JButton payBtn = StyleHelpers.createRoundedButton("Payment");
            JButton soBtn = StyleHelpers.createRoundedButton("Standing Orders");
            JButton myAccBtn = StyleHelpers.createRoundedButton("New Account"); // Embedded Create Account
            
            navPanel.add(homeBtn);
            navPanel.add(transferBtn);
            navPanel.add(payBtn);
            navPanel.add(soBtn);
            navPanel.add(myAccBtn);

            // Business Only Feature
            if (user.getRole().contains("BUSINESS") || user instanceof model.Customer && /* check business logic */ false) { 
                // Ελέγξτε αν έχετε διαχωρίσει το Business ως class ή ως AccountType. 
                // Αν ο χρήστης έχει Business account:
                JButton billBtn = StyleHelpers.createRoundedButton("Create Bill");
                navPanel.add(billBtn);
                mainContentPanel.add(new CreateBillPanel(user), "CREATE_BILL");
                billBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "CREATE_BILL"));
            }

            // Panels
            mainContentPanel.add(new HomePanel(user, navigation), "HOME");
            mainContentPanel.add(new TransferPanel(user, "TRANSFER"), "TRANSFER");
            mainContentPanel.add(new TransferPanel(user, "PAYMENT"), "PAYMENT");
            mainContentPanel.add(new StandingOrderPanel(user), "SO");
            mainContentPanel.add(new CreateAccountPanel(user), "NEW_ACC"); // Χρησιμοποιούμε Panel αντί για Frame

            homeBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));
            transferBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "TRANSFER"));
            payBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "PAYMENT"));
            soBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "SO"));
            myAccBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "NEW_ACC"));
            
            homeBtn.doClick();
        }

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
    }
}