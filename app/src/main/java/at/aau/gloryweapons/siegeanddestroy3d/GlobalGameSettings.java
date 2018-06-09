package at.aau.gloryweapons.siegeanddestroy3d;

import android.provider.Settings;

import java.io.Serializable;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * Created by Alexander on 05.04.2018.
 */

public class GlobalGameSettings implements Serializable {
    private User localUser;
    private int numberPlayers;
    private User currentTurnUser;
    private boolean schummelnEnabled;
    private int numberShots;

    // fixed size of rows and cols
    private int numberRows = 8;
    private int numberCols = 8;

    // fixed number and sizes of ships
    private int numberShips = 4;
    private int[] shipSizes = {3, 4, 2, 4};

    // network settings
    private static final String serviceName = "sAd3D";
    private static final int port = 61616;
    private boolean isServer;

    private GlobalGameSettings() {

    }

    public void setLocalUser(User user) {
        this.localUser = user;
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
        return serviceName;
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

    public User getUserOfCurrentTurn() {
        return this.currentTurnUser;
    }

    public void setUserOfCurrentTurn(User userOfCurrentTurn) {
        this.currentTurnUser = userOfCurrentTurn;
    }

    public boolean isSchummelnEnabled() {
        return schummelnEnabled;
    }

    public void setSchummelnEnabled(boolean schummelnEnabled) {
        this.schummelnEnabled = schummelnEnabled;
    }

    public int getNumberShots() {
        return numberShots;
    }

    public void setNumberShots(int numberShots) {
        this.numberShots = numberShots;
    }

    public static final String INTENT_KEYWORD ="GLOBALGAMESETTINGS_INTENT";
    private static GlobalGameSettings instance = null;

    public static GlobalGameSettings getCurrent() {
        if (instance == null)
            instance = new GlobalGameSettings();
        return instance;
    }

    /**
     * Workaround because changing activities doesnt take singletons very well.
     *
     * @param current
     */
    public static void setCurrent(GlobalGameSettings current) {
        instance = current;
    }

}
