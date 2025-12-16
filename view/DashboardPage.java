package view;

import model.User;
import model.Account;
import model.Transaction;
import services.BankSystem;
import control.BankController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardPage extends JPanel {

    private BankBridge navigation;
    private User user;
    private BankController controller;
    
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

        //  heaDER
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            Image bg = new ImageIcon("services/background.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 250));
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        //  Welcome  
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

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
        welcomeLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        topRow.add(welcomeLabel, BorderLayout.WEST);
        
        // -- Navigation Buttons --
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        navPanel.setOpaque(false);
        
        JButton accBtn = createHeaderButton("Accounts");
        JButton historyBtn = createHeaderButton("History");
        JButton transferBtn = createHeaderButton("Transfer");
        JButton withdrawBtn = createHeaderButton("Withdraw");
        JButton depositBtn = createHeaderButton("Deposit");
        
        // Actions


        accBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Account Managment coming Soon"));
        historyBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "History Coming Soon"));
        
        transferBtn.addActionListener(e -> {
             String source = selectAccountIban("Select Source Account");
             if(source!=null) {
                 String target = JOptionPane.showInputDialog("Target IBAN:");
                 String amount = JOptionPane.showInputDialog("Amount:");
                 if(target != null && amount != null) {
                    try { controller.handleTransfer(source, target, Double.parseDouble(amount)); refreshData(); } 
                    catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
                 }
             }
        });
        
        withdrawBtn.addActionListener(e -> {
             String source = selectAccountIban("Withdraw from:");
             if(source!=null) {
                 String amount = JOptionPane.showInputDialog("Amount:");
                 if(amount != null) {
                    try { controller.handleWithdraw(source, Double.parseDouble(amount)); refreshData(); }
                    catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
                 }
             }
        });
        
        depositBtn.addActionListener(e -> {
             String source = selectAccountIban("Deposit to:");
             if(source!=null) {
                 String amount = JOptionPane.showInputDialog("Amount:");
                 if(amount != null) {
                    try { controller.handleDeposit(source, Double.parseDouble(amount)); refreshData(); }
                    catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
                 }
             }
        });

        navPanel.add(accBtn);
        navPanel.add(historyBtn);
        navPanel.add(transferBtn);
        navPanel.add(withdrawBtn);
        navPanel.add(depositBtn);
        
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("logo.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            JLabel avatar = new JLabel(icon);
            navPanel.add(avatar);
        } catch (Exception e) { }

        topRow.add(navPanel, BorderLayout.EAST);
        headerPanel.add(topRow, BorderLayout.NORTH);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. MAIN BODY (YELLOW) ---
        JPanel mainBody = new JPanel();
        mainBody.setBackground(MUSTARD_BG);
        mainBody.setLayout(new GridLayout(1, 2, 20, 0));
        mainBody.setBorder(new EmptyBorder(20, 40, 20, 40));

        //  FTIAXNW DYO CONTAINERS ENA DEJIA GIA NA B;ALW ta teleytaia 10 TRANSACTIONS  kai ena aristera gia na balo tous logariasmous
        accountsContainer = new JPanel();
        accountsContainer.setLayout(new BoxLayout(accountsContainer, BoxLayout.Y_AXIS));
        accountsContainer.setOpaque(false);
        
        JScrollPane scrollAccounts = new JScrollPane(accountsContainer);
        scrollAccounts.getViewport().setOpaque(false);
        scrollAccounts.setOpaque(false);
        scrollAccounts.setBorder(null);

        scrollAccounts.getVerticalScrollBar().setUI(new MyScrollBarUI());
 
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

        mainBody.add(scrollAccounts);
        mainBody.add(rightCol);

        add(mainBody, BorderLayout.CENTER);

        refreshData();
    }

    private void refreshData() {
        accountsContainer.removeAll();
        transactionsContainer.removeAll();

        List<Account> accounts = BankSystem.getInstance().getAccountManager().getAccountsByOwner(user.getAfm());
        List<Transaction> allTransactions = new ArrayList<>();

        for (Account acc : accounts) {
            accountsContainer.add(new AccountCard(acc));
            accountsContainer.add(Box.createRigidArea(new Dimension(0, 15))); 
            allTransactions.addAll(acc.getTransaction());
        }

        Collections.reverse(allTransactions); 
        
        for (Transaction t : allTransactions) {
            transactionsContainer.add(new TransactionRow(t));
            transactionsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        accountsContainer.revalidate();
        accountsContainer.repaint();
        transactionsContainer.revalidate();
        transactionsContainer.repaint();
    }

    // --- Helper Classes ---

    class AccountCard extends JPanel {
        public AccountCard(Account acc) {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(400, 120));
            setMaximumSize(new Dimension(500, 120));
            setOpaque(false);
            setBorder(new EmptyBorder(15, 20, 15, 20));

            JLabel typeLbl = new JLabel(acc.getAccountType().toString());
            typeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            typeLbl.setForeground(new Color(30, 30, 30));
            
            JLabel balLbl = new JLabel(String.format("%.2f€", acc.getBalance()));
            balLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
            balLbl.setHorizontalAlignment(SwingConstants.RIGHT);

            JLabel ibanLbl = new JLabel(acc.getIban());
            ibanLbl.setFont(new Font("Monospaced", Font.BOLD, 18));
            ibanLbl.setForeground(new Color(50, 50, 50));

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(typeLbl, BorderLayout.WEST);
            top.add(balLbl, BorderLayout.EAST);

            add(top, BorderLayout.NORTH);
            add(ibanLbl, BorderLayout.SOUTH);this.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Αλλάζει ο κέρσορας σε χεράκι
            
            this.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    // Όταν γίνει κλικ, καλούμε τη μέθοδο του Bridge
                    navigation.showAccountDetails(user, acc);
                }
            });
            
        }

        @Override
        protected void paintComponent(Graphics g) {
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

    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
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
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false); 
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String selectAccountIban(String message) {
        List<Account> myAccounts = BankSystem.getInstance().getAccountManager().getAccountsByOwner(user.getAfm());
        if (myAccounts.isEmpty()) return null;
        String[] ibans = myAccounts.stream().map(Account::getIban).toArray(String[]::new);
        return (String) JOptionPane.showInputDialog(this, message, "Select Account", JOptionPane.QUESTION_MESSAGE, null, ibans, ibans[0]);
    }

    // ====================================================================
    //  CUSTOM SCROLLBAR UI (Η κλάση που φτιάχνει την κίτρινη μπάρα)
    // ====================================================================
    class MyScrollBarUI extends BasicScrollBarUI {
        
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = BUTTON_YELLOW; // Το κίτρινο των κουμπιών
        }

        // Εξαφανίζουμε τα βελάκια πάνω-κάτω για πιο μοντέρνο στυλ
        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }

        // Ζωγραφίζουμε το "κουμπί" κύλισης (thumb)
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(thumbColor);
            // Το κάνουμε λίγο πιο στενό από το κανάλι και στρογγυλεμένο
            g2.fillRoundRect(thumbBounds.x + 4, thumbBounds.y + 2, thumbBounds.width - 8, thumbBounds.height - 4, 10, 10);
            g2.dispose();
        }

        // Ζωγραφίζουμε το φόντο της μπάρας (track)
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Μπορούμε να το αφήσουμε κενό για να είναι διάφανο (θα φαίνεται το mustard από πίσω)
            // Ή να βάλουμε ένα ελαφρώς πιο σκούρο κίτρινο για να ξεχωρίζει
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(200, 170, 90)); // Λίγο πιο σκούρο από το MUSTARD_BG
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
}