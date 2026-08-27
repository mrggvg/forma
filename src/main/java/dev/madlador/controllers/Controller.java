package dev.madlador.controllers;

import dev.madlador.ai.MctsBot;
import dev.madlador.engine.Move;
import dev.madlador.engine.State;
import dev.madlador.models.GameModel;
import dev.madlador.views.AppView;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class Controller {

    /** How long the bot is allowed to think about a move. */
    private static final long BOT_BUDGET_MILLIS = 1000;
    private static final int BOT_MAX_ITERATIONS = 1_000_000;

    private GameModel gameModel;
    private AppView appView;

    private final MctsBot bot = new MctsBot(BOT_BUDGET_MILLIS, BOT_MAX_ITERATIONS);

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;
        this.appView = new AppView(this, gameModel);
    }

    public void playMove(int row, int col) {
        // The human owns the first player only, and never moves out of turn
        if (!gameModel.isHumanTurn() || gameModel.isBotThinking()) return;
        if (gameModel.getGameOutcome() != 0) return;

        gameModel.playMove(row, col);
        scheduleBotMove();
    }

    public void newGame() {
        // The human always starts, so the bot has nothing to do here
        gameModel.newGame();
    }

    /**
     * Runs the search off the event dispatch thread and applies the answer
     * back on it, so the board stays responsive while the bot thinks.
     */
    private void scheduleBotMove() {
        if (gameModel.isHumanTurn() || gameModel.getGameOutcome() != 0) return;

        State position = gameModel.getState();
        gameModel.setBotThinking(true);

        new SwingWorker<Move, Void>() {

            @Override
            protected Move doInBackground() {
                return bot.chooseMove(position);
            }

            @Override
            protected void done() {
                gameModel.setBotThinking(false);

                // A new game may have been started while the bot was thinking
                if (!gameModel.getState().equals(position)) return;

                try {
                    Move move = get();
                    if (move != null) gameModel.playMove(move.row(), move.col());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    throw new IllegalStateException("Bot failed to produce a move", e.getCause());
                }
            }

        }.execute();
    }

}
