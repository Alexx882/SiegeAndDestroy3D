package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

public class CheaterSuspicionResponseDTO {
    private User userWhoCheats;

    public User getUserWhoCheats() {
        return userWhoCheats;
    }

    public void setUserWhoCheats(User userWhoCheats) {
        this.userWhoCheats = userWhoCheats;
    }
}
