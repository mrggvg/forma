package dev.madlador.sandbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class View extends JFrame implements ModelObserver, ActionListener {

    private Model model;
    private Controller controller;

    private final JLabel countLabel = new JLabel();
    private final JButton incBtn = createStyledButton("+");
    private final JButton decBtn = createStyledButton("−");

    public View(Controller controller, Model model) {
        this.controller = controller;
        this.model = model;
        this.model.attach(this);

        setupUI();
    }

    private void setupUI() {
        // Configure frame
        setTitle("Counter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(0x121212));
        setLocationRelativeTo(null);

        // Configure label
        countLabel.setText(Integer.toString(model.getCount()));
        countLabel.setHorizontalAlignment(JLabel.CENTER);
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 72));
        countLabel.setForeground(new Color(0xE0E0E0));
        countLabel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Configure buttons
        incBtn.addActionListener(this);
        decBtn.addActionListener(this);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        buttonPanel.setBackground(new Color(0x121212));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        buttonPanel.add(decBtn);
        buttonPanel.add(incBtn);

        // Layout
        add(countLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(380, 280));
        pack();
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg;
                if (getModel().isPressed()) {
                    bg = new Color(0x3A5A3A);
                } else if (getModel().isRollover()) {
                    bg = new Color(0x2E3E2E);
                } else {
                    bg = new Color(0x2A2A2A);
                }

                Shape originalClip = g2.getClip();
                RoundRectangle2D round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setClip(round);
                g2.setColor(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());

                super.paintComponent(g);
                g2.setClip(originalClip);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x3A3A3A));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };

        btn.setForeground(new Color(0xE0E0E0));
        btn.setFont(new Font("SansSerif", Font.BOLD, 24));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 52));

        return btn;
    }

    @Override
    public void updateCount(int count) {
        countLabel.setText(Integer.toString(count));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton source = (JButton) actionEvent.getSource();
        if (source.equals(incBtn)) {
            controller.incrementCount();
        } else {
            controller.decrementCount();
        }
    }
}