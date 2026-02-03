package dev.madlador.view;

import dev.madlador.controller.Controller;
import dev.madlador.model.GameModel;

import javax.swing.*;
import java.awt.*;

public class ControlView extends JPanel {

    private final GameModel gameModel;
    private final Controller controller;

    private JLabel statusLabel;
    private JLabel turnLabel;
    private JLabel moveCountLabel;

    public ControlView(Controller controller, GameModel gameModel) {
        this.gameModel = gameModel;
        this.controller = controller;

        setPreferredSize(new Dimension(180, 0));
        setBackground(new Color(0x181818));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addTitle();
        addStatus();
        add(Box.createVerticalStrut(20));
        addControls();
    }

    private void addTitle() {
        JLabel title = new JLabel("4mation");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);
    }

    private void addStatus() {
        statusLabel = createLabel("Status: Playing");
        turnLabel = createLabel("Turn: Pink");
        moveCountLabel = createLabel("Moves: 0");

        add(Box.createVerticalStrut(15));
        add(statusLabel);
        add(Box.createVerticalStrut(8));
        add(turnLabel);
        add(Box.createVerticalStrut(8));
        add(moveCountLabel);
    }

    private void addControls() {
        add(Box.createVerticalGlue());

        JButton newGameButton = new JButton("New Game");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.addActionListener(e -> controller.newGame());

        add(newGameButton);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(0xDDDDDD));
        label.setFont(label.getFont().deriveFont(14f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
