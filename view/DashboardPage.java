package view;

import model.User;
import services.BankSystem;
import view.StyleHelpers.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseMotionAdapter;

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

        
        JPanel welcomeWraper = new JPanel(new FlowLayout(FlowLayout.LEFT,0,10));
        welcomeWraper.setOpaque(false);

        StyleHelpers.RoundedPanel box = new StyleHelpers.RoundedPanel(30, StyleHelpers.BUTTON_YELLOW);
        box.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
        
        
        JButton welcomeButton = StyleHelpers.createRoundedButton(user.getFirstName()+" "+user.getLastName());
        JPopupMenu exitMenu = new JPopupMenu("Popup");
        JMenuItem exItem = new JMenuItem("Log Out"){
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            };};

        exItem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exItem.setBackground(new Color(255, 180, 0));
        exItem.setFocusPainted(false);
        exItem.setContentAreaFilled(false);
        exItem.setBorderPainted(false);
        exItem.setBorder(new EmptyBorder(8, 20, 8, 20));
        exItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
         
        exitMenu.add(exItem);

        box.add(welcomeButton);
        welcomeWraper.add(box);
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



        headerPanel.add(welcomeWraper, BorderLayout.WEST);
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
        welcomeButton.addActionListener(e->showExit(exitMenu,mainContentPanel));
        exItem.addActionListener(e->navigation.showLogin());
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
    private void showExit(JPopupMenu menu,JPanel panel){
        Point cursorPos = MouseInfo.getPointerInfo().getLocation();
         SwingUtilities.convertPointFromScreen(cursorPos, panel);
        menu.show(panel, cursorPos.x, cursorPos.y);
        menu.setVisible(true);       
    }

}