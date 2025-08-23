package com.chess.game;

import com.chess.board.Board;
import com.chess.input.MouseController;

import javax.swing.*;
import java.awt.*;

import static com.chess.config.Config.*;

public class Game {

    private JFrame game_window;
//    public Board board;
    private GameManager gameManager;

    public Game() {

        Board board = new Board();

        this.gameManager = new GameManager(board);

        board.setGameManager(gameManager);

        MouseController mouseController = new MouseController(board, gameManager);
        board.addMouseListener(mouseController);

        initializeUI(board);
    }

    private void initializeUI(Board board) {

        game_window = new JFrame("Chess");
        game_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game_window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);  // Set the window size
        game_window.setLocationRelativeTo(null);
        game_window.setResizable(false);

        JMenuBar bar = new JMenuBar();

        // Create "Game" menu
        JMenu gameMenu = new JMenu("Game");

        // Restart/New Game option
        JMenuItem newGameItem = new JMenuItem("Restart");
        // Back/Home option
        JMenuItem homeItem = new JMenuItem("Home");
        //  exit
        JMenuItem exitItem = new JMenuItem("Exit");
        // Add items to the Game menu
        gameMenu.add(newGameItem);
        gameMenu.add(homeItem);
        gameMenu.add(exitItem);
        bar.add(gameMenu);
        game_window.setJMenuBar(bar);

        JPanel mainPanel = new JPanel();
//        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setBackground(new Color(175,122,95));
        mainPanel.setBounds(0,0, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainPanel.setLayout(new GridBagLayout());

        //  chessMain: Holds player cards and board
        JPanel chessPanel = new JPanel(new GridBagLayout());
        chessPanel.setBackground(new Color(175, 122, 95));

        //  grid bag constraints ; spacing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new  Insets(0, 0, 8, 0);

        //  B - Player card (always the opponent)
        JPanel player2Card = new JPanel(new BorderLayout());
        player2Card.setPreferredSize(new Dimension(200, 30));
        JLabel player2Label = new JLabel();
        player2Label.setText("Player8180");
        player2Label.setFont(new Font("Arial", Font.PLAIN, 18));
        player2Card.add(player2Label, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;  // No horizontal growth
        gbc.weighty = 0;  // No vertical growth
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        chessPanel.add(player2Card, gbc);
        //  A - Player actions (end game, new game available only for host) for avoiding mistake clicks
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(200, 30));
        JButton endGameButton = new JButton("End Game");
        buttonPanel.add(endGameButton, BorderLayout.WEST);
        JButton newGameButton = new JButton("New Game");
        buttonPanel.add(newGameButton, BorderLayout.EAST);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;  // No horizontal growth
        gbc.weighty = 0;  // No vertical growth
        gbc.fill = GridBagConstraints.NONE;  // Prevent stretching
        gbc.anchor = GridBagConstraints.EAST;  // Align to the right
        chessPanel.add(buttonPanel, gbc);

//        endGameButton.addActionListener();
//        newGameButton.addActionListener();

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

        //  A - Player card (always the owner)
        JPanel player1Card = new JPanel(new BorderLayout());
        player1Card.setPreferredSize(new Dimension(200, 30));
        JLabel player1Label = new JLabel();
        player1Label.setText("Player2000(You)");
        player1Label.setFont(new Font("Arial", Font.PLAIN, 18));
        player1Card.add(player1Label, BorderLayout.EAST);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;  // No horizontal growth
        gbc.weighty = 0;  // No vertical growth
        gbc.fill = GridBagConstraints.NONE;  // Prevent stretching
        gbc.anchor = GridBagConstraints.EAST;  // Align to the left
        gbc.insets = new Insets(0,0,0,0);
        chessPanel.add(player1Card, gbc);

        mainPanel.add(chessPanel);
        game_window.add(mainPanel);

        // Show the window
        game_window.setVisible(true);
    }

}
