 

package view;

import control.BankController;
import model.Account;
import model.User;
import view.StyleHelpers.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class AdminAccountsPanel extends JPanel {

    private BankController controller;
    private JPanel listPanel;
    private Consumer<Account> onAccountSelected; // Callback όταν πατηθεί ένας λογαριασμός

    public AdminAccountsPanel(User adminUser, Consumer<Account> onAccountSelected) {
        this.controller = new BankController();
        this.onAccountSelected = onAccountSelected;

        setLayout(new BorderLayout(0, 20));
        setBackground(StyleHelpers.MUSTARD_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- SEARCH BAR ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        topPanel.setOpaque(false);

        JTextField searchField = new RoundedTextField(20);
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setText("Enter Name or AFM...");
        
        // Λίγο UX: Καθαρίζει το text όταν κάνεις κλικ
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Enter Name or AFM...")) searchField.setText("");
            }
        });

        JButton searchBtn = StyleHelpers.createRoundedButton("Search Accounts");

        topPanel.add(searchField);
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        // --- RESULTS LIST ---
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());

        add(scroll, BorderLayout.CENTER);

        // --- LOGIC ---
        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if(query.isEmpty()) return;

            listPanel.removeAll();
            
            List<Account> results = controller.searchAccounts(query);
            
            if (results.isEmpty()) {
                JLabel lbl = new JLabel("No accounts found for: " + query);
                lbl.setFont(StyleHelpers.FONT_PLAIN);
                lbl.setAlignmentX(CENTER_ALIGNMENT);
                listPanel.add(lbl);
            } else {
                for (Account acc : results) {
                    // Χρησιμοποιούμε το ήδη υπάρχον AccountSelectionCard!
                    AccountSelectionCard card = new AccountSelectionCard(acc, selectedAcc -> {
                        // Όταν πατηθεί, καλούμε το callback για να αλλάξει σελίδα
                        onAccountSelected.accept(selectedAcc);
                    });
                    listPanel.add(card);
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
            listPanel.revalidate();
            listPanel.repaint();
        });
    }

    
}
