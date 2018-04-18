package at.aau.gloryweapons.siegeanddestroy3d.game.models;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BattleAreaTile {
    public enum TileType {
        WATER,
        SHIP_HEALTHY,
        SHIP_DESTROYED,
        NO_HIT
    }

    // initial type is water
    private TileType type = TileType.WATER;

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }
}
