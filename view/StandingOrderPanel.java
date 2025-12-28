package view;

import model.Account;
import model.StandingOrder;
import model.User;
import model.Iban;
import control.BankController;
import view.StyleHelpers.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList; // Χρειάζεται για τη λίστα καρτών
import java.util.List;

public class StandingOrderPanel extends JPanel {
    
    private User user;
    private BankController controller;

    public StandingOrderPanel(User user) {
        this.user = user;
        this.controller = new BankController();
        
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(StyleHelpers.MUSTARD_BG);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // LEFT: List
        JPanel left = new JPanel(new BorderLayout()); 
        left.setOpaque(false);
        JLabel title = new JLabel("Orders"); 
        title.setFont(StyleHelpers.FONT_TITLE);
        left.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(); 
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); 
        listPanel.setOpaque(false);
        
        List<StandingOrder> orders = controller.getStandingOrdersForUser(user);

        // RIGHT: Form Components
        RoundedPanel formBox = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        formBox.setLayout(new GridBagLayout());
        JTextField fTarget = new RoundedTextField(15);
        JTextField fAmount = new RoundedTextField(15);
        JComboBox<String> accBox = new JComboBox<>();
        List<Account> accounts = controller.getAccountsForUser(user);
        for(Account a : accounts) accBox.addItem(a.getIban());
        JButton saveBtn = StyleHelpers.createRoundedButton("Create New");

        List<StandingOrderCard> cardList = new ArrayList<>();

        for(StandingOrder so : orders) {
            StandingOrderCard card = new StandingOrderCard(so, selectedOrder -> {
                // 1. Ενημέρωση της φόρμας δεξιά
                fTarget.setText(selectedOrder.getTarget().toString());
                fAmount.setText(String.valueOf(selectedOrder.getAmount()));
                saveBtn.setText("Update");

                // 2. Highlight Logic (Mutual Exclusion)
                for (StandingOrderCard c : cardList) {
                    if (c.getStandingOrder().equals(selectedOrder)) {
                        c.setSelected(true);
                    } else {
                        c.setSelected(false);
                    }
                }
            });
            
            cardList.add(card);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
        }
        // ---------------------------------------------
        
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getViewport().setOpaque(false); 
        scroll.setOpaque(false); 
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());
        left.add(scroll, BorderLayout.CENTER);

        // Build Right Form Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets=new Insets(10,5,10,5); 
        gbc.gridx=0; gbc.gridy=0;
        formBox.add(StyleHelpers.createLabel("Source:"), gbc);
        gbc.gridx=1; formBox.add(accBox, gbc);
        gbc.gridx=0; gbc.gridy++; formBox.add(StyleHelpers.createLabel("Target:"), gbc);
        gbc.gridx=1; formBox.add(fTarget, gbc);
        gbc.gridx=0; gbc.gridy++; formBox.add(StyleHelpers.createLabel("Amount:"), gbc);
        gbc.gridx=1; formBox.add(fAmount, gbc);
        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2; formBox.add(saveBtn, gbc);

        // Save Logic
        saveBtn.addActionListener(e -> {
            try {
               StandingOrder so = new StandingOrder(new Iban((String)accBox.getSelectedItem()), new Iban(fTarget.getText()), 
                        Double.parseDouble(fAmount.getText()), "Monthly", StandingOrder.StandingOrderPurpose.TRANSFER);
               controller.createStandingOrder(so);
               JOptionPane.showMessageDialog(this, "Saved!");
               // Εδώ ιδανικά θα έπρεπε να κάνεις refresh τη λίστα αριστερά
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error"); }
        });

        JPanel rightWrap = new JPanel(new GridBagLayout()); 
        rightWrap.setOpaque(false);
        rightWrap.add(formBox);
        
        add(left); add(rightWrap);
    }
}