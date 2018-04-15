package at.aau.gloryweapons.siegeanddestroy3d.network;

public class Instruction {

    enum InstructionType{
        DO_TURN,
        YOU_LOST,
        YOU_WON,
        WAIT,
        USER_DEAD
    }


    private InstructionType type;
    private int userId;
    private int enemyUserId;

    public Instruction(InstructionType type){
        this.type = type;
    }

}
