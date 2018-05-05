package at.aau.gloryweapons.siegeanddestroy3d;

import java.io.Serializable;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * Created by Alexander on 05.04.2018.
 */

public class GlobalGameSettings implements Serializable {
    private User localUser;

    private GlobalGameSettings() {
    }

    public void setLocalUser(User user) {
        if (user == null)
            throw new IllegalArgumentException("user");

        this.localUser = user;
    }

    public int getPlayerId() {
        return localUser.getId();
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

    //Network settings
    private final String SERVICE_NAME = "sAd3D";
    private final int port = 16661;

    private boolean isServer;

    private static GlobalGameSettings _current = null;

    public static GlobalGameSettings getCurrent() {
        if (_current == null)
            _current = new GlobalGameSettings();

        return _current;
    }

    public static void setCurrent(GlobalGameSettings settings) {
        // TODO REWORK! this is just for testing until integration with models.GameSettings is finished
        if (settings == null)
            throw new IllegalArgumentException("settings");

        _current = settings;
    }

    public String getSERVICE_NAME() {
        return SERVICE_NAME;
    }

    public int getPort() {
        return port;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }
}
