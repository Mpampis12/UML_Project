package view;

import model.Bill;
import model.User;
import model.Account;
import control.BankController;
import view.StyleHelpers.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TransferPanel extends JPanel implements Refreshable {

    private User user;
    private String type;
    private BankController controller;
    private CardLayout flowLayout;
    private JPanel flowContainer;
    private JLabel selectedIbanLbl;
    private JLabel detailsArea;
    private Runnable onTransactionSuccess;
    
    // Panel για τη λίστα λογαριασμών
    private JPanel accListPanel; 

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
        this.controller = BankController.getInstance();
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
    
    // --- Η ΜΕΘΟΔΟΣ REFRESH ---
    @Override
    public void refresh() {
        fTarget.setText("");
        fAmount.setText("");
        fName.setText("");
        loadAccounts(); // Ξαναφορτώνει τα υπόλοιπα
    }

    private void initSplitView() {
        JPanel splitView = new JPanel(new GridLayout(1, 2, 20, 0));
        splitView.setBackground(StyleHelpers.MUSTARD_BG);
        splitView.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel leftCol = new JPanel(new BorderLayout());
        leftCol.setOpaque(false);
        
        accListPanel = new JPanel();
        accListPanel.setLayout(new BoxLayout(accListPanel, BoxLayout.Y_AXIS));
        accListPanel.setOpaque(false);
        
        loadAccounts(); // Πρώτη φόρτωση
        
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
        
        updateForm(formGrid);

        transferTypeBox.addActionListener(e -> {
            updateForm(formGrid);
            box.revalidate(); box.repaint();
        });

        rightCol.add(box);
        
        splitView.add(leftCol);
        splitView.add(rightCol);

        contBtn.addActionListener(e -> handleContinue());

        flowContainer.add(splitView, "INPUT");
    }

    private void loadAccounts() {
        accListPanel.removeAll();
        java.util.List<AccountSelectionCard> cardList = new java.util.ArrayList<>();
        List<Account> accounts = controller.getAccountsForUser(user);

        for (Account acc : accounts) {
            AccountSelectionCard card = new AccountSelectionCard(acc, selectedAccount -> {
                selectedIbanLbl.setText(selectedAccount.getIban());
                for (AccountSelectionCard c : cardList) c.setSelected(c.getAccount().equals(selectedAccount));
            });
            cardList.add(card);
            accListPanel.add(card);
            accListPanel.add(Box.createVerticalStrut(10));
        }
        accListPanel.revalidate();
        accListPanel.repaint();
    }
    
    private void handleContinue() {
         String src = selectedIbanLbl.getText();
         if(src.isEmpty()) { JOptionPane.showMessageDialog(this, "Select Account First!"); return; }
            
         if(type.equals("WITHDRAW") || type.equals("DEPOSIT")) {
             try {
                 double am = Double.parseDouble(fAmount.getText());
                 if(type.equals("WITHDRAW")) controller.handleWithdraw(src, am,"");
                 else controller.handleDeposit(src, am,"");
                 JOptionPane.showMessageDialog(this, "Success!");
                 refresh(); 
                 onTransactionSuccess.run();
             } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
             return;
         }

         StringBuilder sb = new StringBuilder("<html>");
         sb.append("Source: ").append(src).append("<br>");
         
         if(type.equals("TRANSFER")) {
             sb.append("To: ").append(fTarget.getText()).append("<br>");
             sb.append("Type: ").append(transferTypeBox.getSelectedItem()).append("<br>");
             sb.append("Amount: ").append(fAmount.getText()).append("€");
         } else if (type.equals("PAYMENT")) {
             String rfCode = fTarget.getText();
             try {
                 Bill bill = controller.getBillByRF(rfCode);
                //  if (bill.getBillStatus() == Bill.Status.PAID) {
                //      JOptionPane.showMessageDialog(this, "This bill is already PAID!");
                //      return;
                //  }
                 fAmount.setText(String.valueOf(bill.getAmount()));
                 sb.append("Payment RF: ").append(rfCode).append("<br>");
                 sb.append("Description: ").append(bill.getDescription()).append("<br>");
                 sb.append("<b>Bill Amount: ").append(bill.getAmount()).append("€</b>");
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, ex.getMessage());
                 return;
             }
         }
         sb.append("</html>");
         detailsArea.setText(sb.toString());
         flowLayout.next(flowContainer);
    }
    
    private void updateForm(JPanel p) {
        p.removeAll();
        if (type.equals("TRANSFER")) {
            String m = (String)transferTypeBox.getSelectedItem();
            if(m.equals("INTERNAL")) {
                JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                typeRow.setOpaque(false);
                JLabel typeLabel = StyleHelpers.createLabel("Type:");
                typeLabel.setPreferredSize(new Dimension(140, 30)); 
                transferTypeBox.setPreferredSize(new Dimension(150, 30)); 
                typeRow.add(typeLabel);
                typeRow.add(transferTypeBox);
                p.add(typeRow);
                
                JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                row1.setOpaque(false);
                JLabel lblTarget = StyleHelpers.createLabel("Target IBAN:");
                lblTarget.setPreferredSize(new Dimension(140, 30)); 
                row1.add(lblTarget);
                row1.add(fTarget); 
                p.add(row1);
                
                JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                row2.setOpaque(false);
                JLabel lblAmount = StyleHelpers.createLabel("Amount (€):");
                lblAmount.setPreferredSize(new Dimension(140, 30)); 
                row2.add(lblAmount);
                row2.add(fAmount);
                p.add(row2);
            } else {
                p.add(StyleHelpers.createLabel("Type:")); p.add(transferTypeBox);
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
        } else {
            p.add(StyleHelpers.createLabel("Amount (€):")); p.add(fAmount);
        }
    }

    private void initConfirmView() {
        JPanel confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));
        confirmPanel.setBackground(StyleHelpers.MUSTARD_BG);
        confirmPanel.setBorder(new EmptyBorder(50, 100, 50, 100)); 
        
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
        
        box.add(title); box.add(detailsArea); box.add(confirmBtn);
        box.add(Box.createVerticalStrut(10)); box.add(backBtn);
        
        JPanel centerWrap = new JPanel(new GridBagLayout()); 
        centerWrap.setOpaque(false); centerWrap.add(box);
        confirmPanel.add(centerWrap);
        
        backBtn.addActionListener(e -> flowLayout.previous(flowContainer));
        
        confirmBtn.addActionListener(e -> {
            try {
                String src = selectedIbanLbl.getText();
                double am = Double.parseDouble(fAmount.getText());
                if (type.equals("TRANSFER")) {
                    String mode = (String)transferTypeBox.getSelectedItem();
                    if(mode.equals("INTERNAL")) {
                        controller.handleTransfer(src, fTarget.getText(), am,"");
                    } else {
                        controller.handleExternalTransfer(mode, src, am, fName.getText(), fTarget.getText(), fBic.getText(), fBankName.getText(), fAddress.getText(), fCountry.getText());
                    }
                } else if (type.equals("PAYMENT")) {
                     controller.payBill(fTarget.getText(), src, user.getAfm());
                }
                JOptionPane.showMessageDialog(this, "Transaction Successful!");
                refresh();
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
        lbl.setPreferredSize(new Dimension(160, 30));
        row.add(lbl);
        row.add(field);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return row;
    }
}