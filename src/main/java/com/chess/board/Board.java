package com.chess.board;

import com.chess.game.*;
import com.chess.piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.chess.config.Config.MOVE_SOUND;

public class Board extends JPanel {

    private BoardGenerator boardGenerator;
    private Game game;
    private GameManager gameManager;
    private Rules rules = new Rules();

    public Board(){

//        this.gameManager = gameManager;
//        this.game = game;

        boardGenerator = new BoardGenerator(8, 8);
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);
//        boardGenerator.drawBoard((Graphics2D) g);
        boardGenerator.generateBoard((Graphics2D) g);
        boardGenerator.drawPieces((Graphics2D) g);
        boardGenerator.renderHighlightMoves((Graphics2D) g);
    }

    public void highlightValidMoves(List<int[]> validMoves){

        boardGenerator.HighlightValidMoves(validMoves);
        repaint();
    }

    public void setGameManager(GameManager gameManager){
        this.gameManager = gameManager;
    }

    public Piece getPiece(int row, int col){
//        return boardGenerator;
        // Expose the necessary functionality without exposing BoardGenerator
        return boardGenerator.getPiece(row, col);
    }

    public List<Piece> getAllPieces(){
        return boardGenerator.getAllPieces();
    }

    public List<Piece> getAllPiecesOfColor(String color){
        return boardGenerator.getAllPiecesOfColor(color);
    }

    public King findKing(String color){
        return boardGenerator.findKing(color);
    }

    public void movePiece(Piece piece, int newRow, int newCol){
        boardGenerator.movePiece(piece.getRow(), piece.getCol(), newRow, newCol);
        piece.setPosition(newRow, newCol);
//        boardGenerator.clearHighlights();
        repaint();
    }

    public void simulateMove(int row, int col, int newRow, int newCol, Piece piece){
        boardGenerator.simulateMove(row, col, newRow, newCol, piece);
    }

    public void revertSimulation(int row, int col, int newRow, int newCol, Piece piece, Piece capturedPiece){
        boardGenerator.revertSimulation(row, col, newRow, newCol,piece, capturedPiece);
    }

    public Rules getRules(){
        return this.rules;
    }

    public King getOpponentKing(String currentKingColor) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = getPiece(r, c);
                if (piece instanceof King && !piece.getColor().equals(currentKingColor)) {
                    return (King) piece; // Return the opponent's king
                }
            }
        }
        return null; // Should never happen in a valid game
    }

    public void updateKingSquare(King king){
        boardGenerator.updateKingSquare(king);
        repaint();
    }

    public void refreshBoard(){ // refresh the board to new
        boardGenerator.refreshBoard();
        repaint();
    }
}
