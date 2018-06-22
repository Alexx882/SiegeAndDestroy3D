package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

public class TurnInfoDTO {

    private User playerNextTurn;

    public User getPlayerNextTurn() {
        return this.playerNextTurn;
    }

    public void setPlayerNextTurn(User playerNextTurn) {
        this.playerNextTurn = playerNextTurn;
    }
}
