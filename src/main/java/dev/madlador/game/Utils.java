package dev.madlador.game;

public class Utils {

    private static void dump(long bitboard) {
        final String RESET = "\u001B[38;5;0m";
        final String GREEN = "\u001B[38;5;46m";
        final String TILE = "⬛ ";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 49; i++) {
            sb.append((bitboard & (1L << i)) != 0 ? GREEN : RESET);
            sb.append(TILE);
            if ((i + 1) % 7 == 0) sb.append("\n");
        }
        System.out.println(sb);
    }

    private static void dumpIndexed(long bitboard) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 49; i++) {
            sb.append(i).append(" ");
            if ((i + 1) % 7 == 0) sb.append("\n");
        }
        System.out.println(sb);
    }

}
