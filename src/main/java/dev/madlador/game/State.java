package dev.madlador.game;

/**
 * Represents the immutable state of the game at a given moment.
 * <p>
 * <b>Bitboards:</b>
 * <ul>
 *     <li>{@code first}: bits set for positions occupied by the first player</li>
 *     <li>{@code second}: bits set for positions occupied by the second player</li>
 * </ul>
 * <p>
 * <b>Metadata:</b> packed into a single byte. Layout:
 * <table border="1" cellpadding="4">
 *     <tr>
 *         <th>Bit</th><th>Purpose</th>
 *     </tr>
 *     <tr>
 *         <td>7 (0x80)</td><td>Turn flag: 1 = first player's turn, 0 = second player's turn</td>
 *     </tr>
 *     <tr>
 *         <td>4–5 (0x30)</td><td>Index of the last move applied (0–48)</td>
 *     </tr>
 *     <tr>
 *         <td>0–3 (0x0F)</td><td>Unused / reserved</td>
 *     </tr>
 * </table>
 */
public record State(long first, long second, byte metadata) {

    /**
     * Returns an empty initial state where no moves have been played,
     * and it is the first player's turn.
     *
     * @return a new {@code State} representing an empty board
     */
    public static State emptyState() {
        return new State(0L, 0L, (byte) 0x80);
    }

    /**
     * Returns a new {@code State} resulting from applying the given move
     * to the specified previous state.
     * <p>
     * <b>Important:</b> This method does <b>not</b> validate the move against
     * game rules. It simply applies the move to the appropriate bitboard,
     * flips the turn bit, and updates the last move index in metadata.
     * <p>
     * The method:
     * <ul>
     *     <li>Determines which player is to move from the previous state's metadata.</li>
     *     <li>Updates the appropriate bitboard with the move.</li>
     *     <li>Flips the turn bit and stores the last move index in metadata.</li>
     * </ul>
     *
     * @param previous the previous {@code State} to apply the move to
     * @param move     the {@code Move} to apply
     * @return a new {@code State} representing the game after the move
     */
    public static State transition(State previous, Move move) {
        long first = previous.first();
        long second = previous.second();
        long moveMask = move.toMask();
        boolean firstToMove = (previous.metadata & 0x80) != 0;

        long newFirst = firstToMove ? (first | moveMask) : first;
        long newSecond = firstToMove ? second : (second | moveMask);

        byte nextTurnBit = (byte) (previous.metadata ^ 0x80);
        byte metadata = (byte) (move.toBitIndex() | nextTurnBit);

        return new State(newFirst, newSecond, metadata);
    }

}
