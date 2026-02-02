package dev.madlador.views;

import dev.madlador.controllers.Controller;
import dev.madlador.models.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SquareView extends JButton {

    public enum PlayerState {
        EMPTY,
        FIRST,
        SECOND,
        FIRST_LAST,
        SECOND_LAST
    }

    private static final int ARC = 14;
    private static final int PADDING = 6;

    private static final Color COLOR_BG          = new Color(0x1E1E1E); // Base background
    private static final Color COLOR_LEGAL       = new Color(0x2A342A); // Dim sage green
    private static final Color COLOR_BORDER      = new Color(0x2A2A2A);
    private static final Color COLOR_BORDER_LEGAL= new Color(0x4A5A4A); // Muted green glow
    private static final Color COLOR_HOVER       = new Color(0x2E2E2E);
    private static final Color COLOR_HOVER_LEGAL = new Color(0x3A443A); // Slightly brighter sage on hover
    private static final Color COLOR_FIRST       = new Color(0x8F2780);
    private static final Color COLOR_FIRST_GLOW  = new Color(0xB84CA0); // Glow for last move
    private static final Color COLOR_SECOND      = new Color(0x27518F);
    private static final Color COLOR_SECOND_GLOW = new Color(0x4A7BBF); // Glow for last move

    private final int row;
    private final int col;
    private PlayerState playerState = PlayerState.EMPTY;
    private boolean isLegalMove = false;

    public SquareView(Controller controller, GameModel gameModel, int row, int col) {
        this.row = row;
        this.col = col;

        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setMinimumSize(new Dimension(58, 58));
        setPreferredSize(new Dimension(58, 58));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> controller.playMove(row, col));
    }

    public void setPlayerState(PlayerState state) {
        this.playerState = state;
        repaint();
    }

    public void setLegalMove(boolean legal) {
        this.isLegalMove = legal;
        repaint();
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    private Color resolveBackgroundColor() {
        if (playerState == PlayerState.EMPTY) {
            if (isLegalMove && getModel().isRollover()) {
                return COLOR_HOVER_LEGAL;
            }
            if (isLegalMove) {
                return COLOR_LEGAL;
            }
            if (getModel().isRollover() && isEnabled()) {
                return COLOR_HOVER;
            }
        }
        return COLOR_BG;
    }

    private Color resolvePieceColor() {
        return switch (playerState) {
            case FIRST, FIRST_LAST  -> COLOR_FIRST;
            case SECOND, SECOND_LAST -> COLOR_SECOND;
            default -> null;
        };
    }

    private Color resolveBorderColor() {
        if (playerState == PlayerState.FIRST_LAST) return COLOR_FIRST_GLOW;
        if (playerState == PlayerState.SECOND_LAST) return COLOR_SECOND_GLOW;
        if (isLegalMove) return COLOR_BORDER_LEGAL;
        return COLOR_BORDER;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape originalClip = g2.getClip();
        RoundRectangle2D outerRound = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2.setClip(outerRound);

        // Draw base background
        g2.setColor(resolveBackgroundColor());
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw piece color with padding if occupied
        Color pieceColor = resolvePieceColor();
        if (pieceColor != null) {
            RoundRectangle2D innerRound = new RoundRectangle2D.Float(
                    PADDING, PADDING,
                    getWidth() - PADDING * 2,
                    getHeight() - PADDING * 2,
                    ARC - PADDING, ARC - PADDING
            );
            g2.setColor(pieceColor);
            g2.fill(innerRound);
        }

        super.paintComponent(g);
        g2.setClip(originalClip);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(resolveBorderColor());

        // If last move, draw thicker glowing border
        if (playerState == PlayerState.FIRST_LAST || playerState == PlayerState.SECOND_LAST) {
            g2.setStroke(new BasicStroke(2.5f));
        }

        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
    }
}