package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

@JsonObject
public class RequestDTO{
    private RequestType type;
    private Object transferObject;

    public enum RequestType{
        GET_ID,
        CHECK_USERNAME
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public Object getTransferObject() {
        return transferObject;
    }

    public void setTransferObject(Object request) {
        this.transferObject = request;
    }
}
