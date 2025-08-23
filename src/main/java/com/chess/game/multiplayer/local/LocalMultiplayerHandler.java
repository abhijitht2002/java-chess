package com.chess.game.multiplayer.local;

import com.chess.board.Board;
import com.chess.game.GameManager;
import com.chess.piece.King;
import com.chess.piece.Piece;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LocalMultiplayerHandler extends Thread{
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private GameManager gameManager;
    private Board board;
    private boolean isHost; // True if this app is the host
    private String hostColor;
    private String clientColor;
    private String myColor;
    private JDialog checkMateDialog;
    private JDialog staleMateDialog;

    public LocalMultiplayerHandler(Board board, GameManager gameManager) {
        this.board = board;
        this.gameManager = gameManager;
    }

    public void startGame(Socket socket, String hostName, String clientName, String hostColor, boolean isHost){
        this.socket = socket;
        this.isHost = isHost;
        this.hostColor = hostColor;
        this.clientColor = hostColor.equals("WHITE") ? "BLACK" : "WHITE";

        // Assign correct colors
        String clientColor = hostColor.equals("WHITE") ? "BLACK" : "WHITE";

        // Initialize GameManager when the game starts

        // Determine who gets which color
        if (hostColor.equals("WHITE")) {
            gameManager.setUpPlayer(hostName, "WHITE", clientName, "BLACK", isHost, hostColor);
        } else {
            gameManager.setUpPlayer(clientName, "WHITE", hostName, "BLACK", isHost, hostColor);
        }

        System.out.println("GameManager initialized. Players set up.");
        System.out.println("is this screen host: " + isHost);

        System.out.println("Host color: " + hostColor);
        System.out.println("Client color: " + clientColor);

        // Determine this player's color
        this.myColor = isHost ? hostColor : clientColor;
        System.out.println("My colour: " + myColor);

        start();    // Start listening for moves
    }

    @Override
    public void run(){

        try {

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            System.out.println("LocalMultiplayerHandler: Game started! ");

            while (true){
                String move = input.readUTF();  // Receive move
//                System.out.println("Receive move: " + move);
//                processMove(move);
                receiveMove(move);
            }


        }catch (IOException e){
            System.err.println("Connection lost: " + e.getMessage());
        }finally {
            closeConnection();
        }
    }

    public void sendMove(String move){
        if (output == null) {
//            System.out.println("Error: Output stream not initialized.");
            return;
        }

//        String[] moveParts = move.split(",");
//        int fromRow = Integer.parseInt(moveParts[0]);
//        int fromCol = Integer.parseInt(moveParts[1]);
//
//        Piece movingPiece = board.getPiece(fromRow, fromCol);
//
//        if (movingPiece == null) {
//            System.out.println("Error: No piece found at source position.");
//            return;
//        }
//
//        // Validate move before sending
//        if (!movingPiece.getColor().equals(myColor)) {
//            System.out.println("Invalid Move! You are " + myColor + " but tried to move " + movingPiece.getColor());
//            return; // Stop here if invalid
//        }

        try {
            System.out.println("Sending move: " + move);
            output.writeUTF(move);
            output.flush();
        }catch (IOException e){
            System.err.println("Failed to send move: " + e.getMessage());
        }
    }

    public void receiveMove(String moveData) {

        String[] parts = moveData.split(",");

        switch (parts[0]) {
            case "MOVE":
                int fromRow = Integer.parseInt(parts[1]);
                int fromCol = Integer.parseInt(parts[2]);
                int toRow = Integer.parseInt(parts[3]);
                int toCol = Integer.parseInt(parts[4]);
                Piece movingPiece = board.getPiece(fromRow, fromCol);
                board.movePiece(movingPiece, toRow, toCol);
                gameManager.switchPlayer();
                break;

            case "CHECK":
                int kingRow = Integer.parseInt(parts[1]);
                int kingCol = Integer.parseInt(parts[2]);
                boolean isCheck = Boolean.parseBoolean(parts[3]);
                board.updateKingSquare(isCheck ? (King) board.getPiece(kingRow, kingCol) : null);
                System.out.println("CHECK! Your king is in danger.");
                break;

            case "CHECKMATE":

                System.out.println("CHECKMATE! Game Over. " + parts[1] + " wins.");
                showCheckmateDialog(parts[1]);
                break;

            case "STALEMATE":
                System.out.println("STALEMATE! Game ends in a draw.");
                showStalemateDialog();
                break;

            case "NEWMATCH":
                System.out.println("new game started!");

                checkMateDialog.dispose();

                // Close any existing dialogs
//                if (checkMateDialog != null && checkMateDialog.isVisible()) {
//                    checkMateDialog.dispose();  // Close checkmate dialog if it's open
//                }
//                if (staleMateDialog != null && staleMateDialog.isVisible()) {
//                    staleMateDialog.dispose();  // Close stalemate dialog if it's open
//                }

                gameManager.refreshGame();
                break;

        }
    }

    public void processMove(String move){
        //  handle moves logic
        System.out.println("Processing move: " + move);

        String[] moveParts = move.split(",");
        int fromRow = Integer.parseInt(moveParts[0]);
        int fromCol = Integer.parseInt(moveParts[1]);
        int toRow = Integer.parseInt(moveParts[2]);
        int toCol = Integer.parseInt(moveParts[3]);

        Piece movingPiece = board.getPiece(fromRow, fromCol);

        if (movingPiece == null) {
            System.out.println("Error: No piece found at source position.");
            return;
        }

        // Get the color of the moving piece
        String pieceColor = movingPiece.getColor();
        // Determine opponent's color (since we're processing their move)
        String opponentColor = isHost ? clientColor : hostColor;
        // Validate that the opponent's move is legitimate
        if (!pieceColor.equals(opponentColor)) {
            System.out.println("Invalid move received! Opponent is " + opponentColor + " but move tried to use " + pieceColor);
            return;
        }

        System.out.println("process opponent move from: " + fromRow + "," + fromCol + " to: " + toRow + "," + toCol);
        // Get the piece and move it

        board.movePiece(movingPiece, toRow, toCol);
        gameManager.switchPlayer(); // Switch turn after move

    }

    private void closeConnection(){
        try {
            if(socket != null)  socket.close();
            if(input != null)   input.close();
            if(output != null)  output.close();
        }catch (IOException e){
            System.out.println("Error Closing Connection: " + e.getMessage());
        }
    }

    public void showCheckmateDialog(String winner) {
        SwingUtilities.invokeLater(() -> {
            checkMateDialog = new JDialog(gameManager.getMainFrame(), "Game Over", true);
            JOptionPane.showMessageDialog(checkMateDialog,
                    "CHECKMATE! " + winner + " wins.",
                    "Game Over",
                    JOptionPane.PLAIN_MESSAGE);
            checkMateDialog.setVisible(true);
        });
    }

    public void showStalemateDialog() {
        SwingUtilities.invokeLater(() -> {
            staleMateDialog = new JDialog(gameManager.getMainFrame(), "Gameover", true);
            JOptionPane.showMessageDialog(staleMateDialog,
                    "STALEMATE! Game ends in a draw.",
                    "Game Over",
                    JOptionPane.PLAIN_MESSAGE);
        });
    }

}
