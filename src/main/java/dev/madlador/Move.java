package dev.madlador;

public record Move(int row, int col) {

    public Move {
        if (row < 0 || row >= 7 || col < 0 || col >= 7)
            throw new IllegalArgumentException("Move out of bounds: (" + row + ", " + col + ") expected 0-6");
    }

    int toBitIndex() {
        return row * 7 + col;
    }

    long toMask() {
        return 1L << toBitIndex();
    }

}
