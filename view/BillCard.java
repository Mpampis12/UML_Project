package view;

import model.Bill;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class BillCard extends JPanel {
    private boolean isSelected = false;
    private Bill bill;
    private Consumer<Bill> onSelect;
    private Consumer<Bill> onDelete;

    // Χρώματα ίδια ακριβώς με το StandingOrderCard
    private final Color COLOR_NORMAL = StyleHelpers.CARD_COLOR;
    private final Color COLOR_SELECTED = new Color(255, 230, 150);
    private final Color BORDER_SELECTED = new Color(255, 140, 0);

    public BillCard(Bill bill, Consumer<Bill> onSelect, Consumer<Bill> onDelete) {
        this.bill = bill;
        this.onSelect = onSelect;
        this.onDelete = onDelete;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(220, 70));
        setMaximumSize(new Dimension(500, 70));
        setBorder(new EmptyBorder(5, 15, 5, 5));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Info Panel (Center)
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        
        // Description (ή κενό αν δεν υπάρχει)
        String descText = (bill.getDescription() != null && !bill.getDescription().isEmpty()) 
                        ? bill.getDescription() 
                        : "Bill Payment";

        // RF Code & Status στο κάτω μέρος
        String subText = "RF: " + bill.getRfCode() + " (" + bill.getBillStatus() + ")";
                
        JLabel descLbl = new JLabel("<html><b>" + descText + "</b></html>");
        JLabel rfLbl = new JLabel(subText);
        rfLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        // Αν είναι πληρωμένο, ας το δείχνουμε με πράσινο κείμενο στο status
        if(bill.getBillStatus() == Bill.Status.PAID) {
            rfLbl.setForeground(new Color(0, 100, 0));
        } else {
            rfLbl.setForeground(Color.DARK_GRAY);
        }
        
        info.add(descLbl);
        info.add(rfLbl);

        // Right Panel (Amount + Delete)
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        
        JLabel amountLbl = new JLabel(String.format("%.2f €", bill.getAmount()));
        amountLbl.setFont(StyleHelpers.FONT_BOLD);
        amountLbl.setForeground(new Color(0, 100, 0));
        amountLbl.setBorder(new EmptyBorder(0,0,0,10));
        
        JButton delBtn = new JButton("X");
        delBtn.setForeground(Color.RED);
        delBtn.setBorderPainted(false);
        delBtn.setContentAreaFilled(false);
        delBtn.setFocusPainted(false);
        delBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        delBtn.addActionListener(e -> onDelete.accept(bill));

        right.add(amountLbl, BorderLayout.CENTER);
        right.add(delBtn, BorderLayout.EAST);

        add(info, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSelect.accept(bill);
            }
        });
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }

    public Bill getBill() { return bill; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected) g2.setColor(COLOR_SELECTED);
        else g2.setColor(COLOR_NORMAL);

        int arc = 20;
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        if (isSelected) {
            g2.setColor(BORDER_SELECTED);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}