package dev.madlador.game;

/**
 * Immutable snapshot of the game board.
 *
 * @param first    bitboard of the first player's pieces
 * @param second   bitboard of the second player's pieces
 * @param metadata packed byte — bit 7 is the turn flag, bits 0–5 are the last move index
 */
public record State(long first, long second, byte metadata) {

    /**
     * Returns an empty board with the first player to move.
     */
    public static State emptyState() {
        return new State(0L, 0L, (byte) 0x80);
    }

    /**
     * Applies {@code move} to {@code previous} without validation.
     * Sets the bit on the active player's board, flips the turn, and records the move index.
     *
     * @param previous state before the move
     * @param move     move to apply
     * @return new state after the move
     */
    public static State transition(State previous, Move move) {
        long first = previous.first();
        long second = previous.second();
        long moveMask = move.toMask();
        boolean firstToMove = (previous.metadata & 0x80) != 0;

        long newFirst = firstToMove ? (first | moveMask) : first;
        long newSecond = firstToMove ? second : (second | moveMask);

        byte nextTurnBit = (byte) (firstToMove ? 0x00 : 0x80);
        byte metadata = (byte) ((move.toBitIndex() & 0x3F) | nextTurnBit);

        return new State(newFirst, newSecond, metadata);
    }

    /**
     * Prints a colored board to stdout. The last move is highlighted in a lighter shade.
     */
    public void dump() {
        final String RESET = "\u001B[38;5;0m";
        final String BLUE = "\u001B[38;5;26m";
        final String BLUE_LIGHT = "\u001B[38;5;51m";
        final String MAGENTA = "\u001B[38;5;199m";
        final String MAGENTA_LIGHT = "\u001B[38;5;219m";
        final String TILE = "⬛ ";

        int lastMoveIndex = metadata & 0x3F;
        long lastMask = 1L << lastMoveIndex;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 49; i++) {
            long mask = 1L << i;

            if ((first & mask) != 0) {
                sb.append(mask == lastMask ? MAGENTA_LIGHT : MAGENTA);
            } else if ((second & mask) != 0) {
                sb.append(mask == lastMask ? BLUE_LIGHT : BLUE);
            } else {
                sb.append(RESET);
            }
            sb.append(TILE);

            if ((i + 1) % 7 == 0) sb.append("\n");
        }

        sb.append(RESET);
        System.out.println(sb);
    }

}
