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
        JPanel left = new JPanel(new BorderLayout()); left.setOpaque(false);
        JLabel title = new JLabel("Orders"); title.setFont(StyleHelpers.FONT_TITLE);
        left.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel(); list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS)); list.setOpaque(false);
        List<StandingOrder> orders = controller.getStandingOrdersForUser(user);

        // RIGHT: Form
        RoundedPanel formBox = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        formBox.setLayout(new GridBagLayout());
        JTextField fTarget = new RoundedTextField(15);
        JTextField fAmount = new RoundedTextField(15);
        JComboBox<String> accBox = new JComboBox<>();
        List<Account> accounts = controller.getAccountsForUser(user);
        for(Account a : accounts) accBox.addItem(a.getIban());
        JButton saveBtn = StyleHelpers.createRoundedButton("Create New");

        for(StandingOrder so : orders) {
            RoundedPanel card = new RoundedPanel(20, StyleHelpers.CARD_COLOR);
            card.setLayout(new BorderLayout()); card.setBorder(new EmptyBorder(10,10,10,10));
            card.setMaximumSize(new Dimension(400, 60)); card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            card.add(new JLabel("<html><b>"+so.getDescription()+"</b><br>"+so.getAmount()+"€</html>"), BorderLayout.CENTER);
            card.addMouseListener(new java.awt.event.MouseAdapter(){
                public void mouseClicked(java.awt.event.MouseEvent e){
                    fTarget.setText(so.getTarget().toString());
                    fAmount.setText(String.valueOf(so.getAmount()));
                    saveBtn.setText("Update");
                }
            });
            list.add(card); list.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scroll = new JScrollPane(list);
        scroll.getViewport().setOpaque(false); scroll.setOpaque(false); scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());
        left.add(scroll, BorderLayout.CENTER);

        // Build Right
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets=new Insets(10,5,10,5); gbc.gridx=0; gbc.gridy=0;
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
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error"); }
        });

        JPanel rightWrap = new JPanel(new GridBagLayout()); rightWrap.setOpaque(false);
        rightWrap.add(formBox);
        
        add(left); add(rightWrap);
    }
}