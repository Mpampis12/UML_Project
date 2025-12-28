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

    // Χρώματα
    private final Color COLOR_NORMAL = StyleHelpers.CARD_COLOR;
    private final Color COLOR_SELECTED = new Color(255, 230, 150);
    private final Color BORDER_SELECTED = new Color(255, 140, 0);

    public StandingOrderCard(StandingOrder order, Consumer<StandingOrder> onSelect) {
        this.standingOrder = order;
        this.onSelect = onSelect;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(200, 60)); // Λίγο πιο μικρό ύψος από τους λογαριασμούς
        setMaximumSize(new Dimension(500, 60));
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Περιεχόμενο Κάρτας
        JLabel descLbl = new JLabel("<html><b>" + order.getDescription() + "</b></html>");
        descLbl.setFont(StyleHelpers.FONT_PLAIN);
        
        JLabel amountLbl = new JLabel(String.format("%.2f €", order.getAmount()));
        amountLbl.setFont(StyleHelpers.FONT_BOLD);
        amountLbl.setForeground(new Color(0, 100, 0));

        add(descLbl, BorderLayout.CENTER);
        add(amountLbl, BorderLayout.EAST);

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