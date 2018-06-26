package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import java.io.Serializable;

/**
 * Ship Container - needed for being unique
 */
public class ShipContainer implements Serializable {
    private BasicShip ship;
    private int row;
    private int col;
    private int currentLength;

    //the coordinates of a random field on the ship
    private int rowCheating;
    private int colCheating;

    public BasicShip getShip() {
        return ship;
    }

    public void setShip(BasicShip ship) {
        this.ship = ship;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(int currentLength) {
        this.currentLength = currentLength;
    }

    public int getRowCheating() {
        return rowCheating;
    }

    public void setRowCheating(int rowCheating) {
        this.rowCheating = rowCheating;
    }

    public int getColCheating() {
        return colCheating;
    }

    public void setColCheating(int colCheating) {
        this.colCheating = colCheating;
    }
}