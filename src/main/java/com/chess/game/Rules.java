package com.chess.game;

import com.chess.board.Board;
import com.chess.piece.*;

import java.util.ArrayList;
import java.util.List;

//  for common rules and functionalities like casteling, check validation, stalemate, checkmate
public class Rules {

    public boolean isCheck(Board board, int kingRow, int kingCol, String kingColor){

        for(int r = 0; r < 8; r++){
            for(int c = 0; c < 8; c++){

                Piece targetSquare = board.getPiece(r, c); // this captures the valid squares of selectedpiece also get if opponent is there

                // If it's an opponent piece and can attack the king's position(i.e., opponents valid position include kings valid positions)
                if(targetSquare != null && !targetSquare.getColor().equals(kingColor) && !(targetSquare instanceof King)){
                    if(targetSquare.isValidMove(kingRow, kingCol, board)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheck(Board board, King king){
        for (Piece piece : board.getAllPieces()) {
            // If piece belongs to the opponent and can attack the king's square
            if (!piece.getColor().equals(king.getColor()) && piece.isValidMove(king.getRow(), king.getCol(), board)) {
                return true;
            }
        }
        return false;
    }

    //  pinned piece logic (ray tracing)
    public boolean isPinned(Board board, Piece piece){
        // Find the king of the same color as the given piece
        King king = findKing(board, piece.getColor());


        int kingRow = king.getRow();
        int kingCol = king.getCol();
        int pieceRow = piece.getRow();
        int pieceCol = piece.getCol();


        //  find direction from king to piece
        int rowDir = Integer.compare(pieceRow, kingRow);
        int colDir = Integer.compare(pieceCol, kingCol);


        // Ensure the piece is in a straight or diagonal line with the king
        if(rowDir == 0 || colDir == 0 || (Math.abs(rowDir) == Math.abs(colDir))){
            boolean foundPiece = false; // our clicked piece
            Piece pinningPiece = null;


            //  start checking from the king's position and move towards the clicked piece
            int r = kingRow + rowDir;
            int c = kingCol + colDir;


            while (!isOutOfBounds(r, c)){   // start ray tracking   :   loop till out of bounds
                Piece current = board.getPiece(r, c);


                if(current != null){
                    if(!foundPiece){
                        if(current == piece){
                            foundPiece = true;  // found our piece first, continue checking
                        }else {
                            break;  // a blocking piece found before our piece, no pin
                        }
                    }else {
                        if(current.getColor().equals(piece.getColor())){
                            break;  // another friendly piece found after our piece, no pin
                        }else {
                            pinningPiece = current;
                            System.out.println("Pin detected: The " + piece.getPieceName() + " is pinned! (note: may be harmless opponent)");
                            break;  // found an opponent piece that might be pinning
                        }
                    }
                }
                //  move to next square
                r += rowDir;
                c += colDir;
            }
            // Confirm if the pinning piece is actually capable of pinning
            if(pinningPiece != null){
                return isPotentialPinner(pinningPiece, rowDir, colDir);
            }
        }
        return false;
    }

    public int[] getPinDirection(Board board, Piece piece){
        if(!isPinned(board, piece)){
            return null;
        }

        King king = findKing(board, piece.getColor());
        int kingRow = king.getRow();
        int kingCol = king.getCol();
        int pieceRow = piece.getRow();
        int pieceCol = piece.getCol();

        // Direction from king to piece
        int rowDir = Integer.compare(pieceRow, kingRow);
        int colDir = Integer.compare(pieceCol, kingCol);

        return new int[]{rowDir, colDir};
    }

    //  checkmate and stalemate system
    public boolean isCheckmate(Board board, String kingColor){

        King king = findKing(board,kingColor);

        if(!isInCheck(board,king))  return false;

        List<Piece> pieces = board.getAllPiecesOfColor(kingColor);
        for (Piece piece : pieces){
            List<int[]> validMoves = piece.getValidMoves(board);
            List<int[]> safeMoves = filterValidMoves(piece, validMoves, board);

            if(!safeMoves.isEmpty())    return false;
        }

        return true;   // No safe moves, and in check
    }

    public boolean isStalemate(Board board, String kingColor){

        King king = findKing(board,kingColor);

        if(isInCheck(board,king))  return false;

        List<Piece> pieces = board.getAllPiecesOfColor(kingColor);
        for (Piece piece : pieces){
            List<int[]> validMoves = piece.getValidMoves(board);
            List<int[]> safeMoves = filterValidMoves(piece, validMoves, board);

            if(!safeMoves.isEmpty())    return false;
        }

        return true;   // No safe moves, and not in check
    }

    //  make a clone of filteredMoves, getAllthe Valid moves of all the pieces - filter it,
    //  and finally check if valid moves are available if not CHECKMATE

    public List<int[]> filterValidMoves(Piece piece, List<int[]> moves, Board board){

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
            boolean checkReleased = !isInCheck(board, board.findKing(piece.getColor()));

            //  reverse simulation
            board.revertSimulation(fromRow, fromCol, toRow, toCol, piece, targetPiece);

            if(checkReleased){
                filteredMoves.add(move);
            }

        }

        return filteredMoves;
    }

    //  private methods
    private King findKing(Board board, String color){
        for (Piece piece : board.getAllPieces()){
            if((piece instanceof King) && piece.getColor().equals(color)){
                return (King) piece;
            }
        }
        return null;
    }

    private boolean isPotentialPinner(Piece piece, int rowDir, int colDir){
        if (piece instanceof Queen) return true;
        if ((piece instanceof Rook) && (rowDir == 0 || colDir == 0)) return true;
        if ((piece instanceof Bishop) && (Math.abs(rowDir) == Math.abs(colDir))) return true;
        return false;
    }

    private boolean isOutOfBounds(int r, int c){
        if(r < 0 || r >= 8 || c < 0 || c >= 8){
            return true;
        }
        return false;
    }

}
