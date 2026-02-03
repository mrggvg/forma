package dev.madlador.model;

import dev.madlador.engine.Engine;
import dev.madlador.engine.GameState;
import dev.madlador.engine.Move;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameModel {

    private Set<GameModelObserver> observers = new HashSet<>();

    private Engine engine;
    private GameState gameState;

    public GameModel() {
        this.engine = new Engine();
        this.gameState = GameState.emptyState();
    }

    public void playMove(int row, int col) {
        gameState = gameState.transition(new Move(row, col));
        notifyObservers();
    }

    public void newGame() {
        gameState = GameState.emptyState();
        notifyObservers();
    }

    public List<Move> getFirstPlayerMoves() {
        return gameState.getFirstPlayerMoves();
    }

    public List<Move> getSecondPlayerMoves() {
        return gameState.getSecondPlayerMoves();
    }

    public List<Move> getLegalMoves() {
        return engine.moves(gameState);
    }

    public int getGameOutcome() {
        return engine.outcome(gameState);
    }

    public void subscribe(GameModelObserver observer) {
        this.observers.add(observer);
    }

    public void unsubscribe(GameModelObserver observer) {
        this.observers.remove(observer);
    }

    public void notifyObservers() {
        this.observers.forEach(GameModelObserver::update);
    }

    public GameState getState() {
        return gameState;
    }
}
