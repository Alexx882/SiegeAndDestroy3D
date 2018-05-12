package at.aau.gloryweapons.siegeanddestroy3d.network.dto.converter;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;

public class TurnTypeConverter extends StringBasedTypeConverter<TurnDTO.TurnType> {
    @Override
    public TurnDTO.TurnType getFromString(String string) {
        return TurnDTO.TurnType.valueOf(string);
    }

    @Override
    public String convertToString(TurnDTO.TurnType object) {
        return object.toString();
    }
}
