package com.chess.piece;

import com.chess.board.Board;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{

    public Knight(String color, int row, int col) {
        super(color, row, col);
        this.type = "KNIGHT";
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
        List<int[]> direction = new ArrayList<>();
        //  different L-shaped patterns
        direction.add(new int[]{2,1});
        direction.add(new int[]{2,-1});
        direction.add(new int[]{-2,-1});
        direction.add(new int[]{-2,1});
        direction.add(new int[]{-1,-2});
        direction.add(new int[]{1,-2});
        direction.add(new int[]{-1,2});
        direction.add(new int[]{1,2});

        int[] pinDir = board.getRules().getPinDirection(board, this);   // get the pin direction
        // If not pinned, pinDir is null, and the pawn can move normally
        boolean isPinned = pinDir != null;

        for(int[] dir : direction) {

            int newRow = row +  dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {

                Piece piece = board.getPiece(newRow, newCol);
                if(piece == null){
                    if(!isPinned || (pinDir[0] == dir[1] && pinDir[1] == dir[0])){
                        validMoves.add(new int[]{newRow, newCol});
                    }
                }else {

                    if(!piece.getColor().equals(this.color)){   // can capture if opponent piece is there
                        if(!isPinned || (pinDir[0] == dir[1] && pinDir[1] == dir[0])){
                            validMoves.add(new int[]{newRow, newCol});
                        }
                    }
                }
            }
        }
        return validMoves;
    }
}
