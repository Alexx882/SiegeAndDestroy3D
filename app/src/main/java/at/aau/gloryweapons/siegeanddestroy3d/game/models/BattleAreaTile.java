package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.IntBasedTypeConverter;

import java.io.Serializable;
import java.util.Arrays;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.converter.TileTypeConverter;

/**
 * Created by Alexander on 05.04.2018.
 */
@JsonObject
public class BattleAreaTile implements Serializable {


    public enum TileType {
        WATER,
        SHIP_START,
        SHIP_MIDDLE,
        SHIP_END,
        SHIP_DESTROYED,
        NO_HIT
    }


    @JsonField
    private boolean horizontal = true;

    // initial type is water
    @JsonField(typeConverter = TileTypeConverter.class)
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

}
