package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.IntBasedTypeConverter;

import java.io.Serializable;

import at.aau.gloryweapons.siegeanddestroy3d.network.dto.converter.InstructionTypeConverter;

@JsonObject
public class InstructionDTO {

    public enum InstructionType implements Serializable{
        DO_TURN,
        YOU_LOST,
        YOU_WON,
        WAIT,
        USER_DEAD
    }

    @JsonIgnore
    private static final long serialVersionUID = 900825671L;

    @JsonField(typeConverter = InstructionTypeConverter.class)
    private InstructionType type;

    @JsonField
    private int userId;

    @JsonField
    private int enemyUserId;

    public InstructionDTO(InstructionType type){
        this.type = type;
    }

    public InstructionDTO(){
    }

    public InstructionType getType() {
        return type;
    }

    public void setType(InstructionType type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEnemyUserId() {
        return enemyUserId;
    }

    public void setEnemyUserId(int enemyUserId) {
        this.enemyUserId = enemyUserId;
    }
}
