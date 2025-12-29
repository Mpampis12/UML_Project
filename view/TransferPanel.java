package view;

import model.User;
import control.BankController;
import view.StyleHelpers.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TransferPanel extends JPanel {

    private User user;
    private String type;
    private BankController controller;
    private CardLayout flowLayout;
    private JPanel flowContainer;
    private JLabel selectedIbanLbl;
    private JLabel detailsArea;
    private Runnable onTransactionSuccess;

    // Components
    JTextField fTarget = new RoundedTextField(15);
    JTextField fAmount = new RoundedTextField(15);
    JTextField fName = new RoundedTextField(15);
    JTextField fBankName = new RoundedTextField(15);
    JTextField fBic = new RoundedTextField(15);
    JTextField fAddress = new RoundedTextField(15);
    JTextField fCountry = new RoundedTextField(15);
    JComboBox<String> transferTypeBox = new JComboBox<>(new String[]{"INTERNAL", "SEPA", "SWIFT"});

    public TransferPanel(User user, String type, Runnable onSuccess) {
        this.user = user;
        this.type = type;
        this.controller = new BankController();
        this.onTransactionSuccess = onSuccess;

        setLayout(new BorderLayout());
        setOpaque(false);

        flowLayout = new CardLayout();
        flowContainer = new JPanel(flowLayout);
        flowContainer.setOpaque(false);

        selectedIbanLbl = new JLabel(); 
        detailsArea = new JLabel();

        initSplitView();
        initConfirmView();

        add(flowContainer, BorderLayout.CENTER);
    }

    public TransferPanel(User user, String type) {
        this(user, type, () -> {});
    }

    private void initSplitView() {
        JPanel splitView = new JPanel(new GridLayout(1, 2, 20, 0));
        splitView.setBackground(StyleHelpers.MUSTARD_BG);
        splitView.setBorder(new EmptyBorder(20, 40, 20, 40));

        // --- LEFT COLUMN: Account List (Ίδιο με πριν) ---
        JPanel leftCol = new JPanel(new BorderLayout());
        leftCol.setOpaque(false);
        
        JPanel accListPanel = new JPanel();
        accListPanel.setLayout(new BoxLayout(accListPanel, BoxLayout.Y_AXIS));
        accListPanel.setOpaque(false);
        
        java.util.List<AccountSelectionCard> cardList = new java.util.ArrayList<>();
        java.util.List<model.Account> accounts = controller.getAccountsForUser(user);

        for (model.Account acc : accounts) {
            AccountSelectionCard card = new AccountSelectionCard(acc, selectedAccount -> {
                selectedIbanLbl.setText(selectedAccount.getIban());
                for (AccountSelectionCard c : cardList) c.setSelected(c.getAccount().equals(selectedAccount));
            });
            cardList.add(card);
            accListPanel.add(card);
            accListPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scroll = new JScrollPane(accListPanel);
        scroll.getViewport().setOpaque(false); scroll.setOpaque(false); scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());
        leftCol.add(scroll, BorderLayout.CENTER);

        JPanel rightCol = new JPanel(new BorderLayout());
        rightCol.setOpaque(false);
        
        RoundedPanel box = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        box.setBorder(new EmptyBorder(30, 30, 30, 30));
        box.setLayout(new BorderLayout(0, 10)); 

        JLabel rTitle = new JLabel(type);
        rTitle.setFont(StyleHelpers.FONT_TITLE);
        rTitle.setHorizontalAlignment(SwingConstants.CENTER);
        box.add(rTitle, BorderLayout.NORTH);
        
        JPanel formGrid = new JPanel(new GridLayout(0, 2, 10, 15));
        formGrid.setOpaque(false);
        box.add(formGrid, BorderLayout.CENTER);

        JButton contBtn = StyleHelpers.createRoundedButton("Continue");
        box.add(contBtn, BorderLayout.SOUTH);
        
        // Logic για γέμισμα φόρμας
        updateForm(formGrid);

        // Listener για αλλαγή τύπου μεταφοράς
        transferTypeBox.addActionListener(e -> {
            updateForm(formGrid);
            box.revalidate(); box.repaint();
        });

        rightCol.add(box);
        
        // Προσθήκη στο split view
        splitView.add(leftCol);
        splitView.add(rightCol);

        // Continue Logic (Ίδιο με πριν)
        contBtn.addActionListener(e -> {
            String src = selectedIbanLbl.getText();
            if(src.isEmpty()) { JOptionPane.showMessageDialog(this, "Select Account First!"); return; }
            
            if(type.equals("WITHDRAW") || type.equals("DEPOSIT")) {
                try {
                    double am = Double.parseDouble(fAmount.getText());
                    if(type.equals("WITHDRAW")) controller.handleWithdraw(src, am);
                    else controller.handleDeposit(src, am);
                    JOptionPane.showMessageDialog(this, "Success!");
                    onTransactionSuccess.run();
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
                return;
            }

            StringBuilder sb = new StringBuilder("<html>");
            sb.append("Source: ").append(src).append("<br>");
            if(type.equals("TRANSFER")) {
                sb.append("To: ").append(fTarget.getText()).append("<br>");
                sb.append("Type: ").append(transferTypeBox.getSelectedItem()).append("<br>");
            } else {
                sb.append("RF: ").append(fTarget.getText()).append("<br>");
            }
            sb.append("Amount: ").append(fAmount.getText()).append("€</html>");
            
            detailsArea.setText(sb.toString());
            flowLayout.next(flowContainer);
        });

        flowContainer.add(splitView, "INPUT");
    }

    // Βοηθητική μέθοδος για να γεμίζει το Grid
    private void updateForm(JPanel p) {
        p.removeAll();
        
        if (type.equals("TRANSFER")) {

            
            String m = (String)transferTypeBox.getSelectedItem();
            
            if(m.equals("INTERNAL")) {
                           // Δημιουργούμε ένα οριζόντιο panel (γραμμή)
            JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            typeRow.setOpaque(false);

            // 1. Label με σταθερό πλάτος για στοίχιση
            JLabel typeLabel = StyleHelpers.createLabel("Type:");
            typeLabel.setPreferredSize(new Dimension(140, 30)); 

            // 2. ComboBox με συγκεκριμένο μέγεθος (ΠΡΟΣΟΧΗ: setPreferredSize, όχι setSize)
            transferTypeBox.setPreferredSize(new Dimension(150, 30)); 

            // Τα βάζουμε στη γραμμή
            typeRow.add(typeLabel);
            typeRow.add(transferTypeBox);

            // Βάζουμε τη γραμμή στο κεντρικό panel
            p.add(typeRow);
               // ΓΡΑΜΜΗ 1: Target IBAN
            JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            row1.setOpaque(false);
            
            JLabel lblTarget = StyleHelpers.createLabel("Target IBAN:");
            lblTarget.setPreferredSize(new Dimension(140, 30)); // Σταθερό πλάτος για στοίχιση
            
            row1.add(lblTarget);
            row1.add(fTarget); 
            p.add(row1);

            // ΓΡΑΜΜΗ 2: Amount
            JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            row2.setOpaque(false);
            
            JLabel lblAmount = StyleHelpers.createLabel("Amount (€):");
            lblAmount.setPreferredSize(new Dimension(140, 30)); // Ίδιο πλάτος με πάνω
            
            row2.add(lblAmount);
            row2.add(fAmount);
            p.add(row2);
            } else {
             p.add(StyleHelpers.createLabel("Type:"));
            p.add(transferTypeBox);
                // SEPA & SWIFT
                p.add(StyleHelpers.createLabel("Target IBAN:")); p.add(fTarget);
                p.add(StyleHelpers.createLabel("Beneficiary Name:")); p.add(fName);
                p.add(StyleHelpers.createLabel("Bank Name:")); p.add(fBankName);
                p.add(StyleHelpers.createLabel("BIC/SWIFT:")); p.add(fBic);
                p.add(StyleHelpers.createLabel("Amount (€):")); p.add(fAmount);
                
                if(m.equals("SWIFT")) {
                    p.add(StyleHelpers.createLabel("Address:")); p.add(fAddress);
                    p.add(StyleHelpers.createLabel("Country:")); p.add(fCountry);
                }
            }
        } else if (type.equals("PAYMENT")) {
     
            fTarget.setPreferredSize(new Dimension(180, 30)); 
    
            fAmount.setPreferredSize(new Dimension(100, 30));

             p.add(createRow("RF Code:", fTarget));
            p.add(createRow("Amount (€):", fAmount));
        } else {
            // Withdraw / Deposit
            
            p.add(StyleHelpers.createLabel("Amount (€):")); p.add(fAmount);
        }
    }

     private void initConfirmView() {
        JPanel confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));
        confirmPanel.setBackground(StyleHelpers.MUSTARD_BG);
        confirmPanel.setBorder(new EmptyBorder(50, 100, 50, 100)); // Κεντράρισμα με margins
        
        RoundedPanel box = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        JLabel title = new JLabel("Confirm Transaction");
        title.setFont(StyleHelpers.FONT_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        detailsArea.setFont(StyleHelpers.FONT_PLAIN);
        detailsArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsArea.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton confirmBtn = StyleHelpers.createRoundedButton("Confirm");
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton backBtn = StyleHelpers.createRoundedButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(title);
        box.add(detailsArea);
        box.add(confirmBtn);
        box.add(Box.createVerticalStrut(10));
        box.add(backBtn);
        
        // Ένα wrapper panel για να κεντράρει το box κάθετα
        JPanel centerWrap = new JPanel(new GridBagLayout()); // Εδώ επιτρέπεται ένα GridBag μόνο για κεντράρισμα ή απλό Flow
        centerWrap.setOpaque(false);
        centerWrap.add(box);

        confirmPanel.add(centerWrap);

        backBtn.addActionListener(e -> flowLayout.previous(flowContainer));
        
        confirmBtn.addActionListener(e -> {
            try {
                String src = selectedIbanLbl.getText();
                double am = Double.parseDouble(fAmount.getText());
                
                if (type.equals("TRANSFER")) {
                    String mode = (String)transferTypeBox.getSelectedItem();
                    if(mode.equals("INTERNAL")) {
                        controller.handleTransfer(src, fTarget.getText(), am);
                    } else {
                        controller.handleExternalTransfer(mode, src, am, fName.getText(), fTarget.getText(), fBic.getText(), fBankName.getText(), fAddress.getText(), fCountry.getText());
                    }
                } else if (type.equals("PAYMENT")) {
                     controller.payBill(fTarget.getText(), src, user.getAfm());
                }
                
                JOptionPane.showMessageDialog(this, "Transaction Successful!");
                onTransactionSuccess.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        flowContainer.add(confirmPanel, "CONFIRM");
    }
    private JPanel createRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setOpaque(false);
        JLabel lbl = StyleHelpers.createLabel(labelText);
        lbl.setPreferredSize(new Dimension(160, 30)); // Λίγο πιο φαρδύ label εδώ
        row.add(lbl);
        row.add(field);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return row;
    }
}