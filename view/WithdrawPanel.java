package view;
import javax.swing.*;
import java.awt.*;
import control.BankController;
public class WithdrawPanel extends JPanel{
    
    public WithdrawPanel() {
        setLayout(new GridBagLayout());
        setBackground(StyleHelpers.MUSTARD_BG);
        
        JPanel card = new StyleHelpers.RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridLayout(4, 2, 10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        JTextField ibanField = new JTextField();
        JTextField amountField = new JTextField();
        JButton submitBtn = StyleHelpers.createRoundedButton("Withdraw Cash");

        card.add(new JLabel("Source IBAN:")); card.add(ibanField);
        card.add(new JLabel("Amount (€):")); card.add(amountField);
        card.add(new JLabel("")); card.add(submitBtn);

        submitBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText());
                new BankController().handleWithdraw(ibanField.getText(), amt);
                JOptionPane.showMessageDialog(this, "Withdraw Successful!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        add(card);
    }
}

