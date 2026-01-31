package dev.madlador;

public class Main {
    public static void main(String[] args) {


        State state = new State();
        state.occupy(1, 1);
        state.occupy(1, 2);
        state.occupy(1, 3);



        System.out.println(state);


    }
}

class State {

    public final String RESET = "\u001B[38;5;0m";
    public final String BLUE = "\u001B[38;5;12m";
    public final String MAGENTA = "\u001B[38;5;5m";
    public static final String TILE = "⬛ ";

    private boolean isBlueNext = true;
    private long blue = 0;
    private long pink = 0;

    public void occupy(int row, int col) {
        // todo: should check and make sure it is not out of bounds for 7x7 board

        int bitIndex = row * 7 + col;

        if (isBlueNext) blue = blue | 1L << bitIndex;
        else pink = pink | 1L << bitIndex;

        isBlueNext = !isBlueNext;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 49; i++) {
            long mask = 1L << i;


            if ((mask & blue) > 0) {
                sb.append(BLUE);
                sb.append(TILE);
                if ((i + 1) % 7 == 0) sb.append("\n");
                continue;
            }

            if ((mask & pink) > 0) {
                sb.append(MAGENTA);
                sb.append(TILE);
                if ((i + 1) % 7 == 0) sb.append("\n");
                continue;
            }

            sb.append(RESET);
            sb.append(TILE);
            if ((i + 1) % 7 == 0) sb.append("\n");
        }

        return sb.toString();
    }
}