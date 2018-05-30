package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

@JsonObject
public class TurnInfoDTO {

    @JsonField
    private User playerNextTurn;

    public TurnInfoDTO() {

    }

    public User getPlayerNextTurn() {
        return this.playerNextTurn;
    }

    public void setPlayerNextTurn(User playerNextTurn) {
        this.playerNextTurn = playerNextTurn;
    }
}
