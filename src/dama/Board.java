/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shashki1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import javafx.scene.Group;

/**
 *
 * @author Spike
 */
public class Board implements Serializable{
    private Tile[][] board;
    private ArrayList<Tile> listTile = new ArrayList<>();
    private ArrayList<Piece> listPiece = new ArrayList<>();

    private PieceType nextStep = null;
    public void setListTile(ArrayList<Tile> listTile) {
        this.listTile = listTile;
    }

    public void setListPiece(ArrayList<Piece> listPiece) {
        this.listPiece = listPiece;
    }

    
    
    public Board() {
        board = new Tile[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                listTile.add(tile);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = new Piece(PieceType.RED, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = new Piece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    listPiece.add(piece);
                }
            }
        }
    }

    public Tile[][] getBoard() {
        return board;
    }

    public void setBoard(Tile[][] board) {
        this.board = board;
    }

    @Override
    public int hashCode() {
        int hash = 3 * (int)(Math.random() * 100);
        return hash;
    }

   

    public ArrayList<Tile> getListTile() {
        return listTile;
    }

    public ArrayList<Piece> getListPiece() {
        return listPiece;
    }

    public PieceType getNextStep() {
        return nextStep;
    }

    public void setNextStep(PieceType nextStep) {
        this.nextStep = nextStep;
    }

    
    



    
    
    
    
    
}
