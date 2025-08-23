package com.chess.piece;

import com.chess.board.Board;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece{

    public Queen(String color, int row, int col) {
        super(color, row, col);
        this.type = "QUEEN";
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Board board) {
        List<int[]> validMoves = getValidMoves(board);
        for (int[] move : validMoves){
            if(move[0] == newRow && move[1] == newCol){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<int[]> getValidMoves(Board board) {

        List<int[]> validMoves = new ArrayList<>();
        validMoves.addAll(straightMovement(board));
        validMoves.addAll(diagonalMovement(board));

        int[] pinDir = board.getRules().getPinDirection(board, this);   // get the pin direction
        // If not pinned, pinDir is null, and the pawn can move normally
        boolean isPinned = pinDir != null;

        if(isPinned){

            // Filter moves to keep only those along the pin direction
            validMoves.removeIf(move ->     // removeIF filter without the use iterator or looped removal
                    (move[0] - row) * pinDir[1] != (move[1] - col) * pinDir[0]
            );
        }

        return validMoves;
    }

}
