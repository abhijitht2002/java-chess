package com.chess.game;

public enum PieceColor {
    WHITE,
    BLACK;

    public PieceColor getOpponentColor(){
        return this == WHITE ? BLACK : WHITE;
    }
}
