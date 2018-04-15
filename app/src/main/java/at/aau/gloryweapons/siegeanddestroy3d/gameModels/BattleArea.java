package at.aau.gloryweapons.siegeanddestroy3d.gameModels;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BattleArea {

    private int userId;
    private int rows;
    private int columns;
    private BattleAreaTile[][] battleAreaTiles;

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
     * Places a ship on this BattleArea with given coordinates.
     *
     * @param shipToPlace The ship to place.
     * @param row         The row for the start of the ship.
     * @param col         The column for the start of the ship.
     */
    public void placeShip(BasicShip shipToPlace, int row, int col) throws IllegalArgumentException {
        // TODO: check collision with other ships and board bounds

        // place the ship by setting the tiles
        for (int i = 0; i < shipToPlace.getLength(); ++i) {
            // set tile on board
            battleAreaTiles[row][col].setType(BattleAreaTile.TileType.ShipHealthy);
            // assign tile to ship
            shipToPlace.getTiles()[i] = battleAreaTiles[row][col];

            // update coordinates for next tile
            if (shipToPlace.isHorizontal())
                ++col;
            else
                ++row;
        }

//        if (shipToPlace.isHorizontal()) {
//            for (int i = 0; i < shipToPlace.getLength(); ++i) {
//                // set tile on board
//                battleAreaTiles[row][col + i].setType(BattleAreaTile.TileType.ShipHealthy);
//                // assign tile to ship
//                shipToPlace.getTiles()[i] = battleAreaTiles[row][col + i];
//            }
//
//        } else {
//            for (int i = 0; i < shipToPlace.getLength(); ++i) {
//                // set tile on board
//                battleAreaTiles[row + i][col].setType(BattleAreaTile.TileType.ShipHealthy);
//                // assign tile to ship
//                shipToPlace.getTiles()[i] = battleAreaTiles[row + i][col];
//            }
//        }
    }
}
