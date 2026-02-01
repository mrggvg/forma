package dev.madlador.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Cell extends JButton {

    private int arcSize = 16;

    public Cell() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBackground(new Color(0x262626));
        setMinimumSize(new Dimension(60, 60));
        setPreferredSize(new Dimension(60, 60));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setArcSize(int arcSize) {
        this.arcSize = arcSize;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Save original clip before modifying
        Shape originalClip = g2.getClip();

        RoundRectangle2D roundRect = new RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(), arcSize, arcSize
        );

        g2.setClip(roundRect);

        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);

        // Restore clip so paintBorder and anything else isn't affected
        g2.setClip(originalClip);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(60, 60, 80));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcSize, arcSize);
    }
}