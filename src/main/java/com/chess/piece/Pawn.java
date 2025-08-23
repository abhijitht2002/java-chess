package com.chess.piece;

import com.chess.board.Board;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{

    public Pawn(String color, int row, int col) {
        super(color, row, col);
        this.type = "PAWN";
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Board board) {

        for (int[] move : getValidMoves(board)){
            if(move[0] == newRow && move[1] == newCol){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<int[]> getValidMoves(Board board) {

        List<int[]> validMoves = new ArrayList<>();
        int direction = color.equals("BLACK") ? 1 : -1;

        int[] pinDir = board.getRules().getPinDirection(board, this);   // get the pin direction
        // If not pinned, pinDir is null, and the pawn can move normally
        boolean isPinned = pinDir != null;

        //  Basic move: one square forward
        if(board.getPiece(row+direction, col) == null){
            if(!isPinned || (pinDir[0] == direction && pinDir[1] == 0)){
                validMoves.add(new int[]{row+direction, col});
            }
        }
        
        //  double pawn: two square forward at starting
        if((row == 1 && color.equals("BLACK")) || (row == 6 && color.equals("WHITE"))){
            if(board.getPiece(row+direction, col) == null && board.getPiece(row + 2 * direction, col) == null){
                if(!isPinned || (pinDir[0] == direction && pinDir[1] == 0)){
                    validMoves.add(new int[]{row + 2 * direction, col});
                }
            }
        }

        //  capture logic
        //  capture : left diagonal
        //  including out of bounds validation
        if(col > 0 && board.getPiece(row + direction, col - 1) != null && !board.getPiece(row + direction, col - 1).getColor().equals(this.color)){
            if(!isPinned || (pinDir[0] == direction && pinDir[1] == 0)){
                validMoves.add(new int[]{row + direction, col - 1});
            }
        }
        //  capture : right diagonal
        //  including out of bounds validation
        if(col < 7 && board.getPiece(row + direction, col + 1) != null && !board.getPiece(row + direction, col + 1).getColor().equals(this.color)){
            if(!isPinned || (pinDir[0] == direction && pinDir[1] == 0)){
                validMoves.add(new int[]{row + direction, col + 1});
            }
        }

        return validMoves;
    }

}

//  pawn promotion