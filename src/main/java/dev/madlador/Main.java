package dev.madlador;

import dev.madlador.game.Engine;
import dev.madlador.game.Move;
import dev.madlador.game.State;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Engine engine = new Engine();

        State state = State.emptyState();
        List<Move> moves;
        while (!(moves = engine.legalMoves(state)).isEmpty()) {
            state = state.transition(moves.get(0));
            state.dump();

            Thread.sleep(1000);
        }


    }
}
