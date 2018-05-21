package at.aau.gloryweapons.siegeanddestroy3d.game.models.converter;

import com.bluelinelabs.logansquare.typeconverters.IntBasedTypeConverter;
import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Arrays;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;

public class TileTypeConverter extends IntBasedTypeConverter<BattleAreaTile.TileType> {
   /*  @Override
     public BattleAreaTile.TileType getFromString(String type) {
         return BattleAreaTile.TileType.valueOf(type);
     }

     @Override
     public String convertToString(BattleAreaTile.TileType object) {
         return object.toString();
     }*/

    @Override
    public BattleAreaTile.TileType getFromInt(int i) {
        return BattleAreaTile.TileType.values()[i];
    }

    @Override
    public int convertToInt(BattleAreaTile.TileType object) {
        return Arrays.asList(BattleAreaTile.TileType.values()).indexOf(object);
    }

}
