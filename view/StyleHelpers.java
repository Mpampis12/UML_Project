package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class StyleHelpers {

    public static final Color MUSTARD_BG = new Color(228, 196, 101);
    public static final Color BUTTON_YELLOW = new Color(255, 180, 0);
    public static final Color CARD_COLOR = new Color(160, 140, 90, 200);
    public static final Color BOX_COLOR = new Color(255, 255, 255, 180);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    // --- FACTORY METHODS ---

    public static JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BOLD);
        l.setForeground(new Color(40, 40, 40));
        return l;
    }

    public static JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setBackground(BUTTON_YELLOW);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- CUSTOM COMPONENTS ---

    public static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int r, Color c) { radius=r; bgColor=c; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,radius,radius);
        }
    }

    public static class RoundedTextField extends JTextField {
        public RoundedTextField(int cols) {
            super(cols); setOpaque(false);
            setBorder(new EmptyBorder(5,10,5,10));
            setPreferredSize(new Dimension(250, 40));
            setFont(FONT_PLAIN);
        }
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
            super.paintComponent(g);
        }
        protected void paintBorder(Graphics g) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
        }
    }
    
    public static class MyScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() { this.thumbColor = BUTTON_YELLOW; }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() { JButton j = new JButton(); j.setPreferredSize(new Dimension(0, 0)); return j; }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) { if(!scrollbar.isEnabled()) return; Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(thumbColor); g2.fillRoundRect(r.x+4,r.y+2,r.width-8,r.height-4,10,10); }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
    }
}