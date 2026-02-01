package dev.madlador.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PlayerPanel extends JPanel {

    private final PlayerCard opponentCard;
    private final PlayerCard myCard;

    public PlayerPanel() {
        setBackground(new Color(0x171717));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        opponentCard = new PlayerCard("Player 2", new Color(0x27518F));
        opponentCard.setAlignmentX(LEFT_ALIGNMENT);
        add(opponentCard);

        add(Box.createVerticalGlue());

        myCard = new PlayerCard("Player 1", new Color(0x8F2780));
        myCard.setAlignmentX(LEFT_ALIGNMENT);
        add(myCard);
    }

    public PlayerCard getOpponentCard() { return opponentCard; }
    public PlayerCard getMyCard()       { return myCard; }

    // --- Player Card ---

    public static class PlayerCard extends JPanel {

        private final JLabel nameLabel;
        private final JLabel timerLabel;
        private final AvatarPanel avatar;
        private boolean active = false;

        public PlayerCard(String name, Color color) {
            setBackground(new Color(0x1E1E1E));
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
            setMaximumSize(new Dimension(Short.MAX_VALUE, 90));

            avatar = new AvatarPanel(color, 56);
            add(avatar);

            JPanel info = new JPanel();
            info.setBackground(new Color(0x1E1E1E));
            info.setLayout(new BoxLayout(info, BoxLayout.PAGE_AXIS));

            nameLabel = new JLabel(name);
            nameLabel.setForeground(new Color(0xE0E0E0));
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            info.add(nameLabel);
            info.add(Box.createVerticalStrut(6));

            timerLabel = new JLabel("0:00");
            timerLabel.setForeground(new Color(0x888888));
            timerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            info.add(timerLabel);

            add(info);
        }

        public void setTime(String time) {
            timerLabel.setText(time);
        }

        public void setActive(boolean active) {
            this.active = active;
            setBorder(BorderFactory.createLineBorder(
                    active ? new Color(0x6BCB77) : new Color(0x1E1E1E), 2));
            repaint();
        }

        public boolean isActive() { return active; }
    }

    // --- Avatar ---

    private static class AvatarPanel extends JPanel {

        private final Color color;
        private final int size;

        public AvatarPanel(Color color, int size) {
            this.color = color;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            setBackground(new Color(0x171717));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Float(0, 0, size, size, 12, 12));
        }
    }

}