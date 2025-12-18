package view;

import model.User;
import model.Account;
import model.Transaction;
import model.StandingOrder;
import model.Iban;
import model.StandingOrder.StandingOrderPurpose;
import services.BankSystem;
import control.BankController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardPage extends JPanel {

    private BankBridge navigation;
    private User user;
    private BankController controller;
    
    private JPanel mainContentPanel; 
    private CardLayout cardLayout;   
    private JLabel totalBalanceLabel;
    
    private JPanel accountsContainer;
    private JPanel transactionsContainer;
    
    private static final Color MUSTARD_BG = new Color(228, 196, 101);  
    private static final Color BUTTON_YELLOW = new Color(255, 180, 0);  
    private static final Color CARD_COLOR = new Color(160, 140, 90, 200);  

    public DashboardPage(BankBridge navigation, User user) {
        this.navigation = navigation;
        this.user = user;
        this.controller = new BankController();

        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            Image bg = new ImageIcon("services/background.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 250));
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel topLeftPanel = new JPanel();
        topLeftPanel.setLayout(new BoxLayout(topLeftPanel, BoxLayout.Y_AXIS));
        topLeftPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome " + user.getFirstName(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BUTTON_YELLOW);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        welcomeLabel.setOpaque(false);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setBorder(new EmptyBorder(5, 20, 5, 20));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topLeftPanel.add(welcomeLabel);
        
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        navPanel.setOpaque(false);
        
        JButton homeBtn = createRoundedButton("Home");
        JButton historyBtn = createRoundedButton("History");
        JButton transferBtn = createRoundedButton("Transfer");
        JButton paymentBtn = createRoundedButton("Payment");
        JButton standingBtn = createRoundedButton("Standing Order");
        JButton withdrawBtn = createRoundedButton("Withdraw");
        JButton depositBtn = createRoundedButton("Deposit");
        JButton createAccButton = createRoundedButton("Create Account");

        homeBtn.addActionListener(e -> { refreshData(); cardLayout.show(mainContentPanel, "HOME"); });
        historyBtn.addActionListener(e -> { refreshHistory(); cardLayout.show(mainContentPanel, "HISTORY"); });
        
        // Νέα λογική: Κάθε φορά που πατάς το κουμπί, ξαναδημιουργούμε το panel για να είναι καθαρό
        transferBtn.addActionListener(e -> {
            mainContentPanel.add(createTransferPaymentPanel("TRANSFER"), "TRANSFER");
            cardLayout.show(mainContentPanel, "TRANSFER");
        });
        paymentBtn.addActionListener(e -> {
            mainContentPanel.add(createTransferPaymentPanel("PAYMENT"), "PAYMENT");
            cardLayout.show(mainContentPanel, "PAYMENT");
        });
        standingBtn.addActionListener(e -> {
            mainContentPanel.add(createStandingOrderPanel(), "STANDING_ORDER");
            cardLayout.show(mainContentPanel, "STANDING_ORDER");
        });
        
        withdrawBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "WITHDRAW"));
        depositBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "DEPOSIT"));
        
        createAccButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Admin Functionality"));

        navPanel.add(homeBtn);
        navPanel.add(paymentBtn);
        navPanel.add(standingBtn);     
        navPanel.add(historyBtn);
        navPanel.add(transferBtn);
        navPanel.add(withdrawBtn);
        navPanel.add(depositBtn);
        
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("logo.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            navPanel.add(new JLabel(icon));
        } catch (Exception e) { }

        headerPanel.add(topLeftPanel, BorderLayout.WEST); 
        headerPanel.add(navPanel, BorderLayout.EAST); 
        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(MUSTARD_BG);

        mainContentPanel.add(createHomePanel(createAccButton), "HOME");
        mainContentPanel.add(createHistoryPanel(), "HISTORY");
        // Τα υπόλοιπα Panels προστίθενται δυναμικά στα ActionListeners
        mainContentPanel.add(createTransferPaymentPanel("WITHDRAW"), "WITHDRAW"); // Απλή φόρμα
        mainContentPanel.add(createTransferPaymentPanel("DEPOSIT"), "DEPOSIT");   // Απλή φόρμα

        add(mainContentPanel, BorderLayout.CENTER);
        
        refreshData();
        cardLayout.show(mainContentPanel, "HOME");
    }

    // --- 1. HOME PANEL ---
    private JPanel createHomePanel(JButton createAccBtn) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(MUSTARD_BG);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // LEFT
        JPanel leftCol = new JPanel(new BorderLayout());
        leftCol.setOpaque(false);
        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setOpaque(false);
        totalBalanceLabel = new JLabel("Total Balance: 0.00€");
        totalBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        totalBalanceLabel.setForeground(new Color(40, 40, 40));
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        btnWrap.setOpaque(false);
        btnWrap.add(createAccBtn);
        leftHeader.add(totalBalanceLabel);
        leftHeader.add(btnWrap);
        
        accountsContainer = new JPanel();
        accountsContainer.setLayout(new BoxLayout(accountsContainer, BoxLayout.Y_AXIS));
        accountsContainer.setOpaque(false);
        JScrollPane scrollAccounts = new JScrollPane(accountsContainer);
        scrollAccounts.getViewport().setOpaque(false);
        scrollAccounts.setOpaque(false);
        scrollAccounts.setBorder(null);
        scrollAccounts.getVerticalScrollBar().setUI(new MyScrollBarUI());
        leftCol.add(leftHeader, BorderLayout.NORTH);
        leftCol.add(scrollAccounts, BorderLayout.CENTER);

        // RIGHT
        JPanel rightCol = new JPanel(new BorderLayout());
        rightCol.setOpaque(false);
        JLabel transTitle = new JLabel("Last Transactions");
        transTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        transTitle.setForeground(new Color(40, 40, 40));
        transTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        rightCol.add(transTitle, BorderLayout.NORTH);
        transactionsContainer = new JPanel();
        transactionsContainer.setLayout(new BoxLayout(transactionsContainer, BoxLayout.Y_AXIS));
        transactionsContainer.setOpaque(false);
        JScrollPane scrollTrans = new JScrollPane(transactionsContainer);
        scrollTrans.getViewport().setOpaque(false);
        scrollTrans.setOpaque(false);
        scrollTrans.setBorder(null);
        scrollTrans.getVerticalScrollBar().setUI(new MyScrollBarUI());
        rightCol.add(scrollTrans, BorderLayout.CENTER);

        panel.add(leftCol);
        panel.add(rightCol);
        return panel;
    }

    // --- 2. HISTORY PANEL ---
    private JTable historyTable;
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MUSTARD_BG);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(40, 40, 40));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        String[] columns = {"Date", "Type", "Description", "Amount", "Source", "Target"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        historyTable = new JTable(model) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        historyTable.setOpaque(false);
        historyTable.setBackground(new Color(0,0,0,0));
        ((DefaultTableCellRenderer)historyTable.getDefaultRenderer(Object.class)).setOpaque(false);
        historyTable.setRowHeight(35);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        historyTable.setShowGrid(false);
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(BUTTON_YELLOW);
        header.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // --- 3. TRANSFER / PAYMENT (STEP-BY-STEP CONFIRMATION) ---
    private JPanel createTransferPaymentPanel(String type) {
        // Χρησιμοποιούμε εσωτερικό CardLayout για να αλλάζουμε από Input -> Confirm
        CardLayout innerLayout = new CardLayout();
        JPanel container = new JPanel(innerLayout);
        container.setOpaque(false);

        // --- STEP 1: INPUT FORM ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(MUSTARD_BG);
        inputPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel(type.equals("TRANSFER") ? "Transfer Money" : (type.equals("PAYMENT") ? "Pay Bill" : type));
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        inputPanel.add(title, gbc);

        // Fields
        JComboBox<String> accountBox = new JComboBox<>();
        accountBox.setPreferredSize(new Dimension(200, 30));
        List<Account> accounts = controller.getAccountsForUser(user);
        for(Account acc : accounts) accountBox.addItem(acc.getIban());

        JComboBox<String> transferTypeBox = new JComboBox<>(new String[]{"INTERNAL", "SEPA", "SWIFT"});
        
        JTextField fTarget = new JTextField(15);
        JTextField fAmount = new JTextField(15);
        JTextField fName = new JTextField(15);
        JTextField fBic = new JTextField(15);
        JTextField fBankName = new JTextField(15);
        JTextField fAddress = new JTextField(15);
        JTextField fCountry = new JTextField(15);
        
        // Add Fields to Layout
        gbc.gridwidth = 1; gbc.gridy++;
        inputPanel.add(createLabel(type.equals("DEPOSIT") ? "Target Account:" : "Source Account:"), gbc);
        gbc.gridx = 1; inputPanel.add(accountBox, gbc);

        // Dynamic Fields Panel
        JPanel dynPanel = new JPanel(new GridBagLayout());
        dynPanel.setOpaque(false);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        inputPanel.add(dynPanel, gbc);

        GridBagConstraints subGbc = new GridBagConstraints();
        subGbc.insets = new Insets(5, 0, 5, 0);
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.gridx = 0; subGbc.gridy = 0;

        if(type.equals("TRANSFER")) {
            addDynamicRow(dynPanel, subGbc, "Type:", transferTypeBox);
            addDynamicRow(dynPanel, subGbc, "Target IBAN:", fTarget);
            addDynamicRow(dynPanel, subGbc, "Amount (€):", fAmount);
            // Listener για εμφάνιση extra πεδίων SEPA/SWIFT
            transferTypeBox.addActionListener(e -> {
                String mode = (String) transferTypeBox.getSelectedItem();
                // Καθαρισμός και επανατοποθέτηση βασικών
                dynPanel.removeAll();
                subGbc.gridy = 0;
                addDynamicRow(dynPanel, subGbc, "Type:", transferTypeBox);
                addDynamicRow(dynPanel, subGbc, "Target IBAN:", fTarget);
                addDynamicRow(dynPanel, subGbc, "Amount (€):", fAmount);
                
                if (!mode.equals("INTERNAL")) {
                    addDynamicRow(dynPanel, subGbc, "Beneficiary Name:", fName);
                    addDynamicRow(dynPanel, subGbc, "Bank Name:", fBankName);
                    addDynamicRow(dynPanel, subGbc, "BIC:", fBic);
                }
                if (mode.equals("SWIFT")) {
                    addDynamicRow(dynPanel, subGbc, "Address:", fAddress);
                    addDynamicRow(dynPanel, subGbc, "Country:", fCountry);
                }
                dynPanel.revalidate(); dynPanel.repaint();
            });
        } else if (type.equals("PAYMENT")) {
             addDynamicRow(dynPanel, subGbc, "RF Code:", fTarget);
             // Για τα payments συνήθως το ποσό έρχεται από τον οργανισμό, εδώ το ζητάμε ή το παίρνουμε από το Bill
        } else {
             // WITHDRAW / DEPOSIT
             addDynamicRow(dynPanel, subGbc, "Amount (€):", fAmount);
        }

        JButton continueBtn = createRoundedButton("Continue");
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        inputPanel.add(continueBtn, gbc);

        // --- STEP 2: CONFIRMATION PANEL (Οθόνη Επιβεβαίωσης) ---
        JPanel confirmPanel = new JPanel(new GridBagLayout());
        confirmPanel.setBackground(MUSTARD_BG);
        confirmPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        JLabel confirmTitle = new JLabel("Confirmation");
        confirmTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        confirmPanel.add(confirmTitle, gbc);
        
        JTextArea detailsArea = new JTextArea(8, 30);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailsArea.setEditable(false);
        detailsArea.setOpaque(false);
        gbc.gridy++;
        confirmPanel.add(detailsArea, gbc);

        JPanel actionBtns = new JPanel(new FlowLayout());
        actionBtns.setOpaque(false);
        JButton confirmBtn = createRoundedButton("Confirm & Pay");
        JButton backBtn = createRoundedButton("Edit");
        backBtn.setBackground(Color.GRAY);
        actionBtns.add(backBtn);
        actionBtns.add(confirmBtn);
        gbc.gridy++;
        confirmPanel.add(actionBtns, gbc);

        // --- LOGIC ---
        continueBtn.addActionListener(e -> {
            // Αν είναι απλό withdraw/deposit, κάντο απευθείας
            if(type.equals("WITHDRAW") || type.equals("DEPOSIT")) {
                try {
                    double am = Double.parseDouble(fAmount.getText());
                    if(type.equals("WITHDRAW")) controller.handleWithdraw((String)accountBox.getSelectedItem(), am);
                    else controller.handleDeposit((String)accountBox.getSelectedItem(), am);
                    JOptionPane.showMessageDialog(this, "Success!");
                    refreshData(); cardLayout.show(mainContentPanel, "HOME");
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
                return;
            }

            // Για Transfer/Payment -> Πήγαινε στο Confirm Screen
            StringBuilder sb = new StringBuilder();
            String src = (String) accountBox.getSelectedItem();
            sb.append("Source Account: ").append(src).append("\n");
            
            double amount = 0;
            double fee = 0;
            
            try {
                if(type.equals("TRANSFER")) {
                    amount = Double.parseDouble(fAmount.getText());
                    String mode = (String) transferTypeBox.getSelectedItem();
                    String name = fName.getText();
                    
                    sb.append("Beneficiary: ").append(maskString(name.isEmpty() ? "Unknown" : name)).append("\n");
                    sb.append("Target IBAN: ").append(fTarget.getText()).append("\n");
                    
                    // Mock Fee Calculation
                    fee = mode.equals("INTERNAL") ? 0.0 : (mode.equals("SEPA") ? 1.0 : 5.0);
                } else if (type.equals("PAYMENT")) {
                    String rf = fTarget.getText();
                    sb.append("Organization: ").append("DEI / Utility (Mock)").append("\n"); // Θα μπορούσες να το βρεις από το RF
                    sb.append("RF Code: ").append(rf).append("\n");
                    // Payment Amount συνήθως 150.0 πχ
                    amount = 150.0; // Mock amount αν δεν το ζητάμε
                    fee = 0.50;
                }
                
                sb.append("Amount: ").append(amount).append("€\n");
                sb.append("Commission: ").append(fee).append("€\n");
                sb.append("--------------------------\n");
                sb.append("TOTAL: ").append(amount + fee).append("€\n");
                
                detailsArea.setText(sb.toString());
                innerLayout.next(container); // Switch to Confirm
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Data: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> innerLayout.previous(container));
        
        confirmBtn.addActionListener(e -> {
            try {
                String src = (String) accountBox.getSelectedItem();
                if(type.equals("TRANSFER")) {
                    String mode = (String) transferTypeBox.getSelectedItem();
                    double am = Double.parseDouble(fAmount.getText());
                    if(mode.equals("INTERNAL")) controller.handleTransfer(src, fTarget.getText(), am);
                    else controller.handleExternalTransfer(mode, src, am, fName.getText(), fTarget.getText(), fBic.getText(), fBankName.getText(), fAddress.getText(), fCountry.getText());
                } else if (type.equals("PAYMENT")) {
                    controller.payBill(fTarget.getText(), src, user.getAfm());
                }
                JOptionPane.showMessageDialog(this, "Transaction Executed Successfully!");
                refreshData(); 
                cardLayout.show(mainContentPanel, "HOME");
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Execution Error: " + ex.getMessage());
            }
        });

        container.add(inputPanel, "INPUT");
        container.add(confirmPanel, "CONFIRM");
        return container;
    }

    // --- 4. STANDING ORDERS (MASTER-DETAIL VIEW) ---
    private JPanel createStandingOrderPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(MUSTARD_BG);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // LEFT: LIST OF ORDERS
        JPanel leftCol = new JPanel(new BorderLayout());
        leftCol.setOpaque(false);
        JLabel title = new JLabel("Your Orders");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        leftCol.add(title, BorderLayout.NORTH);
        
        JPanel ordersListPanel = new JPanel();
        ordersListPanel.setLayout(new BoxLayout(ordersListPanel, BoxLayout.Y_AXIS));
        ordersListPanel.setOpaque(false);
        
        List<StandingOrder> orders = controller.getStandingOrdersForUser(user);
        
        // RIGHT: DETAILS FORM
        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setOpaque(false);
        
        // Φόρμα δεξιά (αρχικά κενή ή για Create New)
        JTextField fTarget = new JTextField(15);
        JTextField fAmount = new JTextField(15);
        JComboBox<String> accBox = new JComboBox<>();
        List<Account> accounts = controller.getAccountsForUser(user);
        for(Account acc : accounts) accBox.addItem(acc.getIban());

        JButton saveBtn = createRoundedButton("Create New");
        
        // Helper για να γεμίζουμε τη δεξιά φόρμα
        Runnable clearForm = () -> {
             fTarget.setText(""); fAmount.setText(""); 
             saveBtn.setText("Create New");
        };

        // Γέμισμα Λίστας Αριστερά (Cards)
        for(StandingOrder so : orders) {
            JPanel card = new JPanel(new BorderLayout());
            card.setOpaque(false);
            card.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(5,0,5,0), BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true)
            ));
            card.setMaximumSize(new Dimension(300, 60));
            
            JLabel lbl = new JLabel("<html><b>" + so.getDescription() + "</b><br>" + so.getAmount() + "€ -> " + so.getTarget().toString() + "</html>");
            lbl.setBorder(new EmptyBorder(5, 10, 5, 10));
            
            card.add(lbl, BorderLayout.CENTER);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // On Click -> Fill Right Form
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    fTarget.setText(so.getTarget().toString());
                    fAmount.setText(String.valueOf(so.getAmount()));
                    saveBtn.setText("Update Order"); // Αλλάζει η λειτουργία
                    // Εδώ θα χρειαζόταν να κρατήσουμε το ID για update
                }
            });
            ordersListPanel.add(card);
        }
        
        // Κουμπί "New Order" πάνω στη λίστα
        JButton newOrderBtn = createRoundedButton("+ New Order");
        newOrderBtn.addActionListener(e -> clearForm.run());
        leftCol.add(newOrderBtn, BorderLayout.SOUTH);
        leftCol.add(new JScrollPane(ordersListPanel), BorderLayout.CENTER);

        // Στήσιμο Δεξιάς Φόρμας
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.gridx=0; gbc.gridy=0;
        rightCol.add(createLabel("Source Account:"), gbc);
        gbc.gridx=1; rightCol.add(accBox, gbc);
        
        gbc.gridx=0; gbc.gridy++; rightCol.add(createLabel("Target IBAN:"), gbc);
        gbc.gridx=1; rightCol.add(fTarget, gbc);
        
        gbc.gridx=0; gbc.gridy++; rightCol.add(createLabel("Amount (€):"), gbc);
        gbc.gridx=1; rightCol.add(fAmount, gbc);
        
        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2; 
        rightCol.add(saveBtn, gbc);
        
        saveBtn.addActionListener(e -> {
            try {
                StandingOrder so = new StandingOrder(new Iban((String)accBox.getSelectedItem()), new Iban(fTarget.getText()), 
                        Double.parseDouble(fAmount.getText()), "Monthly Order", StandingOrderPurpose.TRANSFER);
                controller.createStandingOrder(so);
                JOptionPane.showMessageDialog(this, "Order Saved!");
                cardLayout.show(mainContentPanel, "HOME"); // Επιστροφή
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
        });

        panel.add(leftCol);
        panel.add(rightCol);
        return panel;
    }

    // --- UTILS ---
    private String maskString(String input) {
        if (input == null || input.length() < 4) return "****";
        return input.substring(0, 2) + "****" + input.substring(input.length() - 2);
    }
    
    private void addDynamicRow(JPanel p, GridBagConstraints gbc, String label, Component field) {
        gbc.gridx = 0; p.add(createLabel(label), gbc);
        gbc.gridx = 1; p.add(field, gbc);
        gbc.gridy++;
    }
    
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(40, 40, 40));
        return l;
    }
    
    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(BUTTON_YELLOW);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false); 
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void refreshData() {
        accountsContainer.removeAll();
        transactionsContainer.removeAll();
        List<Account> accounts = controller.getAccountsForUser(user);
        double total = accounts.stream().mapToDouble(Account::getBalance).sum();
        totalBalanceLabel.setText(String.format("Total Balance: %.2f€", total));
        
        List<Transaction> allTransactions = new ArrayList<>();
        for (Account acc : accounts) {
            accountsContainer.add(new AccountCard(acc));
            accountsContainer.add(Box.createRigidArea(new Dimension(0, 15))); 
            allTransactions.addAll(acc.getTransaction());
        }
        Collections.reverse(allTransactions); 
        int counter = 6;
        for (Transaction t : allTransactions) {
            if(counter-- > 0) {
                transactionsContainer.add(new TransactionRow(t));
                transactionsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            } else break;
        }
        accountsContainer.revalidate(); accountsContainer.repaint();
        transactionsContainer.revalidate(); transactionsContainer.repaint();
    }
    
    private void refreshHistory() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0); 
        List<Account> accounts = controller.getAccountsForUser(user);
        List<Transaction> allTrans = new ArrayList<>();
        for (Account acc : accounts) allTrans.addAll(acc.getTransaction());
        Collections.reverse(allTrans);
        for (Transaction t : allTrans) {
            String date = (t.getTransactionID() != null) ? "Date info" : "-"; 
            String src = (t.getSource() != null) ? t.getSource().toString() : "-";
            String trg = (t.getTarget() != null) ? t.getTarget().toString() : "-";
            model.addRow(new Object[]{date, t.getType(), t.getDescription(), String.format("%.2f€", t.getAmount()), src, trg});
        }
    }

    class AccountCard extends JPanel {
        public AccountCard(Account acc) {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(400, 120));
            setMaximumSize(new Dimension(500, 120));
            setOpaque(false);
            setBorder(new EmptyBorder(15, 20, 15, 20));
            JLabel typeLbl = new JLabel(acc.getAccountType().toString());
            typeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            JLabel balLbl = new JLabel(String.format("%.2f€", acc.getBalance()));
            balLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
            balLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            JLabel ibanLbl = new JLabel(acc.getIban());
            ibanLbl.setFont(new Font("Monospaced", Font.BOLD, 18));
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false); top.add(typeLbl, BorderLayout.WEST); top.add(balLbl, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);
            JButton copyBtn = createRoundedButton("Copy");
            copyBtn.setPreferredSize(new Dimension(60, 25));
            copyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            copyBtn.addActionListener(e -> {
                 Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(acc.getIban()), null);
                 JOptionPane.showMessageDialog(this, "IBAN copied!");
            });
            JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            bot.setOpaque(false); bot.add(ibanLbl); bot.add(Box.createHorizontalStrut(10)); bot.add(copyBtn);
            add(bot, BorderLayout.SOUTH);
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent evt) { navigation.showAccountDetails(user, acc); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_COLOR);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            super.paintComponent(g);
        }
    }

    class TransactionRow extends JLabel {
        public TransactionRow(Transaction t) {
            String dateStr = (t.getTransactionID() != null) ? "Date info" : "Just now"; 
            String text = "<html><font size='5' color='#003366'>• " + t.getDescription() + "</font> " 
                        + "<font size='5' color='black'><b>" + t.getAmount() + "€</b></font><br>" 
                        + "<font size='3' color='#555555'>&nbsp;&nbsp;" + dateStr + "</font></html>";
            setText(text);
        }
    }

    class MyScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() { this.thumbColor = BUTTON_YELLOW; }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() { JButton j = new JButton(); j.setPreferredSize(new Dimension(0, 0)); return j; }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 4, thumbBounds.y + 2, thumbBounds.width - 8, thumbBounds.height - 4, 10, 10);
            g2.dispose();
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) { }
    }
}