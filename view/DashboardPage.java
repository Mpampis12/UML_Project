package view;

import model.*;
import services.BankSystem;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import control.BankController;
import java.awt.*;

public class DashboardPage extends JPanel {

    private BankBridge navigation;
    private User user;
    private JPanel mainContentPanel; 
    private Runnable onTransactionSuccess;
    private CardLayout cardLayout;
    private BankController controller; 
    
    // Εδώ αποθηκεύουμε τα Panels για να τα βρίσκουμε και να τα κάνουμε refresh
    private Map<String, JPanel> panelsMap = new HashMap<>();

    public DashboardPage(BankBridge navigation, User user) {
        this.navigation = navigation;
        this.user = user;
        this.controller = BankController.getInstance();
        setLayout(new BorderLayout());

        // --- HEADER ---
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

        // --- NAVIGATION BUTTONS ---
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
            
            // Χρήση της νέας μεθόδου addPage
            List<User> admins = controller.getAdmins();
            addPage("MANAGE_ADMINS", new UserManagementPanel(admins, "ADMIN")); 
            addPage("CREATE_ADMIN", new EmbeddedRegisterPanel("ADMIN"));

            manageAdminsBtn.addActionListener(e -> switchPage("MANAGE_ADMINS"));
            createAdminBtn.addActionListener(e -> switchPage("CREATE_ADMIN"));
            
            switchPage("MANAGE_ADMINS");

        } else if (user instanceof Admin) {
            // === ADMIN ===
            JButton manageCustBtn = StyleHelpers.createRoundedButton("Manage Customers");
            JButton newIndivBtn = StyleHelpers.createRoundedButton("New Individual");
            JButton newBizBtn = StyleHelpers.createRoundedButton("New Business");
            JButton depositBtn = StyleHelpers.createRoundedButton("Deposit Cash");
            JButton withdrawBtn = StyleHelpers.createRoundedButton("Withdraw Cash");
            JButton accountsBtn = StyleHelpers.createRoundedButton("Manage Accounts");

            navPanel.add(manageCustBtn);
            navPanel.add(accountsBtn);
            navPanel.add(newIndivBtn);
            navPanel.add(newBizBtn);
            navPanel.add(depositBtn);
            navPanel.add(withdrawBtn);
            
            List<User> customers = controller.getCustomers();
            addPage("MANAGE_CUST", new UserManagementPanel(customers, "CUSTOMER"));
            addPage("NEW_INDIV", new EmbeddedRegisterPanel("PERSONAL"));
            addPage("NEW_BIZ", new EmbeddedRegisterPanel("BUSINESS"));
            addPage("DEPOSIT", new DepositPanel());
            addPage("WITHDRAW", new WithdrawPanel());

            AdminAccountsPanel searchPanel = new AdminAccountsPanel(user, selectedAccount -> {
                 AccountDetailsPage detailsPage = new AccountDetailsPage(navigation, user, selectedAccount);
                 // Δυναμική προσθήκη (δεν την βάζουμε στο map μόνιμα γιατί αλλάζει το account)
                 mainContentPanel.add(detailsPage, "ACC_DETAILS_DYN"); 
                 cardLayout.show(mainContentPanel, "ACC_DETAILS_DYN");
            });
            addPage("MANAGE_ACCOUNTS", searchPanel);

            manageCustBtn.addActionListener(e -> switchPage("MANAGE_CUST"));
            accountsBtn.addActionListener(e -> switchPage("MANAGE_ACCOUNTS"));
            newIndivBtn.addActionListener(e -> switchPage("NEW_INDIV"));
            newBizBtn.addActionListener(e -> switchPage("NEW_BIZ"));
            depositBtn.addActionListener(e -> switchPage("DEPOSIT"));
            withdrawBtn.addActionListener(e -> switchPage("WITHDRAW"));
            
            switchPage("MANAGE_CUST");

        } else if (user instanceof Customer) {
            // === CUSTOMER ===
            JButton homeBtn = StyleHelpers.createRoundedButton("Home");
            JButton transferBtn = StyleHelpers.createRoundedButton("Transfer");
            JButton payBtn = StyleHelpers.createRoundedButton("Payment");
            JButton soBtn = StyleHelpers.createRoundedButton("Standing Orders");
            JButton myAccBtn = StyleHelpers.createRoundedButton("New Account");
            JButton withdrawBtn = StyleHelpers.createRoundedButton("Withdraw Cash");
            JButton historyBtn = StyleHelpers.createRoundedButton("Statements"); // Όνομα κουμπιού όπως το θέλατε

            navPanel.add(homeBtn);
            navPanel.add(transferBtn);
            navPanel.add(payBtn);
            navPanel.add(soBtn);
            navPanel.add(historyBtn);
            navPanel.add(myAccBtn);

            // Business Check
            if (user.getRole().contains("BUSINESS")) { 
                JButton billBtn = StyleHelpers.createRoundedButton("Create Bill");
                navPanel.add(billBtn);
                addPage("CREATE_BILL", new CreateBillPanel(user));
                billBtn.addActionListener(e -> switchPage("CREATE_BILL"));
            }
            
            onTransactionSuccess = () -> { switchPage("HOME"); };

            addPage("HOME", new HomePanel(user, navigation));
            addPage("TRANSFER", new TransferPanel(user, "TRANSFER", onTransactionSuccess));
            addPage("PAYMENT", new TransferPanel(user, "PAYMENT", onTransactionSuccess));
            addPage("SO", new StandingOrderPanel(user));
            addPage("NEW_ACC", new CreateAccountPanel(user));  
            addPage("HISS", new HistoryPanel(user)); 
            
            homeBtn.addActionListener(e -> switchPage("HOME"));
            transferBtn.addActionListener(e -> switchPage("TRANSFER"));
            payBtn.addActionListener(e -> switchPage("PAYMENT"));
            soBtn.addActionListener(e -> switchPage("SO"));
            myAccBtn.addActionListener(e -> switchPage("NEW_ACC"));
            historyBtn.addActionListener(e -> switchPage("HISS"));
            
            switchPage("HOME");
        }

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    // --- Helper Methods ---

    // 1. Προσθέτει το Panel στο Layout ΚΑΙ στο Map
    private void addPage(String name, JPanel panel) {
        panelsMap.put(name, panel);
        mainContentPanel.add(panel, name);
    }

    // 2. Αλλάζει σελίδα και καλεί Refresh αν το panel είναι "Refreshable"
    private void switchPage(String name) {
        JPanel panel = panelsMap.get(name);
        if (panel != null) {
            // ΕΛΕΓΧΟΣ: Είναι το panel "Refreshable";
            if (panel instanceof Refreshable) {
                ((Refreshable) panel).refresh();
            }
            cardLayout.show(mainContentPanel, name);
        }
    }
}