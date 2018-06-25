package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

public class CheatingDTO extends RequestDTO {
    private long incomingServerTimeStamp;

    private int serverControllerId;

    public int getServerControllerId() {
        return serverControllerId;
    }

    public void setServerControllerId(int serverControllerId) {
        this.serverControllerId = serverControllerId;
    }

    public long getIncomingServerTimeStamp() {
        return incomingServerTimeStamp;
    }

    public void setIncomingServerTimeStamp(long incomingServerTimeStamp) {
        this.incomingServerTimeStamp = incomingServerTimeStamp;
    }
}
