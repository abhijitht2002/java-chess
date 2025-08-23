package com.chess.piece;

import com.chess.board.Board;
import com.chess.game.Rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class King extends Piece{

    public King(String color, int row, int col) {
        super(color, row, col);
        this.type = "KING";
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
        List<int[]> directions = new ArrayList<>();
        directions.addAll(Arrays.asList(
                new int[]{1, 1}, new int[]{-1, -1}, new int[]{1, -1}, new int[]{-1, 1},
                new int[]{1, 0}, new int[]{-1, 0}, new int[]{0, 1}, new int[]{0, -1}
        ));

        // Find the opponent's king
        King opponentKing = board.getOpponentKing(this.color);
        int opponentRow = opponentKing.getRow();
        int opponentCol = opponentKing.getCol();

        for(int[] dir : directions){
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if(!isOutOfBounds(newRow, newCol)){

                Piece targetSquare = board.getPiece(newRow, newCol);
                if(targetSquare == null || !targetSquare.getColor().equals(this.color)){

                    board.simulateMove(row, col, newRow, newCol, this);

                    boolean isSafe = !board.getRules().isCheck(board, newRow, newCol, this.color);

                    board.revertSimulation(row, col, newRow, newCol, this, targetSquare);  // put back taken piece

//                    if(isSafe){
//                        validMoves.add(new int[]{newRow, newCol});
//                    }

                    if (isSafe && !(Math.abs(newRow - opponentRow) <= 1 && Math.abs(newCol - opponentCol) <= 1)) {  // preventing king-king check or capture
                        validMoves.add(new int[]{newRow, newCol});
                    }
                }
            }
        }
        return validMoves;
    }

}
