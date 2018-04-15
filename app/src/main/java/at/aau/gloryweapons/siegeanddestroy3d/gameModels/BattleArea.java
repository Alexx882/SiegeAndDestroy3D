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

        //
        for (int i = 0; i < battleAreaTiles.length; i++) {
            for (int j = 0; j < battleAreaTiles.length; j++) {
               battleAreaTiles[i][j] = new BattleAreaTile();
            }
        }
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
}
