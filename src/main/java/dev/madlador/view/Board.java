package dev.madlador.view;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {


    public Board() {
        setLayout(new GridLayout(7, 7, 8, 8));
        setBackground(new Color(0x171717));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // top, left, bottom, right

        for (int i = 0; i < 49; i++) {
            add(new Cell());
        }
    }



}
