package view;
import javax.swing.*;
import java.awt.*;
import control.BankController;

public class DepositPanel extends JPanel {
    public DepositPanel() {
        setLayout(new GridBagLayout());
        setBackground(StyleHelpers.MUSTARD_BG);
        
        JPanel card = new StyleHelpers.RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridLayout(4, 2, 10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        JTextField ibanField = new JTextField();
        JTextField amountField = new JTextField();
        JButton submitBtn = StyleHelpers.createRoundedButton("Deposit Cash");

        card.add(new JLabel("Target IBAN:")); card.add(ibanField);
        card.add(new JLabel("Amount (€):")); card.add(amountField);
        card.add(new JLabel("")); card.add(submitBtn);

        submitBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText());
                new BankController().handleDeposit(ibanField.getText(), amt);
                JOptionPane.showMessageDialog(this, "Deposit Successful!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        add(card);
    }
}