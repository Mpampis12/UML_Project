package view;

import model.User;
import model.Account;
import services.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AccountDetailsPage extends JPanel {

    private BankBridge navigation;
    private User user;
    private Account account;

    // Χρώματα (Ίδια με Dashboard)
    private static final Color MUSTARD_BG = new Color(228, 196, 101);
    private static final Color BUTTON_YELLOW = new Color(255, 180, 0);
    private static final Color CARD_COLOR = new Color(160, 140, 90, 200);

    public AccountDetailsPage(BankBridge navigation, User user, Account account) {
        this.navigation = navigation;
        this.user = user;
        this.account = account;

        setLayout(new BorderLayout());

        // --- 1. HEADER (Ίδιο με Dashboard αλλά με κουμπί Back) ---
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

        // Τίτλος "Bank of TUC" σε κίτρινο πλαίσιο (όπως στη φώτο)
        JLabel titleLabel = createYellowLabel(" Bank of TUC ");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));

        // Κουμπιά Header
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        navPanel.setOpaque(false);
        
        JButton backBtn = createHeaderButton("Back to Dashboard");
        backBtn.addActionListener(e -> navigation.showDashboard(user)); // Επιστροφή

        navPanel.add(backBtn);
        // Μπορείς να προσθέσεις κι άλλα κουμπιά εδώ αν θες

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(titleLabel, BorderLayout.WEST);
        topRow.add(navPanel, BorderLayout.EAST);
        
        headerPanel.add(topRow, BorderLayout.NORTH);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. MAIN BODY (YELLOW) ---
        JPanel mainBody = new JPanel();
        mainBody.setBackground(MUSTARD_BG);
        mainBody.setLayout(new GridLayout(1, 2, 40, 0)); // Χωρίζουμε στη μέση
        mainBody.setBorder(new EmptyBorder(40, 60, 40, 60));

        // --- ΑΡΙΣΤΕΡΑ: Η Κάρτα του Λογαριασμού ---
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);
        
        // Φτιάχνουμε την κάρτα (ίδια λογική με Dashboard)
        JPanel accCard = createAccountCard(account);
        leftCol.add(accCard);
        
        // --- ΔΕΞΙΑ: Πληροφορίες (Account Info) ---
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        JLabel infoTitle = new JLabel("Account Info");
        infoTitle.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        infoTitle.setForeground(new Color(40, 40, 40));
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        rightCol.add(infoTitle);
        rightCol.add(Box.createRigidArea(new Dimension(0, 30)));

         
        String mainOwnerAfm = account.getOwners().get(0);  
        User mainOwner = BankSystem.getInstance().getUserManager().getUserByAfm(mainOwnerAfm);
        String mainName = (mainOwner != null) ? mainOwner.getFirstName() + " " + mainOwner.getLastName() : mainOwnerAfm;
        
        rightCol.add(createInfoBubble("MAIN OWNER", mainName));
        rightCol.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Secondary Owners (αν υπάρχουν)
        if (account.getOwners().size() > 1) {
            for (int i = 1; i < account.getOwners().size(); i++) {
                String secAfm = account.getOwners().get(i);
                User secUser = BankSystem.getInstance().getUserManager().getUserByAfm(secAfm);
                String secName = (secUser != null) ? secUser.getFirstName() + " " + secUser.getLastName() : secAfm;
                
                rightCol.add(createInfoBubble("SECONDARY OWNER", secName));
                rightCol.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        }

        // 3. Date Created
        String dateStr = account.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        rightCol.add(createInfoBubble("Date of Create", dateStr));

        // Προσθήκη στο Main Body
        mainBody.add(leftCol);
        mainBody.add(rightCol);

        add(mainBody, BorderLayout.CENTER);
    }

    // --- HELPER: Δημιουργία των Info Bubbles ---
    private JPanel createInfoBubble(String title, String value) {
        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setMaximumSize(new Dimension(400, 90)); // Σταθερό μέγεθος
        
        // Custom painting για το σχήμα
        JPanel inner = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_COLOR); // Το ίδιο χρώμα με τις κάρτες
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel tLabel = new JLabel(title, SwingConstants.CENTER);
        tLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tLabel.setForeground(new Color(30, 30, 30));

        JLabel vLabel = new JLabel(value.toUpperCase(), SwingConstants.CENTER);
        vLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        vLabel.setForeground(Color.BLACK);

        inner.add(tLabel);
        inner.add(vLabel);
        
        // Το διακοσμητικό ")" δεξιά
     
        
        bubble.add(inner, BorderLayout.CENTER);
         
        
        return bubble;
    }

    // --- HELPER: Δημιουργία Κάρτας Λογαριασμού (Copy από Dashboard) ---
    private JPanel createAccountCard(Account acc) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
            }
        };
        card.setPreferredSize(new Dimension(400, 130));
        card.setMaximumSize(new Dimension(400, 130));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel typeLbl = new JLabel(acc.getAccountType().toString());
        typeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        
        JLabel balLbl = new JLabel(String.format("%.2f€", acc.getBalance()));
        balLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        balLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel ibanLbl = new JLabel(acc.getIban());
        ibanLbl.setFont(new Font("Monospaced", Font.BOLD, 20));
        ibanLbl.setForeground(new Color(50, 50, 50));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(typeLbl, BorderLayout.WEST);
        top.add(balLbl, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(ibanLbl, BorderLayout.SOUTH);
        
        // Διακοσμητικό
        JLabel decor = new JLabel(")");
        decor.setFont(new Font("Segoe UI", Font.BOLD, 40));
        card.add(decor, BorderLayout.EAST);

        return card;
    }

    // --- HELPER: Label με κίτρινο φόντο (για τον τίτλο) ---
    private JLabel createYellowLabel(String text) {
        return new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BUTTON_YELLOW);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
    }
    
    // --- HELPER: Κουμπί Header ---
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
}