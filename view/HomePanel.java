package view;

import model.Account;
import model.Transaction;
import model.User;
import control.BankController;
import view.StyleHelpers.*; // Import τα styles

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class HomePanel extends JPanel {

    private User user;
    private BankController controller;
    private BankBridge navigation; // Για τα details

    public HomePanel(User user, BankBridge navigation) {
        this.user = user;
        this.navigation = navigation;
        this.controller = new BankController();
        
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(StyleHelpers.MUSTARD_BG);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        initUI();
    }

    private void initUI() {
        // --- DATA FETCHING ---
        List<Account> accounts = controller.getAccountsForUser(user);
        double total = accounts.stream().mapToDouble(Account::getBalance).sum();

        // --- LEFT COLUMN ---
        JPanel leftCol = new JPanel(new BorderLayout());
        leftCol.setOpaque(false);
        
        JLabel totalBalanceLabel = new JLabel(String.format("Total Balance: %.2f€", total));
        totalBalanceLabel.setFont(StyleHelpers.FONT_TITLE);
        totalBalanceLabel.setForeground(new Color(40, 40, 40));
        totalBalanceLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel accountsContainer = new JPanel();
        accountsContainer.setLayout(new BoxLayout(accountsContainer, BoxLayout.Y_AXIS));
        accountsContainer.setOpaque(false);
        
        for (Account acc : accounts) {
            accountsContainer.add(new AccountCard(acc));
            accountsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JScrollPane scrollAcc = new JScrollPane(accountsContainer);
        scrollAcc.getViewport().setOpaque(false); scrollAcc.setOpaque(false); scrollAcc.setBorder(null);
        scrollAcc.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());

        leftCol.add(totalBalanceLabel, BorderLayout.NORTH);
        leftCol.add(scrollAcc, BorderLayout.CENTER);

        // --- RIGHT COLUMN ---
        JPanel rightCol = new JPanel(new BorderLayout());
        rightCol.setOpaque(false);
        
        JLabel transTitle = new JLabel("Last Transactions");
        transTitle.setFont(StyleHelpers.FONT_TITLE);
        transTitle.setForeground(new Color(40, 40, 40));
        transTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel transContainer = new JPanel();
        transContainer.setLayout(new BoxLayout(transContainer, BoxLayout.Y_AXIS));
        transContainer.setOpaque(false);

        List<Transaction> allTransactions = new ArrayList<>();
        for (Account acc : accounts) allTransactions.addAll(acc.getTransaction());
        Collections.reverse(allTransactions);

        int count = 0;
        for (Transaction t : allTransactions) {
            if (count++ >= 6) break;
            transContainer.add(new TransactionRow(t));
            transContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollTrans = new JScrollPane(transContainer);
        scrollTrans.getViewport().setOpaque(false); scrollTrans.setOpaque(false); scrollTrans.setBorder(null);
        scrollTrans.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());

        rightCol.add(transTitle, BorderLayout.NORTH);
        rightCol.add(scrollTrans, BorderLayout.CENTER);

        add(leftCol);
        add(rightCol);
    }
    
    // --- INNER CLASSES (AccountCard, TransactionRow) ---
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
            copyBtn.setPreferredSize(new Dimension(60, 25));
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
    
    class TransactionRow extends JLabel {
        public TransactionRow(Transaction t) {
            setText("<html><font size='5' color='#003366'>• " + t.getDescription() + "</font> <font size='5' color='black'><b>" + t.getAmount() + "€</b></font></html>");
        }
    }
}