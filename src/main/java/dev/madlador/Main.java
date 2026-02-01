package dev.madlador;

import dev.madlador.view.App;

public class Main {
    public static void main(String[] args) throws InterruptedException {



        new App();

//
//        for (int i = 0; i < 200; i++) {
//
//            State state = States.place(new Move(3, 3));
//            while (true) {
//                States.dump(state);
//
//                long start = System.currentTimeMillis();
//                List<Move> moves = States.moves(state);
//                long end = System.currentTimeMillis();
//                System.out.println("Time: " + (end - start));
//
//                if  (moves.isEmpty()) {
//                    System.out.println("Stalemate");
//                    break;
//                }
//
//                Collections.shuffle(moves);
//                Move move = moves.getFirst();
//                state = States.place(state, move);
//
//                Thread.sleep(1000);
//            }
//        }
//



    }
}
