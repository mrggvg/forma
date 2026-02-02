package dev.madlador.controllers;

import dev.madlador.engine.Move;
import dev.madlador.models.GameModel;
import dev.madlador.views.AppView;

import java.util.Collections;
import java.util.List;

public class Controller {

    private GameModel gameModel;
    private AppView appView;

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;
        this.appView = new AppView(this, gameModel);
    }

    public void playMove(int row, int col) {
        this.gameModel.playMove(row, col);

        List<Move> moves = gameModel.getLegalMoves();
        Collections.shuffle(moves);

        Move move = moves.getFirst();
        this.gameModel.playMove(move.row(), move.col());

    }

    public void newGame() {
        gameModel.newGame();
    }

}
