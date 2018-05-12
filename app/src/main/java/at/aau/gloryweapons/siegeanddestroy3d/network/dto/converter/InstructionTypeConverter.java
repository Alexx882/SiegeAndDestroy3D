package at.aau.gloryweapons.siegeanddestroy3d.network.dto.converter;


import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import at.aau.gloryweapons.siegeanddestroy3d.network.dto.InstructionDTO;

public class InstructionTypeConverter extends StringBasedTypeConverter<InstructionDTO.InstructionType> {
    @Override
    public InstructionDTO.InstructionType getFromString(String string) {
        return InstructionDTO.InstructionType.valueOf(string);
    }

    @Override
    public String convertToString(InstructionDTO.InstructionType object) {
        return object.toString();
    }
}
