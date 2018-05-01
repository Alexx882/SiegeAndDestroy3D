package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

@JsonObject
public class ResponseDTO {

    @JsonField
    private Object transferObject;

    public Object getTransferObject() {
        return transferObject;
    }

    public void setTransferObject(Object transferObject) {
        this.transferObject = transferObject;
    }

}
