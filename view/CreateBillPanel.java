package view;

import model.Bill;
import model.User;
import model.Account;
import control.BankController;
import view.StyleHelpers.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CreateBillPanel extends JPanel {

    private User user;
    private BankController controller;
    private JPanel listPanel;
    private List<BillCard> cardList;
    private Bill currentEditingBill = null;

    // Form Components
    private JComboBox<String> ibanSelector;
    private JTextField fPayerAfm;
    private JTextField fAmount;
    private JTextField fDesc;
    private JTextField fExpireDate;
    private JButton saveBtn;
    private JButton clearBtn;

    public CreateBillPanel(User user) {
        this.user = user;
        this.controller = new BankController();
        this.cardList = new ArrayList<>();

        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(StyleHelpers.MUSTARD_BG);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // === LEFT COLUMN: Bill List ===
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        
        JLabel title = new JLabel("My Issued Bills");
        title.setFont(StyleHelpers.FONT_TITLE);
        left.add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());
        left.add(scroll, BorderLayout.CENTER);


        // === RIGHT COLUMN: Form ===
        RoundedPanel formBox = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        formBox.setBorder(new EmptyBorder(20, 20, 20, 20));
        formBox.setLayout(new BorderLayout(0, 20));

        // Form Fields Container
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 20));
        fieldsPanel.setOpaque(false);

        // Components Init
        ibanSelector = new JComboBox<>();
        List<Account> accounts = controller.getAccountsForUser(user);
        for(Account a : accounts) {
            if(a.getAccountType() == Account.AccountType.BUSINESS) 
                ibanSelector.addItem(a.getIban());
        }
        ibanSelector.setBackground(Color.WHITE);

        fPayerAfm = new RoundedTextField(15);
        fAmount = new RoundedTextField(15);
        fDesc = new RoundedTextField(15);
        fExpireDate = new RoundedTextField(15);
        fExpireDate.setText("dd/MM/yyyy");

        // Adding to Grid
        fieldsPanel.add(StyleHelpers.createLabel("Credit Account:")); fieldsPanel.add(ibanSelector);
        fieldsPanel.add(StyleHelpers.createLabel("Payer AFM:")); fieldsPanel.add(fPayerAfm);
        fieldsPanel.add(StyleHelpers.createLabel("Amount (€):")); fieldsPanel.add(fAmount);
        fieldsPanel.add(StyleHelpers.createLabel("Description:")); fieldsPanel.add(fDesc);
        fieldsPanel.add(StyleHelpers.createLabel("Expire Date:")); fieldsPanel.add(fExpireDate);

        // Buttons
        saveBtn = StyleHelpers.createRoundedButton("Issue Bill");
        clearBtn = StyleHelpers.createRoundedButton("Clear / New");
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(saveBtn);
        btnPanel.add(clearBtn);

        // Listeners
        saveBtn.addActionListener(e -> handleSave());
        clearBtn.addActionListener(e -> clearForm());

        formBox.add(fieldsPanel, BorderLayout.CENTER);
        formBox.add(btnPanel, BorderLayout.SOUTH);
        
        // Wrapper for Right side alignment
        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(formBox, BorderLayout.NORTH);

        add(left);
        add(rightWrap);

        refreshList();
    }

    private void handleSave() {
        try {
            if (ibanSelector.getSelectedItem() == null) throw new Exception("No Business Account Found.");
            String targetIban = (String) ibanSelector.getSelectedItem();
            String payer = fPayerAfm.getText();
            String desc = fDesc.getText();
            String dateStr = fExpireDate.getText();
            
            if (fAmount.getText().isEmpty()) throw new Exception("Enter Amount");
            double amount = Double.parseDouble(fAmount.getText());

            if (currentEditingBill == null) {
                // CREATE NEW
                String newRf = controller.createBill(targetIban, user.getAfm(), amount, desc, payer, dateStr);
                JOptionPane.showMessageDialog(this, "Bill Created! RF: " + newRf);
            } else {
                // UPDATE EXISTING
                controller.updateBill(currentEditingBill.getRfCode(), targetIban, amount, desc, payer, dateStr);
                JOptionPane.showMessageDialog(this, "Bill Updated!");
            }
            
            clearForm();
            refreshList();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        fPayerAfm.setText("");
        fAmount.setText("");
        fDesc.setText("");
        fExpireDate.setText("dd/MM/yyyy");
        saveBtn.setText("Issue Bill");
        currentEditingBill = null;
        for(BillCard c : cardList) c.setSelected(false);
    }

    private void refreshList() {
        listPanel.removeAll();
        cardList.clear();
        
        List<Bill> myBills = controller.getBillsByBusiness(user.getAfm());

        for (Bill b : myBills) {
            BillCard card = new BillCard(b, 
                selected -> {
                    // ON SELECT
                    currentEditingBill = selected;
                    saveBtn.setText("Update Bill");
                    
                    if (selected.getBillStatus() == Bill.Status.PAID) {
                        saveBtn.setEnabled(false); // Cannot edit Paid bills
                        saveBtn.setText("Paid (Locked)");
                    } else {
                        saveBtn.setEnabled(true);
                    }

                    // Fill Form
                    ibanSelector.setSelectedItem(selected.getTargetIban());
                    fPayerAfm.setText(selected.getPayerAfm());
                    fAmount.setText(String.valueOf(selected.getAmount()));
                    fDesc.setText(selected.getDescription());
                    fExpireDate.setText(selected.getExpireDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    // Highlight logic
                    for (BillCard c : cardList) c.setSelected(c.getBill().equals(selected));
                },
                deleted -> {
                    // ON DELETE
                    if (deleted.getBillStatus() == Bill.Status.PAID) {
                        JOptionPane.showMessageDialog(this, "Cannot delete a PAID bill for audit reasons.");
                        return;
                    }
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete Bill " + deleted.getRfCode() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.deleteBill(deleted.getRfCode());
                        clearForm();
                        refreshList();
                    }
                }
            );
            
            cardList.add(card);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
        }
        
        listPanel.revalidate();
        listPanel.repaint();
    }
}