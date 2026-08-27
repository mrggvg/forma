package dev.madlador.views;

import dev.madlador.models.GameModel;
import dev.madlador.models.GameModelObserver;

import javax.swing.*;
import java.awt.*;

public class ControlView extends JPanel implements GameModelObserver {

    private static final Color COLOR_BG = new Color(0x121212);
    private static final Color COLOR_TEXT = new Color(0x9A9A9A);

    private final GameModel gameModel;
    private final JLabel statusLabel;

    public ControlView(GameModel gameModel) {
        this.gameModel = gameModel;
        this.statusLabel = new JLabel("", SwingConstants.CENTER);

        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        statusLabel.setForeground(COLOR_TEXT);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 13f));
        add(statusLabel, BorderLayout.CENTER);

        gameModel.subscribe(this);
        update(); // Initial render
    }

    @Override
    public void update() {
        statusLabel.setText(resolveStatus());
    }

    private String resolveStatus() {
        int outcome = gameModel.getGameOutcome();

        if (outcome != 0) {
            return switch (outcome) {
                case 1 -> "You won!";
                case 2 -> "Bot won!";
                default -> "Stalemate!";
            };
        }

        if (gameModel.isBotThinking()) return "Bot is thinking…";
        return gameModel.isHumanTurn() ? "Your turn" : "Bot's turn";
    }

}
