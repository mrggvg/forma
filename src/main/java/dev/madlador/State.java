package dev.madlador;

/**
 * Immutable game state.
 *
 * @param b1 first player bitboard
 * @param b2 second player bitboard
 * @param lm last move bit index
 */
public record State(long b1, long b2, int lm) {

    /** @return total number of moves made */
    int turn() {
        return Long.bitCount(b1) + Long.bitCount(b2);
    }

    /** @return 0 if first player's move, 1 if second player's move */
    int nextPlayer() {
        return turn() % 2;
    }

    long lastMoveMask() {
        return 1L << lm;
    }

}

