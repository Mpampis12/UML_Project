package view;

import services.BankSystem;
import model.User;
import javax.swing.*;
import java.awt.*;

public class LoginPage extends JPanel {
    
    private JTextField userField;
    private JPasswordField passField;
    private BankBridge navigation;

    public LoginPage(BankBridge navigation) {
        this.navigation = navigation;
        
        setLayout(new GridBagLayout());
        setOpaque(false);  
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
       
        JLabel title = new JLabel("Bank of TUC", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.orange); 
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        gbc.gridx = 0; gbc.gridy = 0; 
        add(title, gbc);

         userField = new JTextField(15);
         JPanel userPanel = createRoundedPanel("Username:", userField);
        
        gbc.gridy = 1;
        add(userPanel, gbc);
        
         passField = new JPasswordField(15);
         JPanel passPanel = createRoundedPanel("Password:", passField);
        
        gbc.gridy = 2;
        add(passPanel, gbc);

         JButton loginBtn = new JButton("LOGIN");
        loginBtn.setPreferredSize(new Dimension(150, 40));
        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.orange);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel registerLink = new JLabel("Don't have an account? Click here to Register");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerLink.setForeground(Color.WHITE);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
         registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigation.showRegister();
            }
        });

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 10, 10); 
        add(registerLink, gbc);
        
        gbc.gridy = 3;
        add(loginBtn, gbc);
        
         loginBtn.addActionListener(e -> performLogin());
    }
    
  
    private JPanel createRoundedPanel(String labelText, JTextField inputField) {
         JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                 g2.setColor(new Color(230, 230, 230)); 
                
                 g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        
         panel.setOpaque(false);  
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));  
        panel.setPreferredSize(new Dimension(350, 50));  

         JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 50));  

         
        inputField.setOpaque(false);  
        inputField.setBorder(null);   
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setForeground(Color.BLACK);

         panel.add(label);
        panel.add(inputField);

        return panel;
    }

    private void performLogin() {
        String username = userField.getText();
        char[] password = passField.getPassword();
        User user = BankSystem.getInstance().getUserManager().login(username, password);
        
        if (user != null) {
            userField.setText("");
            passField.setText("");
            navigation.showDashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }
}