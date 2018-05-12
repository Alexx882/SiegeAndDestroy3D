package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.converter.TileTypeConverter;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.converter.TurnTypeConverter;

@JsonObject
public class TurnDTO implements Serializable{

    @JsonIgnore
    private static final long serialVersionUID = 1459083456079L;

    @JsonField(typeConverter = TurnTypeConverter.class)
    private TurnType type;

    @JsonField
    private User user;

    @JsonField
    private int attacksUserID;

    @JsonField
    private int yCoordinates;

    @JsonField
    private int xCoordinates;

    public enum TurnType{
        SHOT,
        POWERUP,
        HIT,
        NO_HIT,
        ERROR
    }

    public TurnDTO(TurnType type, User user){
        this.type = type;
        this.user = user;
    }

    public TurnDTO(){
    }

    public TurnType getType() {
        return type;
    }

    public void setType(TurnType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public int getAttacksUserID() {
        return attacksUserID;
    }

    public void setAttacksUserID(int attacksUserID) {
        this.attacksUserID = attacksUserID;
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

    public void setUser(User user) {
        this.user = user;
    }
}

