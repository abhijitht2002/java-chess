package com.chess;

import com.chess.board.Board;
import com.chess.game.GameManager;
import com.chess.game.multiplayer.local.LocalMultiplayerHandler;
import com.chess.ui.*;

import javax.swing.*;
import java.awt.*;

import static com.chess.config.Config.WINDOW_HEIGHT;
import static com.chess.config.Config.WINDOW_WIDTH;

public class MainFrame extends JFrame implements MenuListener{
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private LocalMultiplayerHandler localMultiplayerHandler; // Multiplayer handler
    private Board board;
    private GameManager gameManager;

    public MainFrame(){

        setTitle("ChessPrototype");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Set up CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Initialize UI screens
        contentPanel.add(new MainMenu(this), "MainMenu");

        // Initialize Board & GameManager
        board = new Board();
        gameManager = new GameManager(board);
        gameManager.setMainFrame(this);

        // Initialize LocalMultiplayerHandler with Board & GameManager
        localMultiplayerHandler = new LocalMultiplayerHandler(board, gameManager);

        gameManager.setLocalMultiplayerHandler(localMultiplayerHandler);

        contentPanel.add(new LocalSetupUI(this, localMultiplayerHandler), "LocalSetup");

        contentPanel.add(new LocalGameUI(this, localMultiplayerHandler, board, gameManager), "LocalGame");

        add(contentPanel);

        setVisible(true);
    }

    @Override
    public void onGameStart(String mode) {
        System.out.println("mode: " + mode);
        switch (mode) {

            case "Home":
                cardLayout.show(contentPanel, "MainMenu");
                break;
            case "Local":
                cardLayout.show(contentPanel, "LocalSetup");
                break;
            case "LocalGame":
                cardLayout.show(contentPanel, "LocalGame");
                break;

            default:
                System.out.println("Wrong Selection!!!");
        }
    }
}
