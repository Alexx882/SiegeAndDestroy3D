package at.aau.gloryweapons.siegeanddestroy3d;

import java.io.Serializable;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * Created by Alexander on 05.04.2018.
 */

public class GlobalGameSettings implements Serializable {
    private User localUser;
    private int numberPlayers;

    // fixed size of rows and cols
    private int numberRows = 9;
    private int numberCols = 9;

    // fixed number and sizes of ships
    private int numberShips = 4;
    private int[] shipSizes = {3, 4, 2, 4};

    // network settings
    private final String SERVICE_NAME = "sAd3D";
    private final int port = 61616;
    private boolean isServer;

    private GlobalGameSettings() {
        localUser = new User(1, "12", "Patrick");
    }

    public void setLocalUser(User user) {
        /*if (user == null)
            throw new IllegalArgumentException("user");

        this.localUser = user;*/
    }

    public int getPlayerId() {
        return localUser.getId();
    }

    public User getLocalUser() {
        return this.localUser;
    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }

    public int getNumberRows() {
        return numberRows;
    }

    public int getNumberColumns() {
        return numberCols;
    }

    public int getNumberShips() {
        return numberShips;
    }

    public int[] getShipSizes() {
        return shipSizes;
    }

    public String getServiceName() {
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

    private static GlobalGameSettings _current = null;

    public static GlobalGameSettings getCurrent() {
        if (_current == null)
            _current = new GlobalGameSettings();

        return _current;
    }
}
