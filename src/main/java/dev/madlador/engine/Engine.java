package dev.madlador.engine;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    private static final long[] OUTLINES = new long[49];
    private static final long[][] WIN_MASKS = new long[49][];

    static {
        // Precomputing outlines
        for (int i = 0; i < 49; i++) {
            int row = i / 7;
            int col = i % 7;
            long mask = 0L;

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int nr = row + dr;
                    int nc = col + dc;
                    if (nr >= 0 && nr < 7 && nc >= 0 && nc < 7)
                        mask |= 1L << (nr * 7 + nc);
                }
            }
            OUTLINES[i] = mask;
        }

        // Precomputing win masks
        for (int i = 0; i < 49; i++) {
            int row = i / 7;
            int col = i % 7;
            List<Long> masks = new ArrayList<>();

            int[][] directions = {
                    {0, 1},   // horizontal
                    {1, 0},   // vertical
                    {1, 1},   // diagonal down-right
                    {1, -1},  // diagonal down-left
            };

            for (int[] dir : directions) {
                long line = 0L;
                // Walk forward
                for (int step = 0; step < 7; step++) {
                    int r = row + dir[0] * step;
                    int c = col + dir[1] * step;
                    if (r < 0 || r > 6 || c < 0 || c > 6) break;
                    line |= 1L << (r * 7 + c);
                }
                // Walk backward
                for (int step = 1; step < 7; step++) {
                    int r = row - dir[0] * step;
                    int c = col - dir[1] * step;
                    if (r < 0 || r > 6 || c < 0 || c > 6) break;
                    line |= 1L << (r * 7 + c);
                }

                // Slide a window of 4 along the line
                List<Integer> indices = new ArrayList<>();
                for (int b = 0; b < 49; b++) {
                    if ((line & (1L << b)) != 0) indices.add(b);
                }

                for (int start = 0; start <= indices.size() - 4; start++) {
                    long window = 0L;
                    boolean containsI = false;
                    for (int w = 0; w < 4; w++) {
                        int idx = indices.get(start + w);
                        window |= 1L << idx;
                        if (idx == i) containsI = true;
                    }
                    if (containsI) masks.add(window);
                }
            }

            WIN_MASKS[i] = masks.stream().mapToLong(Long::longValue).toArray();
        }
    }

    public List<Move> moves(GameState gameState) {
        int lastMoveIndex = gameState.getLastMoveIndex();

        // If board is empty we can play any move
        if (lastMoveIndex == -1) return extractMoves(Long.MAX_VALUE);

        long occupied = gameState.first() | gameState.second();
        long legalMoves = OUTLINES[lastMoveIndex] & ~occupied;

        // Return moves that are adjacent to the last move
        if (legalMoves != 0) return extractMoves(legalMoves);

        // If we reach dead end, we can play next move
        // anywhere else that is adjacent to the opponent
        long opponent = gameState.getLastMoveBitboard();
        long opponentOutline = 0L;

        // todo: possible optimization here, precompute
        for (int i = 0; i < 49; i++) {
            if ((opponent & 1L) > 0) {
                opponentOutline |= OUTLINES[i];
            }
            opponent >>= 1;
        }

        legalMoves = opponentOutline & ~occupied;
        return extractMoves(legalMoves);
    }

    /**
     * Evaluates the current engine gameState.
     * <p>
     * Only checks the last move for a win — no earlier moves need re-checking.
     *
     * @param gameState the gameState to evaluate
     * @return engine outcome:
     * <table border="1" cellpadding="4">
     *     <tr><th>Value</th><th>Meaning</th></tr>
     *     <tr><td>1</td><td>First player wins</td></tr>
     *     <tr><td>2</td><td>Second player wins</td></tr>
     *     <tr><td>-1</td><td>Stalemate (48 cells filled, no winner)</td></tr>
     *     <tr><td>0</td><td>Game still in progress</td></tr>
     * </table>
     */
    public int outcome(GameState gameState) {
        int lastIndex = gameState.getLastMoveIndex();
        if (lastIndex == -1) return 0;

        long lastPlayer = gameState.getLastMoveBitboard();

        for (long mask : WIN_MASKS[lastIndex]) {
            if ((lastPlayer & mask) == mask) {
                return (gameState.metadata() & 0x80) != 0 ? 2 : 1;
            }
        }

        if (Long.bitCount(gameState.first() | gameState.second()) == 48) return -1;

        return 0;
    }


    private List<Move> extractMoves(long bitboard) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < 49; i++) {
            if ((bitboard & 1L) > 0) {
                moves.add(new Move(i / 7, i % 7));
            }
            bitboard >>= 1;
        }
        return moves;
    }

}
