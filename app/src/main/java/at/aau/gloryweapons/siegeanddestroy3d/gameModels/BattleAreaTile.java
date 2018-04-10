package at.aau.gloryweapons.siegeanddestroy3d.gameModels;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BattleAreaTile {
    public enum TileType {
        Water,
        ShipHealthy,
        ShipDestroyed
    }

    // initial type is water
    public TileType Type = TileType.Water;


}
