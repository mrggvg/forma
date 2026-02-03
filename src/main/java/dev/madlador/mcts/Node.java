package dev.madlador.mcts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Node {

    private final double C = Math.sqrt(2);
    private int visits = 0;
    private double value = 0;

    private final State state;

    private Node parent;
    private List<Node> children = new ArrayList<>();


    public Node(State state, Node parent) {
        this.state = state;
        this.parent = parent;
    }




    public Node expand() {
        List<Action> actions = state.getLegalActions();
        for (Action action : actions) {
            State clone = this.state.clone();
            clone.performAction(action);
            this.children.add(new Node(clone, this));
        }
        return this;
    }

    private double calculateUct() {
        return visits == 0 ? Double.MAX_VALUE : (value / visits) + C * Math.sqrt(Math.log(parent.visits) / visits);
    }







    Node getParent() {
        return this.parent;
    }


    void incrementVisitsCount() {

    }

    void addValue(double result) {

    }

    State getState() {
        return this.state;
    }


    public Node random() {
        return null;
    }

    boolean hasChildren() {
        return false;
    }

    Node getBestChild() {
        return children.stream().max(Comparator.comparingDouble(Node::calculateUct)).orElse(null);
    }

    boolean isSimulated() {
        return false;
    }

    boolean isTerminal() {
        return false;
    }

}
