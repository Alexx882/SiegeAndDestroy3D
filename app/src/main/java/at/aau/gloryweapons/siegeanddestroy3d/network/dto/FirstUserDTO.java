package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

public class FirstUserDTO extends RequestDTO {
    private User u;

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }
}
