package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import java.io.Serializable;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

public class CheatingResponseDTO implements Serializable{
    private boolean gotCaught;
    private User caughtByUser;

    public boolean isGotCaught() {
        return gotCaught;
    }

    public void setGotCaught(boolean gotCaught) {
        this.gotCaught = gotCaught;
    }

    public User getCaughtByUser() {
        return caughtByUser;
    }

    public void setCaughtByUser(User caughtByUser) {
        this.caughtByUser = caughtByUser;
    }
}
