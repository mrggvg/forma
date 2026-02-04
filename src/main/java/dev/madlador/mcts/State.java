package dev.madlador.mcts;

import java.util.List;

/**
 * Represents a state in the search space for Monte Carlo Tree Search.
 * Implementations must provide state transitions, terminal checking, and evaluation.
 */
public interface State {

    /**
     * Returns all legal actions available from this state.
     *
     * @return list of legal actions, empty if terminal state
     */
    List<Action> getLegalActions();

    /**
     * Applies the given action to this state, modifying it in place.
     *
     * @param action the action to apply
     * @throws IllegalArgumentException if action is not legal
     */
    void apply(Action action);

    /**
     * Applies a uniformly random legal action to this state.
     * Used during the simulation phase of MCTS.
     *
     * @throws IllegalStateException if no legal actions available
     */
    void applyRandom();

    /**
     * Creates an independent deep copy of this state.
     *
     * @return a new state instance with identical configuration
     */
    State clone();

    /**
     * Checks if this state is terminal (no further actions possible).
     *
     * @return true if terminal, false otherwise
     */
    boolean isTerminal();

    /**
     * Returns the reward for the agent from this state's perspective.
     * Only meaningful for terminal states.
     *
     * @return reward value, typically in range [-1.0, 1.0]
     */
    double getReward();

    /**
     * Indicates whether this state represents a maximizing player's turn.
     * Used to correctly backpropagate values through the tree.
     *
     * @return true for maximizing player, false for minimizing player
     */
    boolean isMaximizing();
}
