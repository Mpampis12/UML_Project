package view;

import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPage extends JPanel {

    private BankBridge navigation;
    private User user;
    
    private JPanel mainContentPanel; 
    private CardLayout cardLayout;   

    public DashboardPage(BankBridge navigation, User user) {
        this.navigation = navigation;
        this.user = user;

        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            Image bg = new ImageIcon("services/background.jpg").getImage();
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 250));
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel welcomeLabel = new JLabel(  user.getFirstName()+ user.getLastName(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 180, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        welcomeLabel.setForeground(Color.BLACK);  
        
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setOpaque(false);
        
        JButton homeBtn = StyleHelpers.createRoundedButton("Home");
        JButton historyBtn = StyleHelpers.createRoundedButton("History");
        JButton transferBtn = StyleHelpers.createRoundedButton("Transfer");
        JButton payBtn = StyleHelpers.createRoundedButton("Payment");
        JButton soBtn = StyleHelpers.createRoundedButton("Standing Orders");
        JButton wdBtn = StyleHelpers.createRoundedButton("Withdraw");
        JButton depBtn = StyleHelpers.createRoundedButton("Deposit");

        navPanel.add(homeBtn); navPanel.add(payBtn); navPanel.add(soBtn); 
        navPanel.add(historyBtn); navPanel.add(transferBtn); navPanel.add(wdBtn); navPanel.add(depBtn);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- CONTENT ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(StyleHelpers.MUSTARD_BG);
        
        add(mainContentPanel, BorderLayout.CENTER);

        // --- ACTIONS ---
        // Κάθε φορά φτιάχνουμε νέο Panel για να έχουμε φρέσκα δεδομένα
        homeBtn.addActionListener(e -> loadPanel("HOME", new HomePanel(user, navigation)));
        historyBtn.addActionListener(e -> loadPanel("HISTORY", new HistoryPanel(user)));
        transferBtn.addActionListener(e -> loadPanel("TRANSFER", new TransferPanel(user, "TRANSFER")));
        payBtn.addActionListener(e -> loadPanel("PAYMENT", new TransferPanel(user, "PAYMENT")));
        wdBtn.addActionListener(e -> loadPanel("WITHDRAW", new TransferPanel(user, "WITHDRAW")));
        depBtn.addActionListener(e -> loadPanel("DEPOSIT", new TransferPanel(user, "DEPOSIT")));
        soBtn.addActionListener(e -> loadPanel("SO", new StandingOrderPanel(user)));

        // Start at Home
        homeBtn.doClick();
    }

    private void loadPanel(String name, JPanel panel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(panel, name);
        cardLayout.show(mainContentPanel, name);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
}