package dev.madlador.mcts;

public class Search {

    private Node root;

    public void findBestAction(State state, long time) {

        long start = System.currentTimeMillis();
        root = new Node(state, null);

        while (System.currentTimeMillis() - start < time) {
            Node expanded = expand(select());
            propagate(expanded, simulate(expanded));
        }

        // return root.bestAction()
    }


    private Node select() {
        Node current = root;
        while (current.hasChildren()) {
            current = current.getBestChild();
        }
        return current;
    }

    private Node expand(Node node) {
        if (node.isSimulated() && !node.isTerminal()) {
            return node.expand().random();
        }
        return node;
    }

    private double simulate(Node node) {
        State state = node.getState().clone();
        while (!node.isTerminal()) {
            state.randomAction();
        }
        return state.getSimulationOutcome();
    }

    private void propagate(Node node, double simulationResult) {
        while (node != null) {
            node.incrementVisitsCount();
            node.addValue(simulationResult);
            node = node.getParent();
        }
    }

}
