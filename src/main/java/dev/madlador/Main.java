package dev.madlador;

import dev.madlador.game.Engine;
import dev.madlador.game.Move;
import dev.madlador.game.State;
import dev.madlador.view.App;

import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {



        new App();




    }


    public static void benchmark() {
        Engine engine = new Engine();

        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            State state = State.emptyState();
            List<Move> moves;

            while (!(moves = engine.moves(state)).isEmpty()) {
                Collections.shuffle(moves);
                state = state.transition(moves.getFirst());
                state.dump();

                int result = engine.outcome(state);
                if (result != 0) {
//                    System.out.println(switch (result) {
//                        case 1 -> "Pink wins!";
//                        case 2 -> "Blue wins!";
//                        case -1 -> "Stalemate!";
//                        default -> "";
//                    });
//                    break;
                }
            }

        }

        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start) + "ms");
    }

}
