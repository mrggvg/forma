package dev.madlador.view;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private final JButton newGameButton;
    private final JButton pauseButton;
    private final JSlider speedSlider;
    private final JLabel turnLabel;
    private final JLabel moveCountLabel;
    private final JLabel statusLabel;

    public ControlPanel() {
        setBackground(new Color(0x171717));
        setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Turn indicator
        turnLabel = createLabel("First player's turn", 14);
        addSection(turnLabel);

        // Move counter
        moveCountLabel = createLabel("Moves: 0", 13);
        addSection(moveCountLabel);

        // Status
        statusLabel = createLabel("In progress", 13);
        statusLabel.setForeground(new Color(0x6BCB77));
        addSection(statusLabel);

        // Separator
        addSeparator();

        // Speed control
        JLabel speedLabel = createLabel("Playback speed", 13);
        addSection(speedLabel);

        speedSlider = new JSlider(SwingConstants.HORIZONTAL, 50, 2000, 500);
        speedSlider.setInverted(true); // lower ms = faster = slider to the right
        speedSlider.setBackground(getBackground());
        speedSlider.setForeground(new Color(0x6BCB77));
        speedSlider.setFocusable(false);
        speedSlider.setAlignmentX(LEFT_ALIGNMENT);
        speedSlider.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        add(speedSlider);
        add(Box.createVerticalStrut(16));

        // Buttons
        newGameButton = styledButton("New Game");
        newGameButton.setAlignmentX(LEFT_ALIGNMENT);
        newGameButton.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        add(newGameButton);
        add(Box.createVerticalStrut(8));

        pauseButton = styledButton("Pause");
        pauseButton.setAlignmentX(LEFT_ALIGNMENT);
        pauseButton.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        add(pauseButton);
    }

    // --- Getters for external wiring ---

    public JButton getNewGameButton() { return newGameButton; }
    public JButton getPauseButton()   { return pauseButton; }
    public JSlider getSpeedSlider()   { return speedSlider; }

    // --- State updates ---

    public void setTurn(String playerName) {
        turnLabel.setText(playerName + "'s turn");
    }

    public void setMoveCount(int count) {
        moveCountLabel.setText("Moves: " + count);
    }

    public void setStatus(String status, Color color) {
        statusLabel.setText(status);
        statusLabel.setForeground(color);
    }

    public void setPaused(boolean paused) {
        pauseButton.setText(paused ? "Resume" : "Pause");
    }

    // --- Helpers ---

    private JLabel createLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(0xE0E0E0));
        label.setFont(new Font("SansSerif", Font.PLAIN, size));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    setBackground(new Color(0x3A5A3A));
                } else if (getModel().isRollover()) {
                    setBackground(new Color(0x2E3E2E));
                } else {
                    setBackground(new Color(0x2A2A2A));
                }
                super.paintComponent(g);
            }
        };
        btn.setBackground(new Color(0x2A2A2A));
        btn.setForeground(new Color(0xE0E0E0));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0x3A3A3A), 1));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return btn;
    }

    private void addSection(JComponent component) {
        component.setAlignmentX(LEFT_ALIGNMENT);
        add(component);
        add(Box.createVerticalStrut(8));
    }

    private void addSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setAlignmentX(LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        sep.setForeground(new Color(0x2A2A2A));
        add(sep);
        add(Box.createVerticalStrut(16));
    }

}