package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.converter.TileTypeConverter;

/**
 * Created by Alexander on 05.04.2018.
 */
@JsonObject
public class BasicShip implements Serializable {
    @JsonField
    private int userId = -1;

    @JsonField
    private int length = 0;

    @JsonIgnore
    private BattleAreaTile[] tiles;

    @JsonField
    private boolean horizontal = true;

    @JsonField
    private List<BattleAreaTile> forJsonTile;

    public List<BattleAreaTile> getForJsonTile() {
        if (tiles != null){
            forJsonTile = new ArrayList<>(Arrays.asList(tiles));
        }
        return forJsonTile;
    }

    public void setForJsonTile(List<BattleAreaTile> forJsonTile) {
        this.forJsonTile = forJsonTile;
        tiles = this.forJsonTile.toArray(new BattleAreaTile[forJsonTile.size()]);
    }

    public BasicShip(){
    }

    public BasicShip(int userId, int length, boolean horizontal) {
        this.userId = userId;
        this.length = length;
       // this.tiles = new BattleAreaTile[this.length];
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

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Toggles the orientation from horizontal to vertical and vice versa.
     */
    public void toggleOrientation(){
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
