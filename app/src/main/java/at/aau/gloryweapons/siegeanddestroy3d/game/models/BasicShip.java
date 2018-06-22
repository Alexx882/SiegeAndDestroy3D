package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import java.io.Serializable;

/**
 * Created by Alexander on 05.04.2018.
 */
public class BasicShip implements Serializable {
    private int userId = -1;

    private int length = 0;

    private BattleAreaTile[] tiles;

    private boolean horizontal = true;

    public BasicShip(int userId, int length, boolean horizontal) {
        this.userId = userId;
        this.length = length;
        this.tiles = new BattleAreaTile[this.length];
        this.horizontal = horizontal;
    }

    public BasicShip() {
        this.tiles = new BattleAreaTile[0];
    }

    public int getUserId() {
        return userId;
    }

    public int getLength() {
        return length;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Toggles the orientation from horizontal to vertical and vice versa.
     */
    public void toggleOrientation() {
        this.horizontal = !this.horizontal;
    }

    public BattleAreaTile[] getTiles() {
        return tiles;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setTiles(BattleAreaTile[] tiles) {
        this.tiles = tiles;
    }
}
