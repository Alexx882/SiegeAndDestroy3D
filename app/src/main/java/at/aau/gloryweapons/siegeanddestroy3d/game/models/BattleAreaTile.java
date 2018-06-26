package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import java.io.Serializable;

/**
 * Created by Alexander on 05.04.2018.
 */
public class BattleAreaTile implements Serializable {

    public enum TileType {
        WATER,
        SHIP_START,
        SHIP_MIDDLE,
        SHIP_END,
        SHIP_START_DESTROYED,
        SHIP_MIDDLE_DESTROYED,
        SHIP_END_DESTROYED,
        NO_HIT
    }

    private boolean horizontal = true;

    // initial type is water
    private TileType type = TileType.WATER;

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public boolean isAlive(){
        return getType() == TileType.SHIP_START
                || getType() == TileType.SHIP_MIDDLE
                || getType() == TileType.SHIP_END;
}

    public boolean isDestroyed(){
        return getType() == TileType.SHIP_START_DESTROYED
                || getType() == TileType.SHIP_MIDDLE_DESTROYED
                || getType() == TileType.SHIP_END_DESTROYED;
    }

    /**
     * Returns the destroyed version of a healthy ship tile or the tile itself, if its not a ship part.
     *
     * @param type
     * @return
     */
    public static BattleAreaTile.TileType getDestroyedVersionOfShipTile(BattleAreaTile.TileType type) {
        switch (type) {
            case SHIP_START:
                return BattleAreaTile.TileType.SHIP_START_DESTROYED;
            case SHIP_MIDDLE:
                return BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED;
            case SHIP_END:
                return BattleAreaTile.TileType.SHIP_END_DESTROYED;
        }

        // failed.
        return type;
    }
}
