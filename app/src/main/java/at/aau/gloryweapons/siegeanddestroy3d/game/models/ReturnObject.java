package at.aau.gloryweapons.siegeanddestroy3d.game.models;

public class ReturnObject {
    int i =0;
    String message = null;
    BattleAreaTile.TileType tile =null;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BattleAreaTile.TileType getTile() {
        return tile;
    }

    public void setTile(BattleAreaTile.TileType tile) {
        this.tile = tile;
    }
}
