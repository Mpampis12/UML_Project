package view;
import javax.swing.*;
import java.awt.*;
import control.BankController;
import model.User;

public class CreateBillPanel extends JPanel {
    public CreateBillPanel(User businessUser) {
        setLayout(new GridBagLayout());
        setBackground(StyleHelpers.MUSTARD_BG);

        JPanel panel = new StyleHelpers.RoundedPanel(20, Color.WHITE);
        panel.setLayout(new GridLayout(5, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JTextField payerAfm = new JTextField();
        JTextField payerName = new JTextField();
        JTextField amount = new JTextField();
        JTextField desc = new JTextField();
        JButton createBtn = StyleHelpers.createRoundedButton("Issue Bill");

        panel.add(new JLabel("Payer AFM:")); panel.add(payerAfm);
        panel.add(new JLabel("Payer Name:")); panel.add(payerName);
        panel.add(new JLabel("Amount:")); panel.add(amount);
        panel.add(new JLabel("Description:")); panel.add(desc);
        panel.add(new JLabel("")); panel.add(createBtn);

        createBtn.addActionListener(e -> {
            // Εδώ θα καλούσατε μια μέθοδο στον Controller για δημιουργία Bill
            // Π.χ. createBill(businessUser, payerAfm.getText(), amount...);
            JOptionPane.showMessageDialog(this, "Bill Created (Simulation)");
        });

        add(panel);
    }
}