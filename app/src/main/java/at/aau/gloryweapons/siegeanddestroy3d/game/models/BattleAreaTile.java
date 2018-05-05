package at.aau.gloryweapons.siegeanddestroy3d.game.models;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BattleAreaTile {
    public enum TileType {
        WATER,
        SHIP_START,
        SHIP_MIDDLE,
        SHIP_END,
        SHIP_DESTROYED,
        NO_HIT,
        SHIP_HEALTHY //todo delte it
    }

    private int orientation;
    // initial type is water
    private TileType type = TileType.WATER;

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
