package view;

import model.User;
import model.StandingOrder;
import control.BankController; // MVC: Μόνο Controller
import view.StyleHelpers.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class StandingOrderPanel extends JPanel {

    private User user;
    private BankController controller;
    private JPanel listPanel;
    private StandingOrder currentEditingOrder = null;

    // Form Components
    private JComboBox<String> accBox;
    private JComboBox<Integer> dayBox;
    private JTextField fTarget;
    private JTextField fAmount;
    private JTextField fDetails;
    private JTextField fExpireDate; // Πεδίο Ημερομηνίας Λήξης
    private JButton saveBtn;
    private JButton clearBtn;

    public StandingOrderPanel(User user) {
        this.user = user;
        this.controller = BankController.getInstance();

        setLayout(new GridLayout(1, 2, 20, 0)); // Split View
        setBackground(StyleHelpers.MUSTARD_BG);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // === LEFT COLUMN: List ===
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        
        JLabel title = new JLabel("My Standing Orders");
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

        // Form Fields
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        fieldsPanel.setOpaque(false);

        accBox = new JComboBox<>();
        controller.getAccountsForUser(user).forEach(acc -> accBox.addItem(acc.getIban()));
        accBox.setBackground(Color.WHITE);

        dayBox = new JComboBox<>();
        for(int i=1; i<=28; i++) dayBox.addItem(i); // 1-28 για ασφάλεια
        dayBox.setBackground(Color.WHITE);

        fTarget = new RoundedTextField(15);
        fAmount = new RoundedTextField(15);
        fDetails = new RoundedTextField(15);
        
        fExpireDate = new RoundedTextField(15);
        fExpireDate.setText("dd/MM/yyyy"); // Placeholder

        fieldsPanel.add(StyleHelpers.createLabel("From Account:")); fieldsPanel.add(accBox);
        fieldsPanel.add(StyleHelpers.createLabel("Target (IBAN/RF):")); fieldsPanel.add(fTarget);
        fieldsPanel.add(StyleHelpers.createLabel("Amount (€):")); fieldsPanel.add(fAmount);
        fieldsPanel.add(StyleHelpers.createLabel("Execution Day:")); fieldsPanel.add(dayBox);
        fieldsPanel.add(StyleHelpers.createLabel("Expires On:")); fieldsPanel.add(fExpireDate);
        fieldsPanel.add(StyleHelpers.createLabel("Details (Opt):")); fieldsPanel.add(fDetails);

        saveBtn = StyleHelpers.createRoundedButton("Create Order");
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
        
        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(formBox, BorderLayout.NORTH);

        add(left);
        add(rightWrap);

        refreshList();
    }

    private void handleSave() {
        try {
            if (accBox.getSelectedItem() == null) throw new Exception("Select Source Account");
            String source = (String) accBox.getSelectedItem();
            String target = fTarget.getText().trim();
            if (target.isEmpty()) throw new Exception("Target is empty");
            
            if (fAmount.getText().isEmpty()) throw new Exception("Enter Amount");
            double amount = Double.parseDouble(fAmount.getText());
            
            int day = (int) dayBox.getSelectedItem();
            String details = fDetails.getText();
            if(details.isEmpty()) details = "Standing Order";

            // --- Parsing Ημερομηνίας Λήξης ---
            String dateStr = fExpireDate.getText().trim();
            LocalDateTime expireDate;
            try {
                java.time.LocalDate ld = java.time.LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                expireDate = ld.atTime(23, 59, 59);
                
                // Πρόχειρος έλεγχος (αν και ο Controller κάνει validation, καλό είναι και εδώ για UX)
                if (expireDate.isBefore(java.time.LocalDateTime.now())) {
                    throw new Exception("Expiration date must be in the future.");
                }
            } catch (Exception e) {
                throw new Exception("Invalid Date. Format: dd/MM/yyyy");
            }

            if (currentEditingOrder == null) {
                // CREATE NEW
                // ΔΙΟΡΘΩΣΗ: Καλούμε τη νέα μέθοδο χωρίς τον τύπο (Purpose)
                controller.createStandingOrder(source, target, amount, details, day, expireDate);
                JOptionPane.showMessageDialog(this, "Order Created Successfully!");
            } else {
                // UPDATE EXISTING (Διαγράφουμε την παλιά και φτιάχνουμε νέα για απλότητα ή φτιάχνουμε update μέθοδο)
                // Εδώ απλά θα ενημερώσουμε ότι δεν υποστηρίζεται πλήρως το edit στον Controller ακόμα
                // ή θα κάνουμε delete & create.
                
                // Για τώρα, ας κάνουμε delete την παλιά και create τη νέα (safe approach)
                controller.deleteStandingOrder(currentEditingOrder);
                controller.createStandingOrder(source, target, amount, details, day, expireDate);
                
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
        fExpireDate.setText("dd/MM/yyyy");
        saveBtn.setText("Create Order");
        currentEditingOrder = null;
        refreshList(); // Για να ξε-επιλεγεί τυχόν κάρτα
    }
    
    private void refreshList() {
        listPanel.removeAll();
        java.util.List<StandingOrder> myOrders = controller.getStandingOrdersForUser(user);

        for (StandingOrder so : myOrders) {
            StandingOrderCard card = new StandingOrderCard(so, 
                selected -> {
                    // ON SELECT
                    currentEditingOrder = selected;
                    saveBtn.setText("Update Order");
                    
                    accBox.setSelectedItem(selected.getSource().toString());
                    
                    if (selected.getType() == StandingOrder.StandingOrderPurpose.BILL) {
                        fTarget.setText(selected.getTargetRfCode());
                    } else {
                        fTarget.setText(selected.getTarget().toString());
                    }
                    
                    fAmount.setText(String.valueOf(selected.getAmount()));
                    fDetails.setText(selected.getDescription());
                    dayBox.setSelectedItem(selected.getDayOfMonth());
                    
                    if (selected.getExpiredDay() != null) {
                        fExpireDate.setText(selected.getExpiredDay().toString());
                    }
                    
                    // Repaint list to show selection (αν είχαμε logic στο Card για highlight)
                },
                deleted -> {
                    // ON DELETE
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete this Order?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.deleteStandingOrder(deleted);
                        clearForm();
                        refreshList();
                    }
                }
            );
            
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
        }
        
        listPanel.revalidate();
        listPanel.repaint();
    }
}