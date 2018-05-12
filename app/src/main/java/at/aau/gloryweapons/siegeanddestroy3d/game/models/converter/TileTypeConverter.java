package at.aau.gloryweapons.siegeanddestroy3d.game.models.converter;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;

public class TileTypeConverter extends StringBasedTypeConverter<BattleAreaTile.TileType> {
    @Override
    public BattleAreaTile.TileType getFromString(String string) {
        return BattleAreaTile.TileType.valueOf(string);
    }

    @Override
    public String convertToString(BattleAreaTile.TileType object) {
        return object.toString();
    }

}
