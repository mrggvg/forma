package dev.madlador;

import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {


        for (int i = 0; i < 100; i++) {

            State state = States.place(new Move(3, 3));
            while (true) {
                States.dump(state);
                List<Move> moves = States.moves(state);

                if  (moves.isEmpty()) {
                    System.out.println("Stalemate");
                    break;
                }

                Collections.shuffle(moves);
                Move move = moves.getFirst();
                state = States.place(state, move);

                Thread.sleep(100);
            }
        }




    }
}
