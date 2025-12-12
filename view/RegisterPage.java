package view;

import services.BankSystem;
import javax.swing.*;
import java.awt.*;

public class RegisterPage extends JPanel {

    private JTextField userField, firstNameField, lastNameField, afmField, emailField, phoneField;
    private JPasswordField passField;
    private BankBridge navigation;
    private Image backgroundImage;

    public RegisterPage(BankBridge navigation) {
        this.navigation = navigation;

        // 1. ΦΟΡΤΩΣΗ ΕΙΚΟΝΑΣ (ίδια με το Login)
        try {
            backgroundImage = new ImageIcon("services/background.jpg").getImage();
        } catch (Exception e) {
            System.out.println("Background image not found!");
        }

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- ΤΙΤΛΟΣ ---
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        // Σκιά γύρω από τα γράμματα (Hack για να διαβάζεται στο φόντο)
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        // --- ΠΕΔΙΑ ---
        gbc.gridwidth = 1;
        int row = 1;
        
        // Χρησιμοποιούμε τη μέθοδο για τα γκρι χάπια
        add(createRoundedPanel("Name:", firstNameField = new JTextField()), gbc, 0, row++);
        add(createRoundedPanel("Surname:", lastNameField = new JTextField()), gbc, 0, row++);
        add(createRoundedPanel("AFM:", afmField = new JTextField()), gbc, 0, row++);
        add(createRoundedPanel("Email:", emailField = new JTextField()), gbc, 0, row++);
        add(createRoundedPanel("Phone:", phoneField = new JTextField()), gbc, 0, row++);
        add(createRoundedPanel("Username:", userField = new JTextField()), gbc, 0, row++);
        
        passField = new JPasswordField();
        add(createRoundedPanel("Password:", passField), gbc, 0, row++);

        // --- ΚΟΥΜΠΙΑ ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false); // Διάφανο για να φαίνεται η εικόνα

        JButton registerBtn = createButton("Register", new Color(0, 102, 204));
        registerBtn.addActionListener(e -> performRegister());

        JButton backBtn = createButton("Back", new Color(100, 100, 100));
        backBtn.addActionListener(e -> navigation.showLogin());

        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);

        gbc.gridy = row;
        gbc.insets = new Insets(20, 10, 10, 10); // Λίγο αέρα πάνω από τα κουμπιά
        add(btnPanel, gbc);
    }

    // --- ΖΩΓΡΑΦΙΣΜΑ ΦΟΝΤΟΥ ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // --- LOGIC ΕΓΓΡΑΦΗΣ ---
    private void performRegister() {
        try {
            BankSystem.getInstance().getUserManager().registerCustomer(
                userField.getText(),
                passField.getPassword(),
                firstNameField.getText(),
                lastNameField.getText(),
                afmField.getText(),
                emailField.getText(),
                phoneField.getText()
            );
            JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
            navigation.showLogin();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- ΒΟΗΘΗΤΙΚΕΣ ΜΕΘΟΔΟΙ (ΙΔΙΕΣ ΜΕ LOGIN) ---
    private void add(Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        add(comp, gbc);
    }

    private JPanel createRoundedPanel(String labelText, JTextField inputField) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));
        panel.setPreferredSize(new Dimension(380, 45)); // Λίγο πιο φαρδύ

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 50));
        label.setPreferredSize(new Dimension(80, 25)); // Σταθερό πλάτος ετικέτας

        inputField.setOpaque(false);
        inputField.setBorder(null);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setForeground(Color.BLACK);
        inputField.setPreferredSize(new Dimension(250, 25));

        panel.add(label);
        panel.add(inputField);
        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}