package dev.madlador;

import dev.madlador.game.Move;
import dev.madlador.game.State;

public class Main {
    public static void main(String[] args) throws InterruptedException {


        State state = State.emptyState();
        for (int i = 0; i < 4; i++) {
            state.dump();
            state = State.transition(state, new Move(i, 3));
        }
        state.dump();



    }
}
