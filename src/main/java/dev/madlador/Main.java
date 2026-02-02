package dev.madlador;

import dev.madlador.controllers.Controller;
import dev.madlador.models.GameModel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
            GameModel gameModel = new GameModel();
            Controller controller = new Controller(gameModel);
        });
    }
}
