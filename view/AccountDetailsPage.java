package view;

import model.User;
import model.Account;
import model.Admin;       // Import για έλεγχο ρόλου
import model.SuperAdmin;  // Import για έλεγχο ρόλου
import model.Transaction;
import control.BankController; // Import για το addOwner logic
import services.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class AccountDetailsPage extends JPanel {

    private BankBridge navigation;
    private User user;
    private Account account;

    // Χρώματα
    private static final Color MUSTARD_BG = new Color(228, 196, 101);
    private static final Color BUTTON_YELLOW = new Color(255, 180, 0);
    private static final Color CARD_COLOR = new Color(160, 140, 90, 200);

    public AccountDetailsPage(BankBridge navigation, User user, Account account) {
        this.navigation = navigation;
        this.user = user;
        this.account = account;

        setLayout(new BorderLayout());

        // Ελέγχουμε αν ο χρήστης είναι Admin
        boolean isAdmin = (user instanceof Admin || user instanceof SuperAdmin);

        // --- 1. HEADER LOGIC ---
        if (isAdmin) {
            // === ADMIN HEADER (Compact) ===
            // Αν είναι Admin, βάζουμε μόνο ένα μικρό toolbar για να μην πέφτει πάνω στο Dashboard header
            JPanel adminHeader = new JPanel(new BorderLayout());
            adminHeader.setBackground(MUSTARD_BG); // Ίδιο χρώμα με το background
            adminHeader.setBorder(new EmptyBorder(10, 30, 0, 30));

            JButton backBtn = createHeaderButton("← Back to List");
            backBtn.addActionListener(e -> {
                // Επιστροφή στο Dashboard (που θα δείξει ξανά τη λίστα ή το αρχικό μενού)
                // Εναλλακτικά, αν θέλεις να κρατάει το state, θα χρειαζόταν άλλη διαχείριση,
                // αλλά το showDashboard είναι ασφαλές.
                navigation.showDashboard(user); 
            });

            JLabel titleLbl = new JLabel("Managing Account: " + account.getIban());
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLbl.setForeground(Color.DARK_GRAY);
            titleLbl.setBorder(new EmptyBorder(0, 20, 0, 0));

            JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            leftGroup.setOpaque(false);
            leftGroup.add(backBtn);
            leftGroup.add(titleLbl);

            adminHeader.add(leftGroup, BorderLayout.WEST);
            add(adminHeader, BorderLayout.NORTH);

        } else {
            // === CUSTOMER HEADER (Full/Standard) ===
            // Το κλασικό μεγάλο header με την εικόνα
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

            JLabel welcomeLabel = new JLabel(user.getFirstName() + " " + user.getLastName(), SwingConstants.CENTER) {
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

            JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            navPanel.setOpaque(false);
            
            JButton backBtn = createHeaderButton("Back / Dashboard");
            backBtn.addActionListener(e -> navigation.showDashboard(user));

            try {
                ImageIcon icon = new ImageIcon(new ImageIcon("logo.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                JLabel avatar = new JLabel(icon);
                navPanel.add(backBtn);
                navPanel.add(avatar);
            } catch (Exception e) { 
                navPanel.add(backBtn); 
            }

            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);
            topRow.add(welcomeLabel, BorderLayout.WEST);
            topRow.add(navPanel, BorderLayout.EAST);
            
            headerPanel.add(topRow, BorderLayout.NORTH);
            add(headerPanel, BorderLayout.NORTH);
        }

        // --- 2. MAIN BODY ---
        JPanel mainBody = new JPanel();
        mainBody.setBackground(MUSTARD_BG);
        mainBody.setLayout(new GridBagLayout()); 
        mainBody.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; 
        gbc.insets = new Insets(0, 0, 0, 20); 

        // === LEFT COLUMN ===
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        leftCol.add(createAccountCard(account));
        leftCol.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel infoTitle = new JLabel("Account Info");
        infoTitle.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        infoTitle.setForeground(new Color(40, 40, 40));
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftCol.add(infoTitle);
        leftCol.add(Box.createRigidArea(new Dimension(0, 15)));

        String mainOwnerAfm = account.getOwners().get(0);
        User mainOwner = BankSystem.getInstance().getUserManager().getUserByAfm(mainOwnerAfm);
        String mainName = (mainOwner != null) ? mainOwner.getFirstName() + " " + mainOwner.getLastName() : mainOwnerAfm;
        leftCol.add(createInfoBubble("MAIN OWNER", mainName));
        leftCol.add(Box.createRigidArea(new Dimension(0, 15)));

        if (account.getOwners().size() > 1) {
            for (int i = 1; i < account.getOwners().size(); i++) {
                String secAfm = account.getOwners().get(i);
                User secUser = BankSystem.getInstance().getUserManager().getUserByAfm(secAfm);
                String secName = (secUser != null) ? secUser.getFirstName() + " " + secUser.getLastName() : secAfm;
                leftCol.add(createInfoBubble("CO-OWNER", secName));
                leftCol.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        String dateStr = account.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        leftCol.add(createInfoBubble("Date of Create", dateStr));

        // --- ADMIN BUTTON: Add Co-Owner ---
        if (isAdmin) {
            leftCol.add(Box.createRigidArea(new Dimension(0, 20)));
            JButton addOwnerBtn = createHeaderButton("+ Add Co-Owner");
            addOwnerBtn.setBackground(new Color(34, 139, 34)); // Forest Green
            addOwnerBtn.setForeground(Color.WHITE);
            addOwnerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            addOwnerBtn.setMaximumSize(new Dimension(340, 45));

            addOwnerBtn.addActionListener(e -> {
                String newAfm = JOptionPane.showInputDialog(this, "Enter User AFM to add as Co-Owner:");
                if (newAfm != null && !newAfm.trim().isEmpty()) {
                    try {
                        BankController ctrl = new BankController();
                        ctrl.addOwnerToAccount(account.getIban(), newAfm.trim());
                        JOptionPane.showMessageDialog(this, "Co-Owner Added Successfully!");
                   
                         navigation.showDashboard(user); 
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    }
                }
            });
            leftCol.add(addOwnerBtn);
        }
        // ----------------------------------
        
        leftCol.add(Box.createVerticalGlue());

        gbc.gridx = 0;
        gbc.weightx = 0.35; 
        mainBody.add(leftCol, gbc);


        // === RIGHT COLUMN ===
        JPanel rightCol = new JPanel(new BorderLayout());
        rightCol.setOpaque(false);

        JLabel transTitle = new JLabel("Last Transactions");
        transTitle.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        transTitle.setForeground(new Color(40, 40, 40));
        transTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        rightCol.add(transTitle, BorderLayout.NORTH);

        JPanel transactionsContainer = new JPanel();
        transactionsContainer.setLayout(new BoxLayout(transactionsContainer, BoxLayout.Y_AXIS));
        transactionsContainer.setOpaque(false);

        List<Transaction> myTrans = new ArrayList<>(account.getTransaction());
        Collections.reverse(myTrans); 

        if (myTrans.isEmpty()) {
            JLabel noTrans = new JLabel("No transactions yet.");
            noTrans.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            transactionsContainer.add(noTrans);
        } else {
            for (Transaction t : myTrans) {
                transactionsContainer.add(createTransactionRow(t));
                transactionsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        JScrollPane scrollTrans = new JScrollPane(transactionsContainer);
        scrollTrans.getViewport().setOpaque(false);
        scrollTrans.setOpaque(false);
        scrollTrans.setBorder(null);
        scrollTrans.getVerticalScrollBar().setUI(new MyScrollBarUI());

        rightCol.add(scrollTrans, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.65; 
        gbc.insets = new Insets(0, 20, 0, 0); 
        mainBody.add(rightCol, gbc);

        add(mainBody, BorderLayout.CENTER);
    }


    // --- HELPERS ---

    private JPanel createInfoBubble(String title, String value) {
        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setMaximumSize(new Dimension(340, 85)); 
        bubble.setPreferredSize(new Dimension(340, 85));
        
        JPanel inner = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_COLOR); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel tLabel = new JLabel(title, SwingConstants.CENTER);
        tLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tLabel.setForeground(new Color(30, 30, 30));

        JLabel vLabel = new JLabel(value.toUpperCase(), SwingConstants.CENTER);
        vLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        vLabel.setForeground(Color.BLACK);

        inner.add(tLabel);
        inner.add(vLabel);
        
        bubble.add(inner, BorderLayout.CENTER);
        return bubble;
    }

    private JPanel createAccountCard(Account acc) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        card.setPreferredSize(new Dimension(340, 130)); 
        card.setMaximumSize(new Dimension(340, 130));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel typeLbl = new JLabel(acc.getAccountType().toString());
        typeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        
        JLabel balLbl = new JLabel(String.format("%.2f€", acc.getBalance()));
        balLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        balLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        // --- IBAN + Copy Button ---
        JPanel ibanPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ibanPanel.setOpaque(false);
        
        JLabel ibanLbl = new JLabel(acc.getIban());
        ibanLbl.setFont(new Font("Monospaced", Font.BOLD, 16));
        ibanLbl.setForeground(new Color(50, 50, 50));
        
        JButton copyBtn = createHeaderButton("Copy");
        copyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyBtn.setBackground(BUTTON_YELLOW);
        copyBtn.setForeground(Color.BLACK);
        copyBtn.setFocusPainted(false);
        copyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyBtn.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8)); 
        
        copyBtn.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(acc.getIban());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(this, "IBAN copied to clipboard!");
        });

        ibanPanel.add(ibanLbl);
        ibanPanel.add(copyBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(typeLbl, BorderLayout.WEST);
        top.add(balLbl, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(ibanPanel, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createTransactionRow(Transaction t) {
        String dateStr = (t.getTransactionID() != null) ? "Date info" : "Just now"; 
        String text = "<html><font size='5' color='#003366'>• " + t.getDescription() + "</font> " 
                    + "<font size='5' color='black'><b>" + t.getAmount() + "€</b></font><br>" 
                    + "<font size='3' color='#555555'>&nbsp;&nbsp;" + dateStr + "</font></html>";
        return new JLabel(text);
    }

    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BUTTON_YELLOW);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(BUTTON_YELLOW);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    class MyScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() { this.thumbColor = BUTTON_YELLOW; }
        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton j = new JButton(); j.setPreferredSize(new Dimension(0, 0)); return j;
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x+4, r.y+2, r.width-8, r.height-4, 10, 10);
            g2.dispose();
        }
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(200, 170, 90));
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.dispose();
        }
    }
}