package com.chess.board;

import com.chess.audio.SoundPlayer;
import com.chess.piece.*;
import com.chess.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chess.config.Config.*;

public class BoardGenerator {

    private Piece[][] board;
    private Map<String, BufferedImage> pieceImages;
    private List<int[]> highlightMoves = new ArrayList<>();

    private int kingCheckRow;
    private int kingCheckCol;
    private boolean isKingInCheck = false;

    public BoardGenerator(int row, int col) {
        board = new Piece[row][col];
        initializePieces();
//        Utils.initializeTestPieces(board);
        pieceImages = new HashMap<>();
        SoundPlayer.preloadSounds();
        loadPieceImages();
//        printBoard();
    }

    public void initializePieces(){

        //  black pieces
        //  back row
        board[0][0] = new Rook("BLACK", 0, 0);
        board[0][1] = new Knight("BLACK", 0, 1);
        board[0][2] = new Bishop("BLACK", 0, 2);
        board[0][3] = new Queen("BLACK", 0, 3);
        board[0][4] = new King("BLACK", 0, 4);
        board[0][5] = new Bishop("BLACK", 0, 5);
        board[0][6] = new Knight("BLACK", 0, 6);
        board[0][7] = new Rook("BLACK", 0, 7);
        //  front row
        // pawn
        for(int i = 0; i < 8 ; i++){
            board[1][i] = new Pawn("BLACK", 1, i);
        }

        //  white pieces
        //  back row
        board[7][0] = new Rook("WHITE", 7, 0);
        board[7][1] = new Knight("WHITE", 7, 1);
        board[7][2] = new Bishop("WHITE", 7, 2);
        board[7][3] = new Queen("WHITE", 7, 3);
        board[7][4] = new King("WHITE", 7, 4);
        board[7][5] = new Bishop("WHITE", 7, 5);
        board[7][6] = new Knight("WHITE", 7, 6);
        board[7][7] = new Rook("WHITE", 7, 7);
        //  front row
        //  white pawns
        for(int i = 0; i < 8 ; i++){
            board[6][i] = new Pawn("WHITE", 6, i);
        }
    }

    public void refreshBoard(){ // refresh the board
        clearBoard();
        initializePieces();
    }

    public void loadPieceImages(){

        try{

            //  black pieces
            pieceImages.put("BLACK_QUEEN", ImageIO.read(getClass().getResource("/images/pieces/dqueen.png")));
            pieceImages.put("BLACK_KING", ImageIO.read(getClass().getResource("/images/pieces/dking.png")));
            pieceImages.put("BLACK_BISHOP", ImageIO.read(getClass().getResource("/images/pieces/dbishop.png")));
            pieceImages.put("BLACK_KNIGHT", ImageIO.read(getClass().getResource("/images/pieces/dknight.png")));
            pieceImages.put("BLACK_ROOK", ImageIO.read(getClass().getResource("/images/pieces/drook.png")));
            pieceImages.put("BLACK_PAWN", ImageIO.read(getClass().getResource("/images/pieces/dpawn.png")));
            
            //  white pieces
            pieceImages.put("WHITE_QUEEN", ImageIO.read(getClass().getResource("/images/pieces/lqueen.png")));
            pieceImages.put("WHITE_KING", ImageIO.read(getClass().getResource("/images/pieces/lking.png")));
            pieceImages.put("WHITE_BISHOP", ImageIO.read(getClass().getResource("/images/pieces/lbishop.png")));
            pieceImages.put("WHITE_KNIGHT", ImageIO.read(getClass().getResource("/images/pieces/lknight.png")));
            pieceImages.put("WHITE_ROOK", ImageIO.read(getClass().getResource("/images/pieces/lrook.png")));
            pieceImages.put("WHITE_PAWN", ImageIO.read(getClass().getResource("/images/pieces/lpawn.png")));

        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void generateBoard(Graphics2D g){
        System.out.println("check status: " + isKingInCheck);
        BufferedImage lightSquare = null;
        BufferedImage darkSquare = null;
        BufferedImage redSquare = null;

        //  load the images
        try {
            lightSquare = ImageIO.read(getClass().getResource("/images/lightsquare.png"));
            darkSquare = ImageIO.read(getClass().getResource("/images/darksquare.png"));
            redSquare = ImageIO.read(getClass().getResource("/images/redsquare.png"));
        }catch (IOException e){
            System.out.println("error found: " + e.getMessage());
        }

        int offSetX = 0;
        int offSetY = 0;

        //  draw the chessboard
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {

                int x = j * SQUARE_WIDTH + offSetX;
                int y = i * SQUARE_WIDTH + offSetY;

                if((i + j) % 2 == 0){
                    g.drawImage(lightSquare, x, y, null);
                }
                if((i + j) % 2 == 1){
                    g.drawImage(darkSquare, x, y, null);
                }
                if(isKingInCheck && i == kingCheckRow && j == kingCheckCol){
                    g.drawImage(redSquare, x, y, null);
                }

            }
        }
    }

    public void drawPieces(Graphics2D g) {

        int offSetX = 0, offSetY = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                Piece piece = board[i][j];
//                System.out.print(piece != null ? piece.getPieceName() + " " : "null ");
                if(piece != null) {
                    String pieceName = piece.getPieceName();
                    if (pieceImages.containsKey(pieceName)) {
                        g.drawImage(pieceImages.get(pieceName),
                                j * SQUARE_WIDTH + offSetX,
                                i * SQUARE_HEIGHT + offSetY,
                                SQUARE_WIDTH, SQUARE_HEIGHT,
                                null
                        );
                    }
                }
            }
//            System.out.println();
        }
    }

    public void renderHighlightMoves(Graphics2D g){

//        System.out.println("Repainting board...");
        StringBuilder stringBuilder = new StringBuilder();
//        System.out.print("Highlight moves: ");

        if(highlightMoves == null || highlightMoves.isEmpty()){

            return;
        }

        for (int[] move : highlightMoves){
            stringBuilder.append("[").append(move[0]).append(", ").append(move[1]).append("]");
        }
//        System.out.println(stringBuilder);

        BufferedImage dot = null;

        try{

            dot = ImageIO.read(getClass().getResource("/images/dot.png"));
        }catch (IOException e){
            System.out.println("error: " + e.getMessage());
        }

        if(highlightMoves != null && !highlightMoves.isEmpty()){

            for (int[] move : highlightMoves){

                int x = move[1] * SQUARE_WIDTH;
                int y = move[0] * SQUARE_WIDTH;
                g.drawImage(dot, x, y, null);
            }
        }

//        System.out.println("Highlight: " + highlightMoves);
    }

    public Piece getPiece(int row, int col){
        //  must add row and col out of bounds validation
        return board[row][col];
    }

    public void setPiece(int row, int col, int newRow, int newCol, Piece piece){
        board[newRow][newCol] = piece;
        board[row][col] = null;
    }

    public List<Piece> getAllPieces(){
        List<Piece> pieceList = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Piece piece = board[i][j];
                if(piece != null){
                    pieceList.add(piece);
                }
            }
        }
        return pieceList;
    }

    public List<Piece> getAllPiecesOfColor(String color){
        List<Piece> pieceList = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Piece piece = board[i][j];
                if(piece != null && color != null && piece.getColor().equals(color)){
                    pieceList.add(piece);
                }
            }
        }
        return pieceList;
    }

    public King findKing(String color){
        for (Piece piece : getAllPieces()){
            if((piece instanceof King) && piece.getColor().equals(color)){
                return (King) piece;
            }
        }
        return null;
    }

    public void simulateMove(int row, int col, int newRow, int newCol, Piece piece){
        //  move temporarily
        Piece temp = board[newRow][newCol]; // Save the targeted piece at the new position
        board[newRow][newCol] = piece;    // move the piece to the new position
        board[row][col] = null; // remove piece from old position
    }

    public void revertSimulation(int row, int col, int newRow, int newCol, Piece piece, Piece capturedPiece){
        //  move temporarily
        board[row][col] = piece;    // Move the piece back
        board[newRow][newCol] = capturedPiece;  // Restore the captured piece (if any)
    }

    //  this is a coordinate based movement logic i.e., using board[row][col] to track piece. We can use piece also like movePiece(piece, int newRow, int newCol)
    public void movePiece(int row, int col, int newRow, int newCol){

        board[newRow][newCol] = board[row][col];
        board[row][col] = null;

        highlightMoves.clear();
//        highlightMoves = null;

    }

    public void HighlightValidMoves(List<int[]> validMoves){
        this.highlightMoves = validMoves;
    }

    public void clearHighlights(){
        this.highlightMoves = null;
    }

    public void updateKingSquare(King king){
//        System.out.println("ttrsnow: " + isKingInCheck);
        if(king != null){
            this.kingCheckRow = king.getRow();
            this.kingCheckCol = king.getCol();
            this.isKingInCheck = true;
        }else {
            this.isKingInCheck = false;
        }
    }

    //  private methods
    private void clearBoard(){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++){
                board[i][j] = null; // clear each position
            }
        }
    }
    
    private void printBoard(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

}
