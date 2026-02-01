package dev.madlador.view;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {


    public App() {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        setLayout(new BorderLayout());


        add(new Board(), BorderLayout.CENTER);
        add(new ControlPanel(), BorderLayout.EAST);


        pack();
        setVisible(true);







    }



}
