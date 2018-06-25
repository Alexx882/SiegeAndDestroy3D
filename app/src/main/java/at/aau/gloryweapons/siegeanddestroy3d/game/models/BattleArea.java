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

    //ship List to load the ships
    private List<ShipContainer> shipList = new ArrayList<>();

    /**
     * empty constructor for json mapping
     */
    public BattleArea() {

    }

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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setBattleAreaTiles(BattleAreaTile[][] battleAreaTiles) {
        this.battleAreaTiles = battleAreaTiles;
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

        boolean horizontalPlacement = shipToPlace.isHorizontal();

        //add ship to list
        ShipContainer container = new ShipContainer();
        container.setRow(rowToPlace);
        container.setCol(colToPlace);
        container.setShip(shipToPlace);
        shipList.add(container);

        // place the ship by setting the tiles
        for (int i = 0; i < shipToPlace.getLength(); ++i) {
            BattleAreaTile currentTile = battleAreaTiles[rowToPlace][colToPlace];

            // set tile on board
            if (i == 0)
                currentTile.setType(BattleAreaTile.TileType.SHIP_START);
            else if (i == shipToPlace.getLength() - 1)
                currentTile.setType(BattleAreaTile.TileType.SHIP_END);
            else
                currentTile.setType(BattleAreaTile.TileType.SHIP_MIDDLE);
            currentTile.setHorizontal(horizontalPlacement);

            // assign tile to the ship
            shipToPlace.getTiles()[i] = currentTile;

            // update coordinates for next tile
            if (horizontalPlacement)
                ++colToPlace;
            else
                ++rowToPlace;
        }
    }

    /**
     * Counts how many living tiles remain on the area.
     *
     * @return Number of remaining tiles.
     */
    public int remainingFields() {
        int cnt = 0;
        for (BattleAreaTile[] row : battleAreaTiles)
            for (BattleAreaTile t : row)
                if (t.isAlive())
                    ++cnt;

        return cnt;
    }

    //searches in the Container for the weakest ship and returns it
    public ShipContainer findWeakestShip() {
        ShipContainer containerWeakestShip = null;

        for (ShipContainer shipDetails: shipList) {
            checkShip(shipDetails);
            if (containerWeakestShip != null && containerWeakestShip.getCurrentLength() > shipDetails.getCurrentLength() && shipDetails.getCurrentLength() != 0 ){
                containerWeakestShip = shipDetails;
            }else if (containerWeakestShip == null && shipDetails.getCurrentLength() > 0){
                containerWeakestShip = shipDetails;
            }
        }

        return containerWeakestShip;
    }

    /**
     * gets Ship, checks if horizontal oder vertical - gets Row and Col
     * checks if alive (no water, not already hit)
     * @param shipDetails
     */
    private void checkShip(ShipContainer shipDetails) {
        List<Integer> randomPosition = new ArrayList<>();
        BasicShip ship = shipDetails.getShip();
        int currentLength = 0;
        for (int i = 0; i < ship.getLength(); i++) {
            if (ship.isHorizontal()){
                if (battleAreaTiles[shipDetails.getRow()][shipDetails.getCol() + i].isAlive()){
                    currentLength++;
                    randomPosition.add(shipDetails.getCol() + i);
                }
            }else {
                if (battleAreaTiles[shipDetails.getRow() + i][shipDetails.getCol()].isAlive()){
                    currentLength++;
                    randomPosition.add(shipDetails.getRow() + i);
                }
            }
        }
        //set alive length
        shipDetails.setCurrentLength(currentLength);

        //set random position for cheating
        if (currentLength > 0){
            setRandom(randomPosition,shipDetails );
        }
    }

    //finding a random point of attack
    private void setRandom(List<Integer> randomPosition, ShipContainer container){
        Random random = new Random();
        int index = random.nextInt(randomPosition.size());

        if (container.getShip().isHorizontal()){
            container.setRowCheating(container.getRow());
            container.setColCheating(randomPosition.get(index));
        }else {
            container.setColCheating(container.getCol());
            container.setRowCheating(randomPosition.get(index));
        }
    }
}
