package view;

import model.StandingOrder;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class StandingOrderCard extends JPanel {
    private boolean isSelected = false;
    private StandingOrder standingOrder;
    private Consumer<StandingOrder> onSelect;
    private Consumer<StandingOrder> onDelete; // Νέο callback

    private final Color COLOR_NORMAL = StyleHelpers.CARD_COLOR;
    private final Color COLOR_SELECTED = new Color(255, 230, 150);
    private final Color BORDER_SELECTED = new Color(255, 140, 0);

    public StandingOrderCard(StandingOrder order, Consumer<StandingOrder> onSelect, Consumer<StandingOrder> onDelete) {
        this.standingOrder = order;
        this.onSelect = onSelect;
        this.onDelete = onDelete;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(220, 70));
        setMaximumSize(new Dimension(500, 70));
        setBorder(new EmptyBorder(5, 15, 5, 5));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Info Panel
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        
        String targetTxt = (order.getType() == StandingOrder.StandingOrderPurpose.TRANSFER) 
                ? "To: " + order.getTarget().toString() 
                : "Bill: " + order.getTargetRfCode();
                
        JLabel descLbl = new JLabel("<html><b>" + order.getDescription() + "</b></html>");
        JLabel targetLbl = new JLabel(targetTxt);
        targetLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        info.add(descLbl);
        info.add(targetLbl);

        // Right Panel (Amount + Delete)
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        
        JLabel amountLbl = new JLabel(String.format("%.2f €", order.getAmount()));
        amountLbl.setFont(StyleHelpers.FONT_BOLD);
        amountLbl.setForeground(new Color(0, 100, 0));
        amountLbl.setBorder(new EmptyBorder(0,0,0,10));
        
        JButton delBtn = new JButton("X");
        delBtn.setForeground(Color.RED);
        delBtn.setBorderPainted(false);
        delBtn.setContentAreaFilled(false);
        delBtn.setFocusPainted(false);
        delBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        delBtn.addActionListener(e -> onDelete.accept(order));

        right.add(amountLbl, BorderLayout.CENTER);
        right.add(delBtn, BorderLayout.EAST);

        add(info, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSelect.accept(standingOrder);
            }
        });
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }

    public StandingOrder getStandingOrder() { return standingOrder; }

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