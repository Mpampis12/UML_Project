package view;

import services.BankSystem;
import control.BankController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import view.StyleHelpers.*;

public class RegisterPage extends JPanel {

    private BankBridge navigation;
    private BankController controller;

    public RegisterPage(BankBridge navigation) {
        this.navigation = navigation;
        this.controller = new BankController();

        setLayout(new BorderLayout());

        JPanel bgPanel = new JPanel(new GridBagLayout()) { 
            Image bg = new ImageIcon("services/background.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(bgPanel, BorderLayout.CENTER);

        RoundedPanel regBox = new RoundedPanel(40, new Color(255, 255, 255, 230));
        regBox.setLayout(new BorderLayout(0, 20)); // BorderLayout για δομή
        regBox.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Title
        JLabel title = new JLabel("Create Account");
        title.setFont(StyleHelpers.FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        regBox.add(title, BorderLayout.NORTH);

        // Form Panel (ΑΛΛΑΓΗ ΣΕ GridLayout)
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // 2 στήλες, κενά ανάμεσα
        formPanel.setOpaque(false);

        JTextField fUser = new RoundedTextField(15);
        JTextField fPass = new RoundedTextField(15);
        
        JTextField fName = new RoundedTextField(15);
        JTextField fLast = new RoundedTextField(15);
        JTextField fAfm = new RoundedTextField(15);
        JTextField fEmail = new RoundedTextField(15);
        JTextField fPhone = new RoundedTextField(15);

        formPanel.add(StyleHelpers.createLabel("Username:")); formPanel.add(fUser);
        formPanel.add(StyleHelpers.createLabel("Password:")); formPanel.add(fPass);
        formPanel.add(StyleHelpers.createLabel("First Name:")); formPanel.add(fName);
        formPanel.add(StyleHelpers.createLabel("Last Name:")); formPanel.add(fLast);
        formPanel.add(StyleHelpers.createLabel("AFM:")); formPanel.add(fAfm);
        formPanel.add(StyleHelpers.createLabel("Email:")); formPanel.add(fEmail);
        formPanel.add(StyleHelpers.createLabel("Phone:")); formPanel.add(fPhone);

        regBox.add(formPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        
        JButton submitBtn = StyleHelpers.createRoundedButton("Register");
        JButton backBtn =  StyleHelpers.createRoundedButton("Back to Login");

        btnPanel.add(submitBtn);
        btnPanel.add(backBtn);

        regBox.add(btnPanel, BorderLayout.SOUTH);
        bgPanel.add(regBox);

        // Logic
        submitBtn.addActionListener(e -> {
            try {
                controller.registerUser(
                    fUser.getText(), fPass.getText().toCharArray(),
                    fName.getText(), fLast.getText(),
                    fAfm.getText(), fEmail.getText(), fPhone.getText()
                );
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                navigation.showLogin();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> navigation.showLogin());
    }
}