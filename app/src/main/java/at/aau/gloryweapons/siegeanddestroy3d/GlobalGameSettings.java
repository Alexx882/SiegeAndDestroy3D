package at.aau.gloryweapons.siegeanddestroy3d;

/**
 * Created by Alexander on 05.04.2018.
 */

public class GlobalGameSettings {
    private GlobalGameSettings() {
    }

    private int _playerId = 100; // TODO: assign playerId

    public int getPlayerId() {
        return _playerId;
    }

    private int _nRows = 9; // TODO: assign #rows

    public int getNumberRows() {
        return _nRows;
    }

    private int _nCols = 9; // TODO: assign #cols

    public int getNumberColumns() {
        return _nCols;
    }

    private int _nShips = 4; // TODO

    public int getNumberShips() {
        return _nShips;
    }

    private int[] _shipSizes = {3, 4, 2, 4}; // TODO

    public int[] getShipSizes() {
        return _shipSizes;
    }

    private final String SERVICE_NAME = "sAd3D";
    private final int port = 16661;

    private static GlobalGameSettings _current = null;

    public static GlobalGameSettings getCurrent() {
        if (_current == null)
            _current = new GlobalGameSettings();

        return _current;
    }

    public String getSERVICE_NAME() {
        return SERVICE_NAME;
    }

    public int getPort() {
        return port;
    }
}
