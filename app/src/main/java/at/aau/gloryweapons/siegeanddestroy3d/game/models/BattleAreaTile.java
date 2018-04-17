package at.aau.gloryweapons.siegeanddestroy3d.game.models;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BattleAreaTile {
   // TODO: merge enums with all occurences (convention is UPPERCASE)
    public enum TileType {
        WATER,
        SHIP_HEALTHY,
        SHIP_DESTROYED,
        Water,
        ShipHealthy,
        ShipDestroyed,
        NoHit
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
