package com.chess.utils;

import com.chess.piece.*;

public class Utils {

    private static final String[] COLUMNS = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final String[] ROWS = {"8", "7", "6", "5", "4", "3", "2", "1"};

    //  convert grid position to (row, col) to standard chess notation (A1, B5, .....)
    public static String getChessNotation(int row, int col){

        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            System.out.println("illegal board position found: (" + row + ", " + col + ")");
        }

        return COLUMNS[col] + ROWS[row];
    }

    public static void initializeTestPieces(Piece[][] board){

        board[0][7] = new King("BLACK", 0, 7);
        board[6][1] = new King("WHITE", 6, 1);
//        board[6][2] = new Bishop("WHITE", 6, 2);
//        board[6][3] = new Pawn("WHITE", 6, 3);
        board[7][0] = new Queen("WHITE", 7, 0);
//        board[1][0] = new Bishop("BLACK", 1, 0);
        board[4][0] = new Rook("WHITE", 4, 0);
        board[0][0] = new Rook("BLACK", 0, 0);
//        board[7][5] = new Knight("WHITE", 7, 5);

    }
}
