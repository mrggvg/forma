package dev.madlador.mcts;

/**
 * Monte Carlo Tree Search implementation.
 * Executes the four phases: Selection, Expansion, Simulation, Backpropagation.
 */
public class Search {

    private final Node root;
    private final SearchConfig config;

    /**
     * Creates a new MCTS search starting from the given state.
     *
     * @param initialState the root state to search from
     * @param config search configuration parameters
     */
    public Search(State initialState, SearchConfig config) {
        this.config = config;
        this.root = new Node(initialState, null, config.getExplorationConstant());
    }

    /**
     * Creates a search with default configuration.
     *
     * @param initialState the root state to search from
     */
    public Search(State initialState) {
        this(initialState, SearchConfig.defaults());
    }

    /**
     * Executes MCTS for the specified time limit and returns the best action.
     *
     * @param timeLimitMs time limit in milliseconds
     * @return the best action found
     */
    public Action findBestAction(long timeLimitMs) {
        long startTime = System.currentTimeMillis();
        int iterations = 0;

        while (System.currentTimeMillis() - startTime < timeLimitMs) {
            executeIteration();
            iterations++;
        }

        return getBestAction();
    }

    /**
     * Executes MCTS for the specified number of iterations and returns the best action.
     *
     * @param iterations number of MCTS iterations to perform
     * @return the best action found
     */
    public Action findBestAction(int iterations) {
        for (int i = 0; i < iterations; i++) {
            executeIteration();
        }

        return getBestAction();
    }

    /**
     * Executes MCTS with default iteration count.
     *
     * @return the best action found
     */
    public Action findBestAction() {
        return findBestAction(config.getDefaultIterations());
    }

    /**
     * Executes a single MCTS iteration: selection, expansion, simulation, backpropagation.
     */
    private void executeIteration() {
        Node selected = select(root);
        Node expanded = expand(selected);
        double result = simulate(expanded);
        backpropagate(expanded, result);
    }

    /**
     * Selection phase: traverse tree using UCB until reaching a leaf.
     *
     * @param node the node to start selection from
     * @return the selected leaf node
     */
    private Node select(Node node) {
        Node current = node;

        while (current.hasChildren() && !current.isTerminal()) {
            current = current.selectBestChild();
        }

        return current;
    }

    /**
     * Expansion phase: expand the node if it has been visited before.
     *
     * @param node the node to potentially expand
     * @return the node to simulate from (either expanded child or original node)
     */
    private Node expand(Node node) {
        // Don't expand terminal nodes
        if (node.isTerminal()) {
            return node;
        }

        // Expand on second visit (first visit will simulate the node itself)
        if (node.getVisits() > 0 && !node.isExpanded()) {
            node.expand();
            // Return random child for simulation
            return node.hasChildren() ? node.selectRandomChild() : node;
        }

        return node;
    }

    /**
     * Simulation phase: play out the game randomly from the given node.
     *
     * @param node the node to simulate from
     * @return the simulation result
     */
    private double simulate(Node node) {
        State simulationState = node.getState().clone();

        while (!simulationState.isTerminal()) {
            simulationState.applyRandom();
        }

        return simulationState.getReward();
    }

    /**
     * Backpropagation phase: update statistics from leaf to root.
     *
     * @param node the leaf node to start backpropagation from
     * @param result the simulation result
     */
    private void backpropagate(Node node, double result) {
        Node current = node;

        while (current != null) {
            // Flip the value for opponent's perspective
            double value = current.getState().isMaximizing() ? result : -result;
            current.update(value);
            current = current.getParent();
        }
    }

    /**
     * Extracts the best action from the root node.
     * Uses the most visited child as the best choice.
     *
     * @return the action leading to the most visited child
     */
    private Action getBestAction() {
        if (!root.hasChildren()) {
            throw new IllegalStateException("No actions available from root state");
        }

        Node bestChild = root.getMostVisitedChild();

        // Find the action that leads to this child
        // We need to match the child's state with an action from the root
        State rootState = root.getState();
        for (Action action : rootState.getLegalActions()) {
            State testState = rootState.clone();
            testState.apply(action);

            // Compare states (assumes proper equals implementation)
            if (statesEqual(testState, bestChild.getState())) {
                return action;
            }
        }

        throw new IllegalStateException("Could not find action leading to best child");
    }

    /**
     * Compares two states for equality.
     * This is a simple reference check; implementations should override State.equals().
     */
    private boolean statesEqual(State s1, State s2) {
        // Fallback to reference equality if equals() not properly implemented
        return s1.equals(s2) || s1 == s2;
    }

    /**
     * Returns statistics about the search.
     *
     * @return search statistics
     */
    public SearchStats getStatistics() {
        return new SearchStats(
                root.getVisits(),
                root.getAverageValue(),
                countNodes(root)
        );
    }

    /**
     * Counts total nodes in the tree.
     */
    private int countNodes(Node node) {
        int count = 1;
        if (node.hasChildren()) {
            // Would need access to children - simplified version
            count += node.getVisits(); // Approximation
        }
        return count;
    }
}