package dev.madlador.view;

import dev.madlador.controller.Controller;
import dev.madlador.model.GameModel;
import dev.madlador.model.GameModelObserver;

import javax.swing.*;
import java.awt.*;

public class AppView extends JFrame implements GameModelObserver {

    private GameModel gameModel;
    private Controller controller;

    private BoardView boardView;
    private ControlView controlView;

    private JPanel overlay;
    private JLabel overlayLabel;
    private boolean overlayShown = false;

    public AppView(Controller controller, GameModel gameModel) {
        this.controller = controller;
        this.gameModel = gameModel;
        this.gameModel.subscribe(this);

        this.boardView = new BoardView(controller, gameModel);
        this.controlView = new ControlView(controller, gameModel);

        setTitle("4mation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(0x121212));
        setLocationRelativeTo(null);


        add(boardView, BorderLayout.CENTER);
//        add(controlView, BorderLayout.EAST);

        setMinimumSize(new Dimension(380, 280));
        pack();
        setVisible(true);

    }
    public void showOverlay(String message) {
        if (overlay == null) {
            overlay = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(0, 0, 0, 160)); // darken background
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };

            overlay.setOpaque(false);
            overlay.setLayout(new GridBagLayout());

            JPanel box = new JPanel(new BorderLayout(10, 10));
            box.setBackground(new Color(0x1E1E1E));
            box.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

            overlayLabel = new JLabel("", SwingConstants.CENTER);
            overlayLabel.setForeground(Color.WHITE);
            overlayLabel.setFont(overlayLabel.getFont().deriveFont(Font.BOLD, 18f));

            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                overlay.setVisible(false);
                controller.newGame();
            });

            box.add(overlayLabel, BorderLayout.CENTER);
            box.add(okButton, BorderLayout.SOUTH);

            overlay.add(box);

            setGlassPane(overlay);
        }

        overlayLabel.setText(message);
        overlay.setVisible(true);
    }

    @Override
    public void update() {
        int outcome = gameModel.getGameOutcome();

        if (outcome == 0) {
            overlayShown = false;
            return;
        }

        if (overlayShown) return;

        switch (outcome) {
            case -1 -> {
                overlayShown = true;
                showOverlay("Stalemate!");
            }
            case 1 -> {
                overlayShown = true;
                showOverlay("Pink Won!");
            }
            case 2 -> {
                overlayShown = true;
                showOverlay("Blue Won!");
            }
        }
    }


    public BoardView getBoardView() {
        return boardView;
    }

}
