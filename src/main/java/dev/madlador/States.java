package dev.madlador;

final public class States {

    /**
     * Takes previous state and applies move, returning new state.
     *
     * @param previous state to apply move to
     * @param move     move to apply
     * @return new state with move applied
     * @throws IllegalArgumentException if previous is null
     */
    public static State place(State previous, Move move) {
        if (previous == null) throw new IllegalArgumentException("Previous state cannot be null.");

        long b1 = previous.b1();
        long b2 = previous.b2();
        int next = previous.nextPlayer();

        if (next == 0) b1 = previous.b1() | move.toMask();
        else b2 = previous.b2() | move.toMask();

        return new State(b1, b2, move.toBitIndex());
    }

    /**
     * Creates initial state with first player's opening move.
     *
     * @param move first move to apply
     * @return new initial state
     */
    public static State place(Move move) {
        return new State(move.toMask(), 0L, move.toBitIndex());
    }

    /**
     * Prints colored board representation of the state to console.
     * Blue = first player, Magenta = second player.
     */
    public static void dump(State state) {
        final String RESET = "\u001B[38;5;0m";
        final String BLUE = "\u001B[38;5;27m";
        final String MAGENTA = "\u001B[38;5;201m";
        final String TILE = "⬛ ";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 49; i++) {
            long mask = 1L << i;

            if ((state.b1() & mask) != 0) sb.append(BLUE);
            else if ((state.b2() & mask) != 0) sb.append(MAGENTA);
            else sb.append(RESET);

            sb.append(TILE);
            if ((i + 1) % 7 == 0) sb.append("\n");
        }
        System.out.println(sb);
    }

}
