package com.chess.piece;

import com.chess.board.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Piece {

    protected String color;
    protected String type;
    protected int row;
    protected int col;

    public Piece(String color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getPieceName(){
        return color + "_" + type;
    }

    public void setPosition(int row, int col){
        this.row = row;
        this.col = col;
    }

    //  This method validates if a particular move is possible for the piece.
    public abstract boolean isValidMove(int newRow, int newCol, Board board);

    //  This method should return a list of valid moves for a piece so using an array type instead of String is more practical
    public abstract List<int[]> getValidMoves(Board board);

    protected List<int[]> straightMovement(Board board){

        List<int[]> validMoves = new ArrayList<>();
        List<int[]> directions = new ArrayList<>();
        directions.addAll(Arrays.asList(
                new int[]{1, 0},    // down
                new int[]{-1, 0},  // up
                new int[]{0, 1},   // right
                new int[]{0, -1}    // left
        ));

        for (int[] dir : directions){
            //  loop starting from one to avoid unwanted add up of the default position since i = 0, it doesn't do anything
            for (int i = 1; i < 8; i++) {

                int newRow = row + dir[0] * i;
                int newCol = col + dir[1] * i;

                // Stop if out of bounds
                if (isOutOfBounds(newRow, newCol)) break;

                Piece piece = board.getPiece(newRow, newCol);
                if (piece == null) {
                    validMoves.add(new int[]{newRow, newCol});
                } else {

                    if(!piece.getColor().equals(this.color)){
                        //  gets one more move to capture the piece
                        validMoves.add(new int[]{newRow, newCol});
                    }
                    break;
                }
            }
        }
        return validMoves;
    }

    protected List<int[]> diagonalMovement(Board board){

        List<int[]> validMoves = new ArrayList<>();
        List<int[]> directions = new ArrayList<>();
        directions.addAll(Arrays.asList(
            new int[]{1, 1},    // Diagonal ↘
            new int[]{-1, -1},  // Diagonal ↖
            new int[]{1, -1},   // Diagonal ↙
            new int[]{-1, 1}    // Diagonal ↗
        ));

        for (int[] dir : directions){
            //  loop starting from one to avoid unwanted add up of the default position since i = 0, it doesn't do anything
            for (int i = 1; i < 8; i++) {

                int newRow = row + dir[0] * i;
                int newCol = col + dir[1] * i;

                // Stop if out of bounds
                if (isOutOfBounds(newRow, newCol)) break;

                Piece piece = board.getPiece(newRow, newCol);
                if (piece == null) {
                    validMoves.add(new int[]{newRow, newCol});
                } else {

                    if(!piece.getColor().equals(this.color)){
                        //  gets one more move to capture the piece
                        validMoves.add(new int[]{newRow, newCol});
                    }
                    break;
                }
            }
        }
        return validMoves;
    }

    protected boolean isOutOfBounds(int row, int col){
        return row < 0 || row >= 8 || col < 0 || col >=8;
    }
}
