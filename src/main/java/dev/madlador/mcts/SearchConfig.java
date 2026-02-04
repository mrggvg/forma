package dev.madlador.mcts;

/**
 * Configuration parameters for Monte Carlo Tree Search.
 */
public class SearchConfig {

    private final int defaultIterations;
    private final double explorationConstant;

    private SearchConfig(Builder builder) {
        this.defaultIterations = builder.defaultIterations;
        this.explorationConstant = builder.explorationConstant;
    }

    /**
     * Creates a configuration with default values.
     *
     * @return default configuration
     */
    public static SearchConfig defaults() {
        return builder().build();
    }

    /**
     * Creates a new configuration builder.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    public int getDefaultIterations() {
        return defaultIterations;
    }

    public double getExplorationConstant() {
        return explorationConstant;
    }

    /**
     * Builder for SearchConfig.
     */
    public static class Builder {
        private int defaultIterations = 1000;
        private double explorationConstant = Math.sqrt(2);

        /**
         * Sets the default number of iterations.
         *
         * @param iterations number of iterations (must be positive)
         * @return this builder
         */
        public Builder defaultIterations(int iterations) {
            if (iterations <= 0) {
                throw new IllegalArgumentException("Iterations must be positive");
            }
            this.defaultIterations = iterations;
            return this;
        }

        /**
         * Sets the UCB exploration constant.
         * Typical values: sqrt(2) for balanced exploration/exploitation.
         * Higher values encourage more exploration.
         *
         * @param constant exploration constant (must be non-negative)
         * @return this builder
         */
        public Builder explorationConstant(double constant) {
            if (constant < 0) {
                throw new IllegalArgumentException("Exploration constant must be non-negative");
            }
            this.explorationConstant = constant;
            return this;
        }

        /**
         * Builds the configuration.
         *
         * @return a new SearchConfig instance
         */
        public SearchConfig build() {
            return new SearchConfig(this);
        }
    }
}
