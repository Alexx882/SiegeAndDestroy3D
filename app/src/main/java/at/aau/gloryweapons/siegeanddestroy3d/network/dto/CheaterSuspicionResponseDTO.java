package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

public class CheaterSuspicionResponseDTO {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
