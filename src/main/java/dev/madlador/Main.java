package dev.madlador;

import java.util.*;

public class Main {
    public static void main(String[] args) {


        Move[] moves = new Move[]{
                new Move(0, 2),
                new Move(0, 3),
                new Move(1, 3),
                new Move(1, 2),
                new Move(2, 3),
                new Move(3, 2),
                new Move(2, 1),
                new Move(1, 1),
                new Move(1, 0),
                new Move(0, 0),
        };

        State state = States.place(moves[0]);
        for (int i = 1; i < moves.length; i++) {
            state = States.place(state, moves[i]);
        }

        States.dump(state);
        States.moves(state);


    }
}
