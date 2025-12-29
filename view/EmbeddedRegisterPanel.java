package view;
import javax.swing.*;
import java.awt.*;
import control.BankController;

public class EmbeddedRegisterPanel extends JPanel {
    public EmbeddedRegisterPanel(String type) { // type = "PERSONAL" or "BUSINESS"
        setLayout(new GridBagLayout());
        setBackground(StyleHelpers.MUSTARD_BG);

        JPanel form = new StyleHelpers.RoundedPanel(30, Color.WHITE);
        form.setLayout(new GridLayout(8, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("New " + type + " Customer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JTextField userTxt = new JTextField();
        JPasswordField passTxt = new JPasswordField();
        JTextField nameTxt = new JTextField();
        JTextField lastTxt = new JTextField();
        JTextField afmTxt = new JTextField();
        JTextField emailTxt = new JTextField();
        JTextField phoneTxt = new JTextField();
        
        JButton regBtn = StyleHelpers.createRoundedButton("Register User");

        form.add(new JLabel("Username:")); form.add(userTxt);
        form.add(new JLabel("Password:")); form.add(passTxt);
        form.add(new JLabel("First Name:")); form.add(nameTxt);
        form.add(new JLabel("Last Name:")); form.add(lastTxt);
        form.add(new JLabel("AFM:")); form.add(afmTxt);
        form.add(new JLabel("Email:")); form.add(emailTxt);
        form.add(new JLabel("Phone:")); form.add(phoneTxt);
        form.add(new JLabel("")); form.add(regBtn);

        regBtn.addActionListener(e -> {
            try {
                if (type.equals("ADMIN")) {
                    new BankController().createAdmin(
                        userTxt.getText(), passTxt.getPassword(),
                        nameTxt.getText(), lastTxt.getText(),
                        emailTxt.getText()
                    );
                    JOptionPane.showMessageDialog(this, "Admin Created Successfully!");
                    // Clear fields
                    userTxt.setText(""); emailTxt.setText("");
                    return;
                }
                else {
                new BankController().createCustomerByType(
                    userTxt.getText(), passTxt.getPassword(),
                    nameTxt.getText(), lastTxt.getText(),
                    afmTxt.getText(), emailTxt.getText(), phoneTxt.getText(),
                    type
                );
            }
                JOptionPane.showMessageDialog(this, "User Created Successfully!");
                // Clear fields
                userTxt.setText(""); afmTxt.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
            
        });

         add(title );
        
        add(form );
    }
}