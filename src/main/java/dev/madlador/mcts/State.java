package dev.madlador.mcts;

import java.util.List;

public interface State {

    /**
     * Returns all legal actions available from this state.
     */
    List<Action> getLegalActions();

    /**
     * Creates an independent copy of this state.
     */
    State clone();

    void randomAction();

    double getSimulationOutcome();


    void performAction(Action action);
}
