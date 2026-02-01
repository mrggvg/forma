package dev.madlador.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Cell extends JButton {

    public enum State { EMPTY, FIRST, SECOND, FIRST_LAST, SECOND_LAST }

    private static final int ARC = 14;
    private static final Color COLOR_EMPTY    = new Color(0x1E1E1E);
    private static final Color COLOR_BORDER   = new Color(0x2A2A2A);
    private static final Color COLOR_HOVER    = new Color(0x2E2E2E);
    private static final Color COLOR_FIRST    = new Color(0x8F2780);
    private static final Color COLOR_FIRST_LIGHT  = new Color(0xB84CA0);
    private static final Color COLOR_SECOND   = new Color(0x27518F);
    private static final Color COLOR_SECOND_LIGHT = new Color(0x4A7BBF);

    private State state = State.EMPTY;

    public Cell() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setMinimumSize(new Dimension(58, 58));
        setPreferredSize(new Dimension(58, 58));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setState(State state) {
        this.state = state;
        repaint();
    }

    public State getState() { return state; }

    private Color resolveBackground() {
        if (getModel().isRollover() && state == State.EMPTY) return COLOR_HOVER;
        return switch (state) {
            case EMPTY       -> COLOR_EMPTY;
            case FIRST       -> COLOR_FIRST;
            case FIRST_LAST  -> COLOR_FIRST_LIGHT;
            case SECOND      -> COLOR_SECOND;
            case SECOND_LAST -> COLOR_SECOND_LIGHT;
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape originalClip = g2.getClip();
        RoundRectangle2D round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2.setClip(round);

        g2.setColor(resolveBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
        g2.setClip(originalClip);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COLOR_BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
    }
}