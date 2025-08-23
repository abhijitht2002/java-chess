package com.chess.game;

import com.chess.audio.SoundPlayer;
import com.chess.board.Board;
import com.chess.game.multiplayer.local.LocalMultiplayerHandler;
import com.chess.piece.King;
import com.chess.piece.Piece;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameManager{

    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player currentPlayer;
    private boolean gameOver = false;
    private boolean isHost;
    private String hostColor;
    private LocalMultiplayerHandler localMultiplayerHandler;
    private JFrame mainFrame;

    private Piece selectedPiece;
    private List<int[]> validMoves = new ArrayList<>();

    public GameManager(Board board) {
        this.board = board;

//        setUpPlayer();
        //  game loop
//        restartGame();
    }

    public void initializeBoard(){

    }

    public void setUpPlayer(String whitePlayerName, String whiteColor, String blackPlayerName, String blackColor, boolean isHost, String hostColor){

        this.isHost = isHost;
        this.hostColor = hostColor;

        if (whitePlayerName == null || whiteColor == null || blackPlayerName == null || blackColor == null) {
            throw new IllegalArgumentException("Error: Player details are missing.");
        }

        // Assign White and Black correctly
        whitePlayer = new Player(whitePlayerName, "WHITE");
        blackPlayer = new Player(blackPlayerName, "BLACK");

        currentPlayer = whitePlayer; // White always starts

        System.out.println("Players set up: ");
        System.out.println("White: " + whitePlayer.getName());
        System.out.println("Black: " + blackPlayer.getName());
        System.out.println("Is a Host: " + isHost);
    }

    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    public void switchPlayer(){
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    //  handles extra move logics
    public void handleMoves(Piece selectedPiece, int newRow, int newCol){

        String opponentColor = hostColor.equals("WHITE") ? "BLACK" : "WHITE";

        // Validate if the correct player is moving
        if ((isHost && !currentPlayer.getPieceColor().equals(hostColor)) ||
                (!isHost && !currentPlayer.getPieceColor().equals(opponentColor))) {
            System.out.println("Move bounced!!! Not your turn!");
            return;
        }

        // to get clicked square
        Piece piece = board.getPiece(newRow, newCol);

        //  validation and initiating move
        if(selectedPiece.isValidMove(newRow, newCol, board)){

            if(piece != null && !piece.getColor().equals(selectedPiece.getColor())){
                SoundPlayer.playSound("CAPTURE_SOUND_CACHE");
            }else {
                SoundPlayer.playSound("MOVE_SOUND_CACHE");
            }


            // Create move data string
            String moveData = "MOVE," + selectedPiece.getRow() + "," + selectedPiece.getCol() + "," + newRow + "," + newCol;

            // Send move to opponent
            localMultiplayerHandler.sendMove(moveData);

            board.movePiece(selectedPiece, newRow, newCol);

            // Check if the opponent king is now under attack
            System.out.println("currentplayer: " + currentPlayer.getPieceColor());
            updateCheckStatus();
            checkForGameEnd(board);

            //  switch turn
            switchPlayer();
        }
    }

    public void processClick(int clickedRow, int clickedCol){

        // If game is over, ignore clicks
        if(gameOver){
            System.out.println("gameOver!!!");
            return;
        }

        Piece clickedPiece = board.getPiece(clickedRow, clickedCol);

        // Check if it's the correct player's turn before anything else
        String opponentColor = hostColor.equals("WHITE") ? "BLACK" : "WHITE";
        if ((isHost && !currentPlayer.getPieceColor().equals(hostColor)) ||
                (!isHost && !currentPlayer.getPieceColor().equals(opponentColor))) {
            System.out.println("Move bounced!!! Not your turn!");
            return; // Stop execution if it's not the player's turn
        }

//        if(isInCheck){
//            return;
//        }

        // Case 1 - Select a piece (first click)
        // If no piece is selected, select a piece
        if(selectedPiece == null){
            if(clickedPiece != null){
                //  if wrong player is player highlightmoves must also crash
                if(!clickedPiece.getColor().equals(currentPlayer.getPieceColor())){
                    return;     // Prevent selecting opponent's piece as the first move
                }

//                if(isInCheck){
//                    System.out.println("its a check dude");
//                }

                selectedPiece = clickedPiece;
                validMoves = selectedPiece.getValidMoves(board);

                if(isCheck(board.findKing(selectedPiece.getColor()))){

                    System.out.println("its a check dude");
                    List<int[]> filteredMoves = filterValidMoves(selectedPiece, validMoves, board);
                    validMoves = filteredMoves;
                }

                board.highlightValidMoves(validMoves);

                StringBuilder stringBuilder = new StringBuilder();
                for(int[] move : validMoves){
                    stringBuilder.append("(").append(move[0]).append(", ").append(move[1]).append("), ");
                }
//                System.out.print("\nValid moves: " + stringBuilder);

            }else {
                System.out.print("\nNo piece found, select a piece");
            }
            return;
        }

        //  Case 2 - Try to move the selected piece
        // If a piece is selected, can move the piece on next click or if invalid move can select a piece again
//        if(selectedPiece.isValidMove(clickedRow, clickedCol, board)) {     // If a piece is selected, can move the piece on next click or if invalid move can select a piece again
//            handleMoves(selectedPiece, clickedRow, clickedCol);     // the execution of moving piece
//            resetSelection();
//        }
        // validMoves based movement validation
        for(int[] move : validMoves){
            if(move[0] == clickedRow && move[1] == clickedCol){
                handleMoves(selectedPiece, clickedRow, clickedCol); // the execution of moving piece, Only if it's in validMoves
                resetSelection();
                return;
            }
        }

        // Case 3 - Clicking on another piece while a piece is selected
        if (clickedPiece != null && clickedPiece.getColor() == getCurrentPlayer().getPieceColor()) {

            //  allow reselection of own piece
            selectedPiece = clickedPiece;
            validMoves = selectedPiece.getValidMoves(board);

            if(isCheck(board.findKing(selectedPiece.getColor()))){

                System.out.println("its a check dude");
                List<int[]> filteredMoves = filterValidMoves(selectedPiece, validMoves, board);
                validMoves = filteredMoves;
            }

            board.highlightValidMoves(validMoves);

        }else {     // Case 4 - Invalid move
            System.out.println("invalid move!");
            resetSelection();
        }

    }

    public void refreshGame(){
        restartGame();
    }

    public void setLocalMultiplayerHandler(LocalMultiplayerHandler handler) {
        this.localMultiplayerHandler = handler;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    //  Private methods
    private void updateCheckStatus(){

        // Find opponent's king dynamically
        String opponentColor = currentPlayer.getPieceColor().equals("WHITE") ? "BLACK" : "WHITE";
        King opponentKing = board.findKing(opponentColor);

        boolean isCheck = isCheck(opponentKing);
        String checkData = "CHECK," + opponentKing.getRow() + "," + opponentKing.getCol() + "," + isCheck;
        localMultiplayerHandler.sendMove(checkData);

        if (isCheck) {
            System.out.println("CHECK! " + opponentColor + " king is in danger.");
            board.updateKingSquare(opponentKing);
        }else {
            board.updateKingSquare(null);
        }

        //  checkmate or stalemate -> gameover
    }

    private boolean isCheck(King king) {
        for (Piece piece : board.getAllPieces()) {
            // If piece belongs to the opponent and can attack the king's square
            if (!piece.getColor().equals(king.getColor()) && piece.isValidMove(king.getRow(), king.getCol(), board)) {
                return true;
            }
        }
        return false;
    }

    //  check resolution system
    private List<int[]> filterValidMoves(Piece piece, List<int[]> moves, Board board){

        if(piece instanceof King){  // King already filters its own moves itself
            return piece.getValidMoves(board);
        }

        List<int[]> filteredMoves = new ArrayList<>();

        for (int[] move : moves){
            int fromRow = piece.getRow();
            int fromCol = piece.getCol();
            int toRow = move[0];
            int toCol = move[1];

            Piece targetPiece = board.getPiece(toRow, toCol);

            //  executing simulation
            board.simulateMove(fromRow, fromCol, toRow, toCol, piece);

            //  Check if the king is safe
            boolean checkReleased = !isCheck(board.findKing(piece.getColor()));

            //  reverse simulation
            board.revertSimulation(fromRow, fromCol, toRow, toCol, piece, targetPiece);

            if(checkReleased){
                filteredMoves.add(move);
            }

        }

        return filteredMoves;
    }

    private void checkForGameEnd(Board board) {

        String opponentColor = currentPlayer == whitePlayer ? "BLACK" : "WHITE";
        Piece king = board.findKing(opponentColor);

        boolean isCheckMate = board.getRules().isCheckmate(board, opponentColor);
//        boolean hasLegalMoves = !rules.getFilteredMoves(board, opponentColor).isEmpty();
        boolean isStalemate = board.getRules().isStalemate(board, opponentColor);

        if (isCheckMate) {

            gameOver = true;
            String winner = currentPlayer.getPieceColor();

            String checkMateData = "CHECKMATE," + winner;
            localMultiplayerHandler.sendMove(checkMateData);
            showCheckmateDialog(winner);

        } else if (isStalemate) {
            gameOver = true;

            String staleMateData = "STALEMATE,";
            localMultiplayerHandler.sendMove(staleMateData);
            showStalemateDialog();
        }
    }


    private void resetSelection(){
        selectedPiece = null;
//        validMoves = null;
        validMoves.clear();
        board.repaint();
        System.out.println("validMoves: " + validMoves);
    }

    // restart the game or start new game
    private void restartGame() {

        currentPlayer = whitePlayer;  // White always starts
        gameOver = false;
        board.updateKingSquare(null);
        board.refreshBoard();
        System.out.println("New game started!");
    }

    public void showCheckmateDialog(String winner) {
        SwingUtilities.invokeLater(() -> {

            int choice = JOptionPane.showOptionDialog(
                    getMainFrame(),
                    "CHECKMATE! " + winner + " wins.",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Rematch", "End Game"},
                    "Rematch"
            );

            if (choice == JOptionPane.YES_OPTION) {
                System.out.println("Rematch selected");
                // TODO: Trigger rematch logic

                String newMatchData = "NEWMATCH,";
                localMultiplayerHandler.sendMove(newMatchData);

                restartGame();

            } else {
                System.out.println("End Game selected");
                System.exit(0);
            }

        });
    }

    public void showStalemateDialog() {
        SwingUtilities.invokeLater(() -> {

            int choice = JOptionPane.showOptionDialog(
                    getMainFrame(),
                    "STALEMATE! Game ends in a draw.",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Rematch", "End Game"},
                    "Rematch"
            );

            if (choice == JOptionPane.YES_OPTION) {
                System.out.println("Rematch selected");
                // TODO: Trigger rematch logic
            } else {
                System.out.println("End Game selected");
                System.exit(0);
            }

        });
    }


}
//  can introduce an interface for decoupling