package view;

import model.Account;
import model.Transaction;
import model.User;
import control.BankController;
import view.StyleHelpers.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryPanel extends JPanel {

    private User user;
    private BankController controller;

    public HistoryPanel(User user) {
        this.user = user;
        this.controller = new BankController();
        
        setLayout(new BorderLayout());
        setBackground(StyleHelpers.MUSTARD_BG);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Transaction History");
        title.setFont(StyleHelpers.FONT_TITLE);
        title.setForeground(new Color(40, 40, 40));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Table Init
        String[] columns = {"Date", "Type", "Description", "Amount", "Source", "Target"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        table.setOpaque(false);
        table.setBackground(new Color(0,0,0,0));
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setShowGrid(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(StyleHelpers.BUTTON_YELLOW);
        header.setOpaque(false);

        // Load Data
        List<Account> accounts = controller.getAccountsForUser(user);
        List<Transaction> allTrans = new ArrayList<>();
        for (Account acc : accounts) 
            allTrans.addAll(acc.getTransaction());
        Collections.reverse(allTrans);

        for (Transaction t : allTrans) {
            String src = (t.getSource() != null) ? t.getSource().toString() : "-";
            String trg = (t.getTarget() != null) ? t.getTarget().toString() : "-";
            String date = (t.getTransactionID() != null) ? t.getTimestamp().toString() : "-";
            model.addRow(new Object[]{date, t.getType(), t.getDescription(), String.format("%.2f€", t.getAmount()), src, trg});
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setOpaque(false); scrollPane.setOpaque(false); scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}