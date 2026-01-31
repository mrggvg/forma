package dev.madlador;

/**
 * Immutable game state.
 * @param b1 first player bitboard
 * @param b2 second player bitboard
 * @param lm last move bit index
 */
public record State(long b1, long b2, int lm) {}

