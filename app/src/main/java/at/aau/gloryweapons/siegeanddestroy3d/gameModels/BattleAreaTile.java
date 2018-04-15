package at.aau.gloryweapons.siegeanddestroy3d.gameModels;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BattleAreaTile {
    public enum TileType {
        Water,
        ShipHealthy,
        ShipDestroyed,
        NoHit
    }

    // initial type is water
    private TileType type = TileType.Water;

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }
}
