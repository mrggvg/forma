package dev.madlador;

import dev.madlador.engine.Engine;
import dev.madlador.engine.GameState;
import dev.madlador.engine.Move;
import dev.madlador.mcts.Action;
import dev.madlador.mcts.Search;
import dev.madlador.mcts.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Engine engine = new Engine();

        Search search = new Search();
        search.findBestAction(new StateAdapter(GameState.emptyState(), engine), 1000);





    }
}


class StateAdapter implements State {

    private Engine engine;
    private GameState gameState;


    public StateAdapter(GameState state, Engine engine) {
        this.gameState = state;
        this.engine = engine;
    }

    @Override
    public List<Action> getLegalActions() {

        List<Move> moves = engine.moves(gameState);
        List<Action> actions = new ArrayList<>();
        moves.forEach(move -> {
            actions.add(new ActionAdapter(move));
        });
        return actions;
    }

    @Override
    public State clone() {
        return this;
    }

    @Override
    public void randomAction() {
        var actions = getLegalActions();
        Collections.shuffle(actions);

    }

    @Override
    public double getSimulationOutcome() {
        return 0;
    }

    @Override
    public void performAction(Action action) {

    }
}


class ActionAdapter implements Action {

    private Move move;

    public ActionAdapter(Move move) {
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}