package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

public class HandshakeDTO extends RequestDTO {

    private boolean connectionEstablished;

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public void setConnectionEstablished(boolean connectionEstablished) {
        this.connectionEstablished = connectionEstablished;
    }
}
