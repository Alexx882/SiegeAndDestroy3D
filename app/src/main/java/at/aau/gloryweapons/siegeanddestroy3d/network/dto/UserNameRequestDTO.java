package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class UserNameRequestDTO extends RequestDTO {

    @JsonField
    private String checkUsername;

    public String getCheckUsername() {
        return checkUsername;
    }

    public void setCheckUsername(String checkUsername) {
        this.checkUsername = checkUsername;
    }
}
