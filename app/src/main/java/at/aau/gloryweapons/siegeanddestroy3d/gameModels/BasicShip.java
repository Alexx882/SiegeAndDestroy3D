package at.aau.gloryweapons.siegeanddestroy3d.gameModels;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BasicShip {
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

    public int getUserId() {
        return userId;
    }

    public int getLength() {
        return length;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public BattleAreaTile[] getTiles() {
        return tiles;
    }
}
