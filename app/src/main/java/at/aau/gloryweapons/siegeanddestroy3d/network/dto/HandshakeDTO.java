package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class HandshakeDTO {

    @JsonField
    private boolean connectionEstablished;

    @JsonField
    private String id;

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public void setConnectionEstablished(boolean connectionEstablished) {
        this.connectionEstablished = connectionEstablished;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
