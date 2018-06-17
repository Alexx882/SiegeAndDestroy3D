package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * Created by Alexander on 17.06.2018.
 */

public class WinnerDTO {
    private User winner;


    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }
}
