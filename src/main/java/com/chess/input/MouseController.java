package com.chess.input;

import com.chess.board.Board;
import com.chess.game.Game;
import com.chess.game.GameManager;
import com.chess.game.Player;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.chess.config.Config.SQUARE_WIDTH;

public class MouseController extends MouseAdapter {

    private Board board;
    private Game game;
    private Player player;
    private GameManager gameManager;

    public MouseController(Board board, GameManager gameManager){
        this.board = board;
        this.gameManager = gameManager;
    }

    public void mousePressed(MouseEvent event){

        System.out.println(gameManager.getCurrentPlayer());

        //  returns mouse clicked coordinates
        int x = event.getX();
        int y = event.getY();

        //  converting pixel coordinates to grid coordinates i.e., row = getx() / square size for eg., 8x8 grid => 800x800 i.e., one square 100 so row = getX()/100
        int row = y / SQUARE_WIDTH;
        int col = x / SQUARE_WIDTH;

//        System.out.println("LocationX: " + x + ", LocationY: " + y);
//        System.out.println("row: " + row + ", col: " + col);

        //  out of bound check
        if(x > 0 && y > 0 && x < 400 && y < 400){
            if(event.getButton() == MouseEvent.BUTTON1){    //  left button action
                gameManager.processClick(row, col);
            }
        }else {
//            System.out.print("OutOfBounds!");
        }
    }
}
//  note: here we are dealing with board panel so must be careful with the offsets there,
//  if no offset the starting will be location (0,0) itself
//  so don't care about the total dimension of the window just focus on the dealing panel i.e., board here