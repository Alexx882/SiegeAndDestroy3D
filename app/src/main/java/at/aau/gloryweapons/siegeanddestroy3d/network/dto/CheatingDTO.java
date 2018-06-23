package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

public class CheatingDTO extends RequestDTO {
    private long incomingServerTimeStamp;

    public long getIncomingServerTimeStamp() {
        return incomingServerTimeStamp;
    }

    public void setIncomingServerTimeStamp(long incomingServerTimeStamp) {
        this.incomingServerTimeStamp = incomingServerTimeStamp;
    }
}
