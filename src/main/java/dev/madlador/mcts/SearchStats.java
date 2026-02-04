package dev.madlador.mcts;

/**
 * Statistics from a completed MCTS search.
 */
public class SearchStats {

    private final int totalIterations;
    private final double rootValue;
    private final int treeSize;

    public SearchStats(int totalIterations, double rootValue, int treeSize) {
        this.totalIterations = totalIterations;
        this.rootValue = rootValue;
        this.treeSize = treeSize;
    }

    /**
     * Total number of MCTS iterations performed.
     */
    public int getTotalIterations() {
        return totalIterations;
    }

    /**
     * Average value of the root node.
     */
    public double getRootValue() {
        return rootValue;
    }

    /**
     * Total number of nodes in the search tree.
     */
    public int getTreeSize() {
        return treeSize;
    }

    @Override
    public String toString() {
        return String.format("SearchStats[iterations=%d, rootValue=%.3f, treeSize=%d]",
                totalIterations, rootValue, treeSize);
    }
}

