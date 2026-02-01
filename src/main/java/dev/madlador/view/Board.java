package dev.madlador.view;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {

    private final Cell[] cells = new Cell[49];

    public Board() {
        setLayout(new GridLayout(7, 7, 6, 6));
        setBackground(new Color(0x121212));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        for (int i = 0; i < 49; i++) {
            cells[i] = new Cell();
            add(cells[i]);
        }
    }

    public Cell getCell(int index) {
        return cells[index];
    }

    public Cell getCell(int row, int col) {
        return cells[row * 7 + col];
    }

}