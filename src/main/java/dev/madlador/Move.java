package dev.madlador;

public record Move(int row, int col) {

    int toBitIndex() {
        return row * 7 + col;
    }

    long toMask() {
        return 1L << toBitIndex();
    }

}
