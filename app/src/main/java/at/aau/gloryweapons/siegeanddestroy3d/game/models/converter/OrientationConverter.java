package at.aau.gloryweapons.siegeanddestroy3d.game.models.converter;

import com.bluelinelabs.logansquare.typeconverters.IntBasedTypeConverter;

public class OrientationConverter extends IntBasedTypeConverter<Boolean> {
    @Override
    public Boolean getFromInt(int i) {
        if (i == -1){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public int convertToInt(Boolean object) {
        return object ? 1 : -1;
    }
}
