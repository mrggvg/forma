package dev.madlador.models;

import dev.madlador.engine.Engine;
import dev.madlador.engine.Move;
import dev.madlador.engine.State;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameModel {

    private Set<GameModelObserver> observers = new HashSet<>();

    private Engine engine;
    private State state;

    private boolean botThinking = false;

    public GameModel() {
        this.engine = new Engine();
        this.state = State.emptyState();
    }

    public void playMove(int row, int col) {
        state = state.transition(new Move(row, col));
        notifyObservers();
    }

    public void newGame() {
        state = State.emptyState();
        botThinking = false;
        notifyObservers();
    }

    /**
     * @return {@code true} while it is the human's move — the first player is
     * always the human, so this simply follows the turn flag
     */
    public boolean isHumanTurn() {
        return state.isFirstPlayerToMove();
    }

    public boolean isBotThinking() {
        return botThinking;
    }

    public void setBotThinking(boolean botThinking) {
        this.botThinking = botThinking;
        notifyObservers();
    }

    public List<Move> getFirstPlayerMoves() {
        return state.getFirstPlayerMoves();
    }

    public List<Move> getSecondPlayerMoves() {
        return state.getSecondPlayerMoves();
    }

    public List<Move> getLegalMoves() {
        return engine.moves(state);
    }

    public int getGameOutcome() {
        return engine.outcome(state);
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

    public State getState() {
        return state;
    }
}
