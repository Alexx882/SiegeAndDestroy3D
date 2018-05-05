package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import java.io.Serializable;


public class InstructionDTO {

    public enum InstructionType implements Serializable{
        DO_TURN,
        YOU_LOST,
        YOU_WON,
        WAIT,
        USER_DEAD
    }

    private static final long serialVersionUID = 900825671L;
    private InstructionType type;
    private int userId;
    private int enemyUserId;

    public InstructionDTO(InstructionType type){
        this.type = type;
    }

}
