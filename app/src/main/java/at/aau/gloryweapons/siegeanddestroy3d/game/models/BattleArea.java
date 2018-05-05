package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import java.io.Serializable;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BattleArea implements Serializable {

    private int userId;
    private int rows;
    private int columns;
    private BattleAreaTile[][] battleAreaTiles;

    /**
     * Creates a new BattleArea with given playerId and dimensions. All tiles are water.
     *
     * @param userId
     * @param size
     */
    public BattleArea(int userId, int size) {
        this(userId, size, size);
    }

    /**
     * Creates a new BattleArea with given playerId and dimensions. All tiles are water.
     *
     * @param userId
     * @param nRows
     * @param nColumns
     */
    public BattleArea(int userId, int nRows, int nColumns) {
        this.userId = userId;
        this.rows = nRows;
        this.columns = nColumns;
        this.battleAreaTiles = new BattleAreaTile[nRows][nColumns];

        // init tiles with water
        for (int i = 0; i < nRows; ++i)
            for (int j = 0; j < nColumns; ++j)
                this.battleAreaTiles[i][j] = new BattleAreaTile();
    }

    public int getUserId() {
        return userId;
    }

    public int getRowNumber() {
        return rows;
    }

    public int getColumnNumber() {
        return columns;
    }

    public BattleAreaTile[][] getBattleAreaTiles() {
        return battleAreaTiles;
    }

    /**
     * Checks if the ship is inside this BattleArea.
     *
     * @param ship     The ship to check.
     * @param rowStart The row of the start of the ship.
     * @param colStart The column of the start of the ship.
     * @return True, if the ship is completely inside this BattleArea.
     */
    public boolean isShipInBattleArea(BasicShip ship, int rowStart, int colStart) {
        if (ship == null
                || rowStart < 0 || rowStart > this.rows
                || colStart < 0 || colStart > this.columns)
            return false;

        boolean isHorizontal = ship.isHorizontal();
        int shipLength = ship.getLength();

        // check for collision with border
        if ((isHorizontal && this.columns < colStart + shipLength)
                || (!isHorizontal && this.rows < rowStart + shipLength))
            return false;

        return true;
    }

    /**
     * Checks if the desired position of the ship is available, ie. if there is no other ship on some tile.
     *
     * @param ship     The ship to check the position for.
     * @param rowStart The row of the start of the ship.
     * @param colStart The column of the start of the ship.
     * @return True, if every needed tile is water.
     */
    public boolean isShipPositionAvailable(BasicShip ship, int rowStart, int colStart) {
        if (!isShipInBattleArea(ship, rowStart, colStart))
            return false;

        boolean isHorizontal = ship.isHorizontal();
        int shipLength = ship.getLength();

        // check for collision with other ships, more specifically their tiles
        for (int i = 0; i < shipLength; ++i) {
            if (battleAreaTiles[rowStart][colStart].getType() != BattleAreaTile.TileType.WATER)
                return false;

            if (isHorizontal)
                ++colStart;
            else
                ++rowStart;
        }

        return true;
    }

    /**
     * Checks if the ship can be placed at the given position.
     *
     * @param shipToPlace The ship to check.
     * @param rowToPlace  The row of the start of the ship.
     * @param colToPlace  The column of the start of the ship.
     * @return True, if the ship is inside this BattleArea and every tile for the ship is currently water.
     */
    public boolean canShipBePlaced(BasicShip shipToPlace, int rowToPlace, int colToPlace) {
        return isShipInBattleArea(shipToPlace, rowToPlace, colToPlace)
                && isShipPositionAvailable(shipToPlace, rowToPlace, colToPlace);
    }

    /**
     * Places a ship on this BattleArea with given coordinates.
     *
     * @param shipToPlace The ship to place.
     * @param rowToPlace  The row for the start of the ship.
     * @param colToPlace  The column for the start of the ship.
     */
    public void placeShip(BasicShip shipToPlace, int rowToPlace, int colToPlace) throws IllegalArgumentException {
        if (!canShipBePlaced(shipToPlace, rowToPlace, colToPlace))
            throw new IllegalArgumentException("Not a valid configuration. Check BattleArea.canShipBePlaced() first.");

        // place the ship by setting the tiles
        for (int i = 0; i < shipToPlace.getLength(); ++i) {
            // set tile on board
            battleAreaTiles[rowToPlace][colToPlace].setType(BattleAreaTile.TileType.SHIP_HEALTHY);
            // assign tile to the ship
            shipToPlace.getTiles()[i] = battleAreaTiles[rowToPlace][colToPlace];

            // update coordinates for next tile
            if (shipToPlace.isHorizontal())
                ++colToPlace;
            else
                ++rowToPlace;
        }
    }
}
