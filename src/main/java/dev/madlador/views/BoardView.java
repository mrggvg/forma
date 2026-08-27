package dev.madlador.views;

import dev.madlador.controllers.Controller;
import dev.madlador.models.GameModel;
import dev.madlador.models.GameModelObserver;

import javax.swing.*;
import java.awt.*;

public class BoardView extends JPanel implements GameModelObserver {

    private final GameModel gameModel;
    private final Controller controller;
    private final SquareView[] squares;

    public BoardView(Controller controller, GameModel gameModel) {
        this.controller = controller;
        this.gameModel = gameModel;
        this.squares = new SquareView[49];

        gameModel.subscribe(this);
        setupUI();
        update(); // Initial render
    }

    private void setupUI() {
        setLayout(new GridLayout(7, 7, 8, 8));
        setBackground(new Color(0x121212));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        for (int i = 0; i < 49; i++) {
            int row = i / 7;
            int col = i % 7;
            squares[i] = new SquareView(controller, gameModel, row, col);
            add(squares[i]);
        }
    }

    @Override
    public void update() {
        // Reset all squares to disabled/empty state
        for (SquareView square : squares) {
            square.setPlayerState(SquareView.PlayerState.EMPTY);
            square.setEnabled(false);
            square.setLegalMove(false);
        }

        // Mark first player's moves
        gameModel.getFirstPlayerMoves().forEach(move -> {
            SquareView square = squares[move.toBitIndex()];
            boolean isLast = move.toBitIndex() == gameModel.getState().getLastMoveIndex();
            square.setPlayerState(isLast ? SquareView.PlayerState.FIRST_LAST : SquareView.PlayerState.FIRST);
        });

        // Mark second player's moves
        gameModel.getSecondPlayerMoves().forEach(move -> {
            SquareView square = squares[move.toBitIndex()];
            boolean isLast = move.toBitIndex() == gameModel.getState().getLastMoveIndex();
            square.setPlayerState(isLast ? SquareView.PlayerState.SECOND_LAST : SquareView.PlayerState.SECOND);
        });

        // Enable legal moves and mark them, but only while the human is on turn
        if (gameModel.isHumanTurn() && gameModel.getGameOutcome() == 0) {
            gameModel.getLegalMoves().forEach(move -> {
                SquareView square = squares[move.toBitIndex()];
                square.setEnabled(true);
                square.setLegalMove(true);
            });
        }

        repaint();
    }
}