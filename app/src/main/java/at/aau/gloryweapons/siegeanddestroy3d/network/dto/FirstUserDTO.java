package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

@JsonObject
public class FirstUserDTO extends RequestDTO {
    @JsonField
    private User u;

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }
}
