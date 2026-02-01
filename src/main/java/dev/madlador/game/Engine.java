package dev.madlador.game;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    private static final long[] OUTLINES = new long[49];

    static {
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
    }

    public List<Move> legalMoves(State state) {
        int lastMoveIndex = state.getLastMoveIndex();

        // If board is empty we can play any move
        if (lastMoveIndex == -1) return extractMoves(Long.MAX_VALUE);

        long occupied = state.first() | state.second();
        long legalMoves = OUTLINES[lastMoveIndex] & ~occupied;

        // Return moves that are adjacent to the last move
        if (legalMoves != 0) return extractMoves(legalMoves);

        // If we reach dead end, we can play next move
        // anywhere else that is adjacent to the opponent
        long opponent = state.getLastMoveBitboard();
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
