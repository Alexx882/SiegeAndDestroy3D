package at.aau.gloryweapons.siegeanddestroy3d.gameModels;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BattleArea {

    private int _playerId;
    private int _nRows;
    private int _nColumns;
    private BattleAreaTile[][] _battleAreaTiles = new BattleAreaTile[0][0];

    public BattleArea(int playerId, int size) {
        this(playerId, size, size);
    }

    /**
     * Creates a new BattleArea with given playerId and dimensions. All tiles are water.
     * @param playerId
     * @param nRows
     * @param nColumns
     */
    public BattleArea(int playerId, int nRows, int nColumns) {
        _playerId = playerId;
        _nRows = nRows;
        _nColumns = nColumns;
        _battleAreaTiles = new BattleAreaTile[nRows][nColumns];
    }

    public int getPlayerId() {
        return _playerId;
    }

    public int getRowNumber() {
        return _nRows;
    }

    public int getColumnNumber() {
        return _nColumns;
    }

    public BattleAreaTile[][] getBattleAreaTiles() {
        return _battleAreaTiles;
    }
}
