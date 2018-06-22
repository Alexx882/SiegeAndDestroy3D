package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import java.io.Serializable;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;

public class TurnDTO extends RequestDTO implements Serializable {

    private static final long serialVersionUID = 1459083456079L;

    private TurnType type;

    private int userId;

    private int yCoordinates;

    private int xCoordinates;

    public enum TurnType {
        SHOT,
        POWERUP,
        HIT,
        NO_HIT,
        ERROR
    }

    public TurnDTO(TurnType type, BattleArea area) {
        this(type, area.getUserId());
    }

    public TurnDTO(TurnType type, int playerId){
        this.type = type;
        this.userId = playerId;
    }

    public TurnDTO() {
    }

    public TurnType getType() {
        return type;
    }

    public void setType(TurnType type) {
        this.type = type;
    }

    public int getyCoordinates() {
        return yCoordinates;
    }

    public void setyCoordinates(int yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    public int getxCoordinates() {
        return xCoordinates;
    }

    public void setxCoordinates(int xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }
}

