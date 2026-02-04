package dev.madlador.mcts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Represents a node in the Monte Carlo Tree Search tree.
 * Each node maintains statistics (visits, value) and references to parent and children.
 */
class Node {

    private static final double DEFAULT_EXPLORATION = Math.sqrt(2);
    private static final Random RANDOM = new Random();

    private final State state;
    private final Node parent;
    private final double explorationConstant;

    private List<Node> children;
    private int visits;
    private double totalValue;
    private boolean expanded;

    /**
     * Creates a new node with the given state and parent.
     *
     * @param state the state this node represents
     * @param parent the parent node, or null if this is the root
     * @param explorationConstant the UCB exploration constant
     */
    Node(State state, Node parent, double explorationConstant) {
        this.state = state;
        this.parent = parent;
        this.explorationConstant = explorationConstant;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.totalValue = 0.0;
        this.expanded = false;
    }

    /**
     * Expands this node by creating child nodes for all legal actions.
     *
     * @return this node for method chaining
     */
    Node expand() {
        if (expanded || state.isTerminal()) {
            return this;
        }

        List<Action> actions = state.getLegalActions();
        for (Action action : actions) {
            State childState = state.clone();
            childState.apply(action);
            children.add(new Node(childState, this, explorationConstant));
        }

        expanded = true;
        return this;
    }



    /**
     * Selects the best child using UCB1 formula.
     *
     * @return the child with highest UCB value
     */
    Node selectBestChild() {
        return children.stream()
                .max(Comparator.comparingDouble(this::calculateUCB))
                .orElseThrow(() -> new IllegalStateException("No children to select from"));
    }

    /**
     * Selects a random child node.
     *
     * @return a random child
     */
    Node selectRandomChild() {
        if (children.isEmpty()) {
            throw new IllegalStateException("No children available");
        }
        return children.get(RANDOM.nextInt(children.size()));
    }

    /**
     * Calculates the UCB1 (Upper Confidence Bound) value for this node.
     *
     * @return UCB value, or MAX_VALUE if never visited
     */
    private double calculateUCB(Node child) {
        if (child.visits == 0) {
            return Double.MAX_VALUE;
        }

        double exploitation = child.totalValue / child.visits;
        double exploration = explorationConstant * Math.sqrt(Math.log(this.visits) / child.visits);

        return exploitation + exploration;
    }

    /**
     * Updates this node's statistics after a simulation.
     *
     * @param value the simulation result to backpropagate
     */
    void update(double value) {
        visits++;
        totalValue += value;
    }

    /**
     * Returns the child with the highest visit count (most promising).
     *
     * @return the most visited child
     */
    Node getMostVisitedChild() {
        return children.stream()
                .max(Comparator.comparingInt(Node::getVisits))
                .orElseThrow(() -> new IllegalStateException("No children available"));
    }

    // Getters

    State getState() {
        return state;
    }

    Node getParent() {
        return parent;
    }

    boolean hasChildren() {
        return !children.isEmpty();
    }

    boolean isExpanded() {
        return expanded;
    }

    boolean isTerminal() {
        return state.isTerminal();
    }

    int getVisits() {
        return visits;
    }

    double getAverageValue() {
        return visits == 0 ? 0.0 : totalValue / visits;
    }

    boolean isRoot() {
        return parent == null;
    }
}
