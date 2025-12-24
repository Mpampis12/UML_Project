package view;

import model.Account;
import model.User;
import control.BankController;
import view.StyleHelpers.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TransferPanel extends JPanel {

    private User user;
    private BankController controller;
    private String type; // TRANSFER, PAYMENT, WITHDRAW, DEPOSIT
    private CardLayout flowLayout;
    private JPanel flowContainer;

    public TransferPanel(User user, String type) {
        this.user = user;
        this.type = type;
        this.controller = new BankController();

        flowLayout = new CardLayout();
        flowContainer = new JPanel(flowLayout);
        flowContainer.setOpaque(false);
        
        setLayout(new BorderLayout());
        setBackground(StyleHelpers.MUSTARD_BG);
        add(flowContainer, BorderLayout.CENTER);

        initSplitView();
        initConfirmView();
    }
    
    // Components reference for Confirmation
    private JLabel selectedIbanLbl = new JLabel();
    private JTextField fTarget = new RoundedTextField(15);
    private JTextField fAmount = new RoundedTextField(15);
    private JTextField fName = new RoundedTextField(15);
    private JTextField fBic = new RoundedTextField(15);
    private JTextField fBankName = new RoundedTextField(15);
    private JTextField fAddress = new RoundedTextField(15);
    private JTextField fCountry = new RoundedTextField(15);
    private JComboBox<String> transferTypeBox = new JComboBox<>(new String[]{"INTERNAL", "SEPA", "SWIFT"});
    private JTextArea detailsArea = new JTextArea(8, 25);

    private void initSplitView() {
        JPanel splitView = new JPanel(new GridLayout(1, 2, 20, 0));
        splitView.setBackground(StyleHelpers.MUSTARD_BG);
        splitView.setBorder(new EmptyBorder(20, 40, 20, 40));

        // LEFT: Account List
        JPanel leftCol = new JPanel(new BorderLayout());
        leftCol.setOpaque(false);
        JLabel lTitle = new JLabel("Select Account");
        lTitle.setFont(StyleHelpers.FONT_TITLE);
        leftCol.add(lTitle, BorderLayout.NORTH);

        JPanel accListPanel = new JPanel();
        accListPanel.setLayout(new BoxLayout(accListPanel, BoxLayout.Y_AXIS));
        accListPanel.setOpaque(false);

        List<Account> accounts = controller.getAccountsForUser(user);
        for (Account acc : accounts) {
            RoundedPanel card = new RoundedPanel(20, StyleHelpers.CARD_COLOR);
            card.setLayout(new BorderLayout());
            card.setBorder(new EmptyBorder(10, 15, 10, 15));
            card.setMaximumSize(new Dimension(500, 80));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JLabel lbl = new JLabel("<html><b>" + acc.getAccountType() + "</b><br>" + acc.getIban() + "</html>");
            JLabel bal = new JLabel(String.format("%.2f€", acc.getBalance()));
            bal.setFont(StyleHelpers.FONT_BOLD);
            
            card.add(lbl, BorderLayout.CENTER);
            card.add(bal, BorderLayout.EAST);
            
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    selectedIbanLbl.setText(acc.getIban());
                    JOptionPane.showMessageDialog(splitView, "Selected: " + acc.getIban());
                }
            });
            accListPanel.add(card);
            accListPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scroll = new JScrollPane(accListPanel);
        scroll.getViewport().setOpaque(false); scroll.setOpaque(false); scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());
        leftCol.add(scroll, BorderLayout.CENTER);

        // RIGHT: Form
        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setOpaque(false);
        RoundedPanel box = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        box.setLayout(new GridBagLayout());
        box.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel rTitle = new JLabel(type);
        rTitle.setFont(StyleHelpers.FONT_TITLE);
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; box.add(rTitle, gbc);
        
        // Dynamic Fields
        JPanel dyn = new JPanel(new GridBagLayout());
        dyn.setOpaque(false);
        gbc.gridy++; box.add(dyn, gbc);

        GridBagConstraints sub = new GridBagConstraints();
        sub.insets = new Insets(5, 0, 5, 0); sub.gridx=0; sub.gridy=0;

        if (type.equals("TRANSFER")) {
            addRow(dyn, sub, "Type:", transferTypeBox);
            addRow(dyn, sub, "Target:", fTarget);
            addRow(dyn, sub, "Amount:", fAmount);
            transferTypeBox.addActionListener(e -> {
               String m = (String)transferTypeBox.getSelectedItem();
               dyn.removeAll(); sub.gridy=0;
               addRow(dyn, sub, "Type:", transferTypeBox);
               addRow(dyn, sub, "Target:", fTarget);
               addRow(dyn, sub, "Amount:", fAmount);
               if(!m.equals("INTERNAL")) {
                   addRow(dyn, sub, "Name:", fName);
                   addRow(dyn, sub, "Bank:", fBankName);
                   addRow(dyn, sub, "BIC:", fBic);
               }
               if(m.equals("SWIFT")) {
                   addRow(dyn, sub, "Addr:", fAddress);
                   addRow(dyn, sub, "Country:", fCountry);
               }
               dyn.revalidate(); dyn.repaint(); box.revalidate(); box.repaint();
            });
        } else if (type.equals("PAYMENT")) {
            addRow(dyn, sub, "RF Code:", fTarget);
        } else {
            addRow(dyn, sub, "Amount:", fAmount);
        }

        JButton contBtn = StyleHelpers.createRoundedButton("Continue");
        gbc.gridy++; box.add(contBtn, gbc);
        
        rightCol.add(box);
        splitView.add(leftCol);
        splitView.add(rightCol);

        // Continue Logic
        contBtn.addActionListener(e -> {
            String src = selectedIbanLbl.getText();
            if(src.isEmpty()) { JOptionPane.showMessageDialog(this, "Select Account First!"); return; }
            
            // Direct Execute for simple types
            if(type.equals("WITHDRAW") || type.equals("DEPOSIT")) {
                try {
                    double am = Double.parseDouble(fAmount.getText());
                    if(type.equals("WITHDRAW")) controller.handleWithdraw(src, am);
                    else controller.handleDeposit(src, am);
                    JOptionPane.showMessageDialog(this, "Success!");
                    // Note: Needs parent refresh
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
                return;
            }

            // Prepare Confirm Text
            StringBuilder sb = new StringBuilder();
            sb.append("Source: ").append(src).append("\n");
            if(type.equals("TRANSFER")) {
                sb.append("To: ").append(fTarget.getText()).append("\n");
                sb.append("Type: ").append(transferTypeBox.getSelectedItem()).append("\n");
            } else sb.append("RF: ").append(fTarget.getText());
            
            detailsArea.setText(sb.toString());
            flowLayout.next(flowContainer);
        });

        flowContainer.add(splitView, "INPUT");
    }

    private void initConfirmView() {
        JPanel confirmScreen = new JPanel(new GridBagLayout());
        confirmScreen.setBackground(StyleHelpers.MUSTARD_BG);
        
        RoundedPanel box = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        box.setLayout(new GridBagLayout());
        box.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JLabel title = new JLabel("Confirm");
        title.setFont(StyleHelpers.FONT_TITLE);
        
        detailsArea.setEditable(false); detailsArea.setOpaque(false); detailsArea.setBorder(null);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JButton back = StyleHelpers.createRoundedButton("Back");
        back.setBackground(Color.GRAY);
        JButton exec = StyleHelpers.createRoundedButton("Execute");

        back.addActionListener(e -> flowLayout.previous(flowContainer));
        exec.addActionListener(e -> {
            try {
                String src = selectedIbanLbl.getText();
                if(type.equals("TRANSFER")) {
                    String m = (String)transferTypeBox.getSelectedItem();
                    double am = Double.parseDouble(fAmount.getText());
                    if(m.equals("INTERNAL")) controller.handleTransfer(src, fTarget.getText(), am);
                    else controller.handleExternalTransfer(m, src, am, fName.getText(), fTarget.getText(), fBic.getText(), fBankName.getText(), fAddress.getText(), fCountry.getText());
                } else if(type.equals("PAYMENT")) {
                     controller.payBill(fTarget.getText(), src, user.getAfm());
                }
                JOptionPane.showMessageDialog(this, "Success!");
                // Here we should ideally navigate back to Home
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0; box.add(title, gbc);
        gbc.gridy++; gbc.insets=new Insets(20,0,20,0); box.add(detailsArea, gbc);
        
        JPanel btns = new JPanel(); btns.setOpaque(false);
        btns.add(back); btns.add(exec);
        gbc.gridy++; box.add(btns, gbc);
        
        confirmScreen.add(box);
        flowContainer.add(confirmScreen, "CONFIRM");
    }

    private void addRow(JPanel p, GridBagConstraints gbc, String txt, Component c) {
        gbc.gridx=0; p.add(StyleHelpers.createLabel(txt), gbc);
        gbc.gridx=1; p.add(c, gbc);
        gbc.gridy++;
    }
}