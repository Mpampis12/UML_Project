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
import java.util.ArrayList; 
import java.util.List;
import java.util.stream.IntStream;

public class StandingOrderPanel extends JPanel {
    
    private User user;
    private BankController controller;
    private JPanel listPanel;
    private List<StandingOrderCard> cardList;
    
    // Form Components
    private JTextField fTarget;
    private JTextField fAmount;
    private JTextField fDetails;
    private JComboBox<Integer> dayBox;
    private JComboBox<String> accBox;
    private JButton saveBtn;
    private JButton clearBtn;

    private StandingOrder currentEditingOrder = null;

    public StandingOrderPanel(User user) {
        this.user = user;
        this.controller = new BankController();
        this.cardList = new ArrayList<>();
        
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(StyleHelpers.MUSTARD_BG);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // LEFT COLUMN
        JPanel left = new JPanel(new BorderLayout()); 
        left.setOpaque(false);
        JLabel title = new JLabel("My Orders"); 
        title.setFont(StyleHelpers.FONT_TITLE);
        left.add(title, BorderLayout.NORTH);

        listPanel = new JPanel(); 
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); 
        listPanel.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getViewport().setOpaque(false); scroll.setOpaque(false); scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new StyleHelpers.MyScrollBarUI());
        left.add(scroll, BorderLayout.CENTER);

        // RIGHT COLUMN (ΑΛΛΑΓΗ σε GridLayout)
        RoundedPanel formBox = new RoundedPanel(30, StyleHelpers.BOX_COLOR);
        formBox.setBorder(new EmptyBorder(20, 20, 20, 20));
        // 0 γραμμές, 2 στήλες, 10px κενό
        formBox.setLayout(new GridLayout(0, 2, 10, 20)); 
        
        accBox = new JComboBox<>();
        List<Account> accounts = controller.getAccountsForUser(user);
        for(Account a : accounts) accBox.addItem(a.getIban());
        accBox.setBackground(Color.WHITE);

        fTarget = new RoundedTextField(15);
        fAmount = new RoundedTextField(15);
        fDetails = new RoundedTextField(15);
        
        Integer[] days = IntStream.rangeClosed(1, 30).boxed().toArray(Integer[]::new);
        dayBox = new JComboBox<>(days);
        dayBox.setBackground(Color.WHITE);

        saveBtn = StyleHelpers.createRoundedButton("Create New");
        clearBtn = StyleHelpers.createRoundedButton("Clear Form");
        clearBtn.addActionListener(e -> clearForm());

         formBox.add(StyleHelpers.createLabel("From Account:")); formBox.add(accBox);
        formBox.add(StyleHelpers.createLabel("Target (IBAN/RF):")); formBox.add(fTarget);
        formBox.add(StyleHelpers.createLabel("Amount (€):")); formBox.add(fAmount);
        formBox.add(StyleHelpers.createLabel("Execution Day:")); formBox.add(dayBox);
        formBox.add(StyleHelpers.createLabel("Details (Opt):")); formBox.add(fDetails);
        
        
         JPanel mainRight = new JPanel(new BorderLayout(0, 20));
        mainRight.setOpaque(false);
        
        // Το Grid Panel για τα πεδία
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 20));
        fieldsPanel.setOpaque(false);
        fieldsPanel.add(StyleHelpers.createLabel("From Account:")); fieldsPanel.add(accBox);
        fieldsPanel.add(StyleHelpers.createLabel("Target (IBAN/RF):")); fieldsPanel.add(fTarget);
        fieldsPanel.add(StyleHelpers.createLabel("Amount (€):")); fieldsPanel.add(fAmount);
        fieldsPanel.add(StyleHelpers.createLabel("Execution Day:")); fieldsPanel.add(dayBox);
        fieldsPanel.add(StyleHelpers.createLabel("Details (Opt):")); fieldsPanel.add(fDetails);

        // Το Panel για τα κουμπιά
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(saveBtn); 
        btnPanel.add(clearBtn);

        formBox.setLayout(new BorderLayout()); // Reset layout του RoundedPanel
        formBox.add(fieldsPanel, BorderLayout.CENTER);
        formBox.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> handleSave());

        // Wrapper για να μην πιάνει όλο το ύψος
        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(formBox, BorderLayout.NORTH); // Βάζουμε το formBox πάνω
        
        add(left); add(rightWrap);
        refreshList();
    }
    
    // ... (Οι μέθοδοι handleSave, clearForm, refreshList παραμένουν ΙΔΙΕΣ με πριν) ...
    // Αντίγραψε τες από την προηγούμενη απάντηση μου για να είναι πλήρες το αρχείο.
    private void handleSave() {
        try {
            if (accBox.getSelectedItem() == null) throw new Exception("Select Source Account");
            String target = fTarget.getText().trim();
            if (target.isEmpty()) throw new Exception("Target is empty");
            double amount = Double.parseDouble(fAmount.getText());
            int day = (int) dayBox.getSelectedItem();
            String details = fDetails.getText();
            if(details.isEmpty()) details = "Standing Order";

            StandingOrder.StandingOrderPurpose type;
            if (target.startsWith("RF") || target.length() < 15) { 
                type = StandingOrder.StandingOrderPurpose.BILL;
            } else {
                type = StandingOrder.StandingOrderPurpose.TRANSFER;
            }

            if (currentEditingOrder == null) {
                StandingOrder so = new StandingOrder(
                    new Iban((String)accBox.getSelectedItem()), 
                    target, amount, details, day, type
                );
                controller.createStandingOrder(so);
                JOptionPane.showMessageDialog(this, "Order Created!");
            } else {
                currentEditingOrder.setSource(new Iban((String)accBox.getSelectedItem()));
                currentEditingOrder.setType(type);
                currentEditingOrder.updateDetails(target, amount, details, day);
                controller.saveData(); 
                JOptionPane.showMessageDialog(this, "Order Updated!");
            }
            
            clearForm();
            refreshList();

        } catch(Exception ex) { 
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); 
        }
    }

    private void clearForm() {
        fTarget.setText("");
        fAmount.setText("");
        fDetails.setText("");
        dayBox.setSelectedIndex(0);
        saveBtn.setText("Create New");
        currentEditingOrder = null;
        for(StandingOrderCard c : cardList) c.setSelected(false);
    }

    private void refreshList() {
        listPanel.removeAll(); 
        cardList.clear();      
        java.util.List<StandingOrder> orders = controller.getStandingOrdersForUser(user);

        for(StandingOrder so : orders) {
            StandingOrderCard card = new StandingOrderCard(so, 
                selectedOrder -> {
                    currentEditingOrder = selectedOrder;
                    saveBtn.setText("Update Order");
                    if(selectedOrder.getType() == StandingOrder.StandingOrderPurpose.TRANSFER)
                        fTarget.setText(selectedOrder.getTarget().toString());
                    else 
                        fTarget.setText(selectedOrder.getTargetRfCode());
                    fAmount.setText(String.valueOf(selectedOrder.getAmount()));
                    fDetails.setText(selectedOrder.getDescription());
                    dayBox.setSelectedItem(selectedOrder.getDayOfMonth());
                    accBox.setSelectedItem(selectedOrder.getSource().toString());
                    for (StandingOrderCard c : cardList) c.setSelected(c.getStandingOrder().equals(selectedOrder));
                },
                orderToDelete -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete this order?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if(confirm == JOptionPane.YES_OPTION) {
                        controller.deleteStandingOrder(orderToDelete);
                        clearForm();
                        refreshList();
                    }
                }
            );
            cardList.add(card);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
        }
        listPanel.revalidate(); listPanel.repaint();
    }
}