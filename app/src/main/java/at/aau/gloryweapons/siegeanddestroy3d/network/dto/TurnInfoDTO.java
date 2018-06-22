package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

public class TurnInfoDTO {

    private User playerNextTurn;
    private List<TurnDTO> currentShots;

    public User getPlayerNextTurn() {
        return this.playerNextTurn;
    }

    public void setPlayerNextTurn(User playerNextTurn) {
        this.playerNextTurn = playerNextTurn;
    }

    public List<TurnDTO> getShots() {
        return currentShots;
    }

    public void setShots(List<TurnDTO> shots) {
        this.currentShots = shots;
    }
}
