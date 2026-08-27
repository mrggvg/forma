package dev.madlador.ai;

import dev.madlador.engine.Engine;
import dev.madlador.engine.Move;
import dev.madlador.engine.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Opponent driven by Monte Carlo tree search (UCT).
 * <p>
 * Every iteration walks the tree down to a leaf, expands one untried move,
 * plays a uniformly random game out from there and feeds the result back up
 * the path. The search is stateless between calls — it grows a fresh tree for
 * the position it is handed — so it can be pointed at any {@link State}.
 * <p>
 * Scores are stored from the point of view of the player that moved <em>into</em>
 * a node, which is what makes the parent's comparison of its children meaningful.
 */
public class MctsBot {

    /** Classic UCT constant, balances exploitation against exploration. */
    private static final double EXPLORATION = Math.sqrt(2);

    private final Engine engine = new Engine();

    private final long budgetMillis;
    private final int maxIterations;

    private int lastIterations;

    public MctsBot(long budgetMillis, int maxIterations) {
        this.budgetMillis = budgetMillis;
        this.maxIterations = maxIterations;
    }

    /**
     * Searches {@code root} and returns the move to play.
     *
     * @param root position to move from
     * @return the best move found, or {@code null} if the position is finished
     * or has no legal continuation
     */
    public Move chooseMove(State root) {
        lastIterations = 0;

        if (engine.outcome(root) != 0) return null;

        List<Move> rootMoves = engine.moves(root);
        if (rootMoves.isEmpty()) return null;
        if (rootMoves.size() == 1) return rootMoves.getFirst();

        Node rootNode = new Node(null, null, root, 0, rootMoves);
        long deadline = System.nanoTime() + budgetMillis * 1_000_000L;

        while (lastIterations < maxIterations && System.nanoTime() < deadline) {
            lastIterations++;

            // Selection — descend through fully expanded nodes by UCT score
            Node node = rootNode;
            while (!node.isTerminal() && node.isFullyExpanded()) {
                node = node.selectChild();
            }

            // Expansion — grow the tree by one untried move
            if (!node.isTerminal()) node = expand(node);

            // Simulation & backpropagation
            backpropagate(node, simulate(node.state, node.outcome));
        }

        return rootNode.mostVisitedMove();
    }

    /** Number of iterations the last {@link #chooseMove(State)} call managed to run. */
    public int getLastIterations() {
        return lastIterations;
    }

    private Node expand(Node node) {
        Move move = node.takeUntriedMove();
        State next = node.state.transition(move);

        int outcome = engine.outcome(next);
        List<Move> moves = outcome == 0 ? engine.moves(next) : List.of();

        Node child = new Node(node, move, next, outcome, moves);
        node.children.add(child);
        return child;
    }

    /**
     * Plays a uniformly random game out from {@code state}.
     *
     * @return the engine outcome of the finished game, using {@code -1} for a draw
     * (which also covers a position that simply ran out of legal moves)
     */
    private int simulate(State state, int outcome) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        State current = state;

        while (outcome == 0) {
            List<Move> moves = engine.moves(current);
            if (moves.isEmpty()) return -1;

            current = current.transition(moves.get(random.nextInt(moves.size())));
            outcome = engine.outcome(current);
        }

        return outcome;
    }

    private void backpropagate(Node node, int outcome) {
        for (Node current = node; current != null; current = current.parent) {
            current.visits++;
            current.score += reward(outcome, current.playerJustMoved);
        }
    }

    private static double reward(int outcome, int player) {
        if (outcome == -1) return 0.5;
        return outcome == player ? 1.0 : 0.0;
    }


    /**
     * A single position in the search tree.
     */
    private static final class Node {

        private final Node parent;
        private final Move move;              // move that led here, null at the root
        private final State state;
        private final int outcome;            // engine outcome of state, 0 while in progress
        private final int playerJustMoved;    // 1 or 2, the player scores belong to

        private final List<Move> untried;
        private final List<Node> children;

        private int visits;
        private double score;

        private Node(Node parent, Move move, State state, int outcome, List<Move> legalMoves) {
            this.parent = parent;
            this.move = move;
            this.state = state;
            this.outcome = outcome;
            this.playerJustMoved = state.isFirstPlayerToMove() ? 2 : 1;
            this.untried = new ArrayList<>(legalMoves);
            this.children = new ArrayList<>(legalMoves.size());
        }

        private boolean isTerminal() {
            return outcome != 0 || (untried.isEmpty() && children.isEmpty());
        }

        private boolean isFullyExpanded() {
            return untried.isEmpty();
        }

        private Move takeUntriedMove() {
            return untried.remove(ThreadLocalRandom.current().nextInt(untried.size()));
        }

        /**
         * Picks the child with the highest UCT score. Every child has been
         * visited at least once, so no division by zero is possible here.
         */
        private Node selectChild() {
            double logVisits = Math.log(visits);
            Node best = null;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (Node child : children) {
                double uct = child.score / child.visits
                        + EXPLORATION * Math.sqrt(logVisits / child.visits);

                if (uct > bestScore) {
                    bestScore = uct;
                    best = child;
                }
            }

            return best;
        }

        /**
         * Final move choice — the most visited child, which is more robust
         * than the highest scoring one. Ties are broken by win rate.
         */
        private Move mostVisitedMove() {
            Node best = null;

            for (Node child : children) {
                if (best == null
                        || child.visits > best.visits
                        || (child.visits == best.visits && child.score > best.score)) {
                    best = child;
                }
            }

            return best == null ? null : best.move;
        }
    }

}
