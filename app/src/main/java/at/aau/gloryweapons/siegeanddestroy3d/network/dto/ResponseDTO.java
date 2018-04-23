package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import java.io.Serializable;

public class ResponseDTO implements Serializable {

    public enum ResponseType{
        RESPONSE_GET_ID,
        RESPONSE_CHECK_USERNAME
    }

    private Object transferObject;
    private ResponseType type;


    public ResponseDTO(ResponseType type, Object transferObject){
        this.type = type;
        this.transferObject = transferObject;
    }

    public Object getTransferObject() {
        return transferObject;
    }

    public void setTransferObject(Object transferObject) {
        this.transferObject = transferObject;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }
}
