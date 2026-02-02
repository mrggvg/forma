package dev.madlador.controllers;

import dev.madlador.models.GameModel;
import dev.madlador.views.AppView;

public class Controller {

    private GameModel gameModel;
    private AppView appView;

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;
        this.appView = new AppView(this, gameModel);
    }

    public void playMove(int row, int col) {
        this.gameModel.playMove(row, col);
    }

    public void newGame() {
        gameModel.newGame();
    }

}
