package dev.madlador;

import dev.madlador.engine.Engine;
import dev.madlador.engine.GameState;
import dev.madlador.engine.Move;
import dev.madlador.mcts.Action;
import dev.madlador.mcts.Search;
import dev.madlador.mcts.SearchConfig;
import dev.madlador.mcts.State;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine();
        GameState gameState = GameState.emptyState();

        while (engine.outcome(gameState) == 0) {
            // Random player move
            List<Move> legalMoves = engine.moves(gameState);
            Collections.shuffle(legalMoves);
            gameState = gameState.transition(legalMoves.getFirst());
            gameState.dump();

            if (engine.outcome(gameState) != 0) break;

            // MCTS agent move
            GameStateAdapter state = new GameStateAdapter(gameState, engine);
            Search search = new Search(state);
            Action bestAction = search.findBestAction(1000); // 1000 iterations

            MoveAdapter moveAdapter = (MoveAdapter) bestAction;
            gameState = gameState.transition(moveAdapter.getMove());
            gameState.dump();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Game over! Outcome: " + engine.outcome(gameState));
    }
}

class GameStateAdapter implements State {
    private static final Random RANDOM = new Random();

    private final Engine engine;
    private GameState gameState;

    public GameStateAdapter(GameState gameState, Engine engine) {
        this.gameState = gameState;
        this.engine = engine;
    }

    @Override
    public List<Action> getLegalActions() {
        return engine.moves(gameState).stream()
                .map(MoveAdapter::new)
                .collect(Collectors.toList());
    }

    @Override
    public void apply(Action action) {
        MoveAdapter moveAdapter = (MoveAdapter) action;
        gameState = gameState.transition(moveAdapter.getMove());
    }

    @Override
    public void applyRandom() {
        List<Move> moves = engine.moves(gameState);
        if (!moves.isEmpty()) {
            gameState = gameState.transition(moves.get(RANDOM.nextInt(moves.size())));
        }
    }

    @Override
    public State clone() {
        // Assuming GameState is immutable (record), just create new adapter
        return new GameStateAdapter(gameState, engine);
    }

    @Override
    public boolean isTerminal() {
        return engine.outcome(gameState) != 0;
    }

    @Override
    public double getReward() {
        int outcome = engine.outcome(gameState);
        // Assuming outcome: 1 = player 1 wins, -1 = player 2 wins, 0 = ongoing
        return outcome;
    }

    @Override
    public boolean isMaximizing() {
        // Assuming player 1 is maximizing (return true when it's player 1's turn)
        // You'll need to check whose turn it is from gameState

        return false; // Adjust based on your GameState API
    }
}

class MoveAdapter implements Action {
    private final Move move;

    public MoveAdapter(Move move) {
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoveAdapter)) return false;
        MoveAdapter that = (MoveAdapter) o;
        return move.equals(that.move);
    }

    @Override
    public int hashCode() {
        return move.hashCode();
    }
}