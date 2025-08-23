package com.chess.ui;

import com.chess.board.Board;
import com.chess.game.GameManager;
import com.chess.game.multiplayer.local.LocalMultiplayerHandler;
import com.chess.input.MouseController;

import javax.swing.*;
import java.awt.*;

import static com.chess.config.Config.*;

public class LocalGameUI extends JPanel{

    private MenuListener menuListener;
    private LocalMultiplayerHandler localMultiplayerHandler;
    private Board board;
    private GameManager gameManager;

    public LocalGameUI(MenuListener menuListener, LocalMultiplayerHandler localMultiplayerHandler, Board board, GameManager gameManager) {
        this.menuListener = menuListener;
        this.localMultiplayerHandler = localMultiplayerHandler;
        this.board = board;
        this.gameManager = gameManager;

        // Use the existing board & game manager
        MouseController mouseController = new MouseController(board, gameManager);
        board.addMouseListener(mouseController);

        InitializeUI();
    }

    private void InitializeUI(){

        setBackground(BACKGROUND_COLOUR);
        setBounds(0,0, WINDOW_WIDTH, WINDOW_HEIGHT);
        setLayout(new GridBagLayout());

        //  chessMain: Holds player cards and board
        JPanel chessPanel = new JPanel(new GridBagLayout());
        chessPanel.setBackground(BACKGROUND_COLOUR);

        //  grid bag constraints ; spacing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new  Insets(0, 0, 8, 0);

        //  player2 card - player2 always the opponent
        JPanel player2Card = new JPanel(new BorderLayout());
        player2Card.setPreferredSize(new Dimension(134, 50));
        JLabel player2Label = new JLabel();
        player2Label.setIcon(new ImageIcon(getClass().getResource("/images/ui/player2.png")));
        player2Card.add(player2Label, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;  // No horizontal growth
        gbc.weighty = 0;  // No vertical growth
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        chessPanel.add(player2Card, gbc);
        //  playing indicator for player2
        JPanel playingPanel = new JPanel(new BorderLayout());
        playingPanel.setPreferredSize(new Dimension(75, 20));
        JLabel playingLabel = new JLabel(new ImageIcon(getClass().getResource("/images/ui/playing.png")));
        playingPanel.add(playingLabel, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;  // No horizontal growth
        gbc.weighty = 0;  // No vertical growth
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        chessPanel.add(playingPanel, gbc);

        // adding board to the game window
        // chess board
        board.setPreferredSize(new Dimension(400,400));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;    // Allow horizontal expansion
        gbc.weighty = 1;    // Allow vertical expansion
        gbc.fill = GridBagConstraints.NONE;  // Allow expansion
        gbc.anchor = GridBagConstraints.WEST;  // Center align board
        chessPanel.add(board, gbc);

        //  player1 card - always the current user
        JPanel player1Card = new JPanel(new BorderLayout());
        player1Card.setPreferredSize(new Dimension(134, 50));
        JLabel player1Label = new JLabel();
        player1Label.setIcon(new ImageIcon(getClass().getResource("/images/ui/player1.png")));
        player1Card.add(player1Label, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;  // No horizontal growth
        gbc.weighty = 0;  // No vertical growth
        gbc.fill = GridBagConstraints.NONE;  // Prevent stretching
        gbc.anchor = GridBagConstraints.EAST;  // Align to the left
        gbc.insets = new Insets(0,0,0,0);
        chessPanel.add(player1Card, gbc);
        //  playing indicator for player1
        JPanel playPanel = new JPanel(new BorderLayout());
        playPanel.setPreferredSize(new Dimension(72, 36));
        JLabel playLabel = new JLabel(new ImageIcon(getClass().getResource("/images/ui/play.png")));
        playPanel.add(playLabel, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;  // No horizontal growth
        gbc.weighty = 0;  // No vertical growth
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        chessPanel.add(playPanel, gbc);

        add(chessPanel);
    }
}
