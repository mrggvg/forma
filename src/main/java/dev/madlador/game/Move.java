package dev.madlador.game;

public record Move(int row, int col) {

    public Move {
        if (row < 0 || row > 6 || col < 0 || col > 6)
            throw new IllegalArgumentException("Move out of bounds: (" + row + ", " + col + ")");
    }

    int toBitIndex() {
        return row * 7 + col;
    }

    long toMask() {
        return 1L << toBitIndex();
    }

}
