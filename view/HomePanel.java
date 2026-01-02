package view;

import model.Account;
import model.Transaction;
import model.User;
import control.BankController;
import view.StyleHelpers.*; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class HomePanel extends JPanel {

    private User user;
    private BankController controller;
    private BankBridge navigation;
    
    // Components που πρέπει να ανανεώνονται
    private JLabel totalBalanceLabel;
    private JPanel accountsContainer;
    private JPanel transContainer;

    public HomePanel(User user, BankBridge navigation) {
        this.user = user;
        this.navigation = navigation;
        this.controller = new BankController();
        
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(StyleHelpers.MUSTARD_BG);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        initUI();
        
        // ΑΥΤΟΜΑΤΟ REFRESH: Όταν εμφανίζεται το Panel, ξαναφορτώνει τα δεδομένα
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshData();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void initUI() {
        // --- LEFT COLUMN SETUP ---
        JPanel leftCol = new JPanel(new BorderLayout());
        leftCol.setOpaque(false);
        
        totalBalanceLabel = new JLabel("Total Balance: ...");
        totalBalanceLabel.setFont(StyleHelpers.FONT_TITLE);
        totalBalanceLabel.setForeground(new Color(40, 40, 40));
        totalBalanceLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        accountsContainer = new JPanel();
        accountsContainer.setLayout(new BoxLayout(accountsContainer, BoxLayout.Y_AXIS));
        accountsContainer.setOpaque(false);

        JScrollPane scrollAcc = new JScrollPane(accountsContainer);
        scrollAcc.getViewport().setOpaque(false); scrollAcc.setOpaque(false); scrollAcc.setBorder(null);
        scrollAcc.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());

        leftCol.add(totalBalanceLabel, BorderLayout.NORTH);
        leftCol.add(scrollAcc, BorderLayout.CENTER);

        // --- RIGHT COLUMN SETUP ---
        JPanel rightCol = new JPanel(new BorderLayout());
        rightCol.setOpaque(false);
        
        JLabel transTitle = new JLabel("Last Transactions");
        transTitle.setFont(StyleHelpers.FONT_TITLE);
        transTitle.setForeground(new Color(40, 40, 40));
        transTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        transContainer = new JPanel();
        transContainer.setLayout(new BoxLayout(transContainer, BoxLayout.Y_AXIS));
        transContainer.setOpaque(false);

        JScrollPane scrollTrans = new JScrollPane(transContainer);
        scrollTrans.getViewport().setOpaque(false); 
        scrollTrans.setOpaque(false); 
        scrollTrans.setBorder(null);
        scrollTrans.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());

        rightCol.add(transTitle, BorderLayout.NORTH);
        rightCol.add(scrollTrans, BorderLayout.CENTER);

        add(leftCol);
        add(rightCol);
        
        // Πρώτη φόρτωση δεδομένων
        refreshData();
    }
    
    // --- Η ΜΕΘΟΔΟΣ ΠΟΥ ΚΑΝΕΙ ΤΗ ΔΟΥΛΕΙΑ ---
    private void refreshData() {
        // 1. Καθαρισμός παλιών
        accountsContainer.removeAll();
        transContainer.removeAll();
        
        // 2. Λήψη νέων δεδομένων από τη βάση (μέσω controller)
        List<Account> accounts = controller.getAccountsForUser(user);
        
        // 3. Υπολογισμός Συνολικού Υπολοίπου
        double total = accounts.stream().mapToDouble(Account::getBalance).sum();
        totalBalanceLabel.setText(String.format("Total Balance: %.2f€", total));

        // 4. Γέμισμα λίστας λογαριασμών
        for (Account acc : accounts) {
            accountsContainer.add(new AccountCard(acc));
            accountsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // 5. Γέμισμα λίστας συναλλαγών (Combined History)
        List<Transaction> allTransactions = new ArrayList<>();
        for (Account acc : accounts) allTransactions.addAll(acc.getTransaction());
        
        // Ταξινόμηση (Πιο πρόσφατα πρώτα) - Προϋποθέτει ότι τα Transactions μπαίνουν με τη σειρά
        Collections.reverse(allTransactions); 
        // Αν θέλετε ταξινόμηση με βάση ημερομηνία:
        // allTransactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

        int count = 0;
        for (Transaction t : allTransactions) {
            if (count++ >= 6) break; // Δείξε μόνο τα 6 τελευταία
            transContainer.add(new TransactionRow(t));
            transContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // 6. Ενημέρωση UI
        accountsContainer.revalidate();
        accountsContainer.repaint();
        transContainer.revalidate();
        transContainer.repaint();
    }
    
    // --- INNER CLASSES (AccountCard, TransactionRow) ---
    // (Ίδιες με πριν, απλά σιγουρέψου ότι είναι μέσα στην κλάση)
    
    class AccountCard extends RoundedPanel {
        public AccountCard(Account acc) {
            super(40, StyleHelpers.CARD_COLOR);
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(400, 120));
            setMaximumSize(new Dimension(500, 120));
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

            JButton copyBtn = StyleHelpers.createRoundedButton("Copy");
            copyBtn.setPreferredSize(new Dimension(80, 25));
            copyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            copyBtn.addActionListener(e -> {
                 Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(acc.getIban()), null);
                 JOptionPane.showMessageDialog(this, "IBAN copied!");
            });

            JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            bot.setOpaque(false); bot.add(ibanLbl); bot.add(Box.createHorizontalStrut(10)); bot.add(copyBtn);
            add(bot, BorderLayout.SOUTH);

            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) { navigation.showAccountDetails(user, acc); }
            });
        }
    }
    
class TransactionRow extends JPanel {
        
        public TransactionRow(Transaction t) {
            // Χρήση BoxLayout (Κάθετα) για να μπουν το ένα κάτω από το άλλο
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false); // Διαφάνεια για να φαίνεται το background
            setBorder(new EmptyBorder(5, 0, 5, 0)); // Λίγο αέρα πάνω-κάτω

            // --- 1. ΠΑΝΩ ΜΕΡΟΣ: Περιγραφή & Ποσό ---
            // Χρησιμοποιούμε HTML για να μορφοποιήσουμε τα χρώματα στην ίδια γραμμή
            String amountStr = String.format("%.2f€", t.getAmount());
            JLabel topLabel = new JLabel("<html><font size='4' color='#003366'>• " + t.getDescription() + "</font> <font size='4' color='black'><b> " + amountStr + "</b></font></html>");
            topLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // --- 2. ΚΑΤΩ ΜΕΡΟΣ: Ημερομηνία ---
            String dateText = "Unknown Date";
            if (t.getTimestamp() != null) {
                // Μορφοποίηση ημερομηνίας (π.χ. 29/12/2025 10:30)
                dateText = t.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }
            
            JLabel dateLabel = new JLabel(dateText);
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Μικρότερη γραμματοσειρά
            dateLabel.setForeground(Color.GRAY); // Γκρι χρώμα για να ξεχωρίζει
            dateLabel.setBorder(new EmptyBorder(0, 12, 0, 0)); // Εσοχή (Indent) για να στοιχιστεί με το κείμενο
            dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            add(topLabel);
            add(Box.createVerticalStrut(2)); // Μικρό κενό ανάμεσα στις γραμμές
            add(dateLabel);
        }
    }
}