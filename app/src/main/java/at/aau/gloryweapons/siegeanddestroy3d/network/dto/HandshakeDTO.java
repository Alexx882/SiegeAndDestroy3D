package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class HandshakeDTO extends RequestDTO {

    @JsonField
    private boolean connectionEstablished;

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public void setConnectionEstablished(boolean connectionEstablished) {
        this.connectionEstablished = connectionEstablished;
    }
}
