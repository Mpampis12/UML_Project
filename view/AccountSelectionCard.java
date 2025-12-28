package view;

import model.Account;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class AccountSelectionCard extends JPanel {
    private boolean isSelected = false;
    private Account account;
    private Consumer<Account> onSelect;

    // Χρώματα για τις καταστάσεις
    private final Color COLOR_NORMAL = StyleHelpers.CARD_COLOR;
    private final Color COLOR_SELECTED = new Color(255, 230, 150); // Το χρώμα επιλογής που είχες
    private final Color BORDER_SELECTED = new Color(255, 140, 0);  // Πορτοκαλί περίγραμμα

    public AccountSelectionCard(Account account, Consumer<Account> onSelect) {
        this.account = account;
        this.onSelect = onSelect;

        // Ρυθμίσεις Panel για Rounded εμφάνιση
        setLayout(new BorderLayout());
        setOpaque(false); // Σημαντικό για να φαίνονται οι γωνίες
        setPreferredSize(new Dimension(200, 80));
        setMaximumSize(new Dimension(500, 80));
        setBorder(new EmptyBorder(10, 15, 10, 15)); // Padding εσωτερικά
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Περιεχόμενα (όπως ήταν στο TransferPanel)
        JLabel typeLbl = new JLabel("<html><b>" + account.getAccountType() + "</b><br>" + account.getIban() + "</html>");
        typeLbl.setFont(StyleHelpers.FONT_PLAIN);
        
        JLabel balLbl = new JLabel(String.format("%.2f €", account.getBalance()));
        balLbl.setFont(StyleHelpers.FONT_BOLD);
        balLbl.setForeground(new Color(0, 100, 0));

        add(typeLbl, BorderLayout.CENTER);
        add(balLbl, BorderLayout.EAST);

        // Click Listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSelect.accept(account);
            }
        });
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint(); // Ξαναζωγραφίζει το panel με το νέο χρώμα
    }

    public Account getAccount() { return account; }

    @Override
    protected void paintComponent(Graphics g) {
        // Custom ζωγραφική για στρογγυλεμένες γωνίες
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Επιλογή χρώματος φόντου
        if (isSelected) {
            g2.setColor(COLOR_SELECTED);
        } else {
            g2.setColor(COLOR_NORMAL);
        }

        // Ζωγραφίζουμε το στρογγυλεμένο παραλληλόγραμμο
        int arc = 20; // Η ακτίνα της γωνίας (radius)
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        // Αν είναι επιλεγμένο, ζωγραφίζουμε και ένα περίγραμμα
        if (isSelected) {
            g2.setColor(BORDER_SELECTED);
            g2.setStroke(new BasicStroke(2)); // Πάχος γραμμής
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}