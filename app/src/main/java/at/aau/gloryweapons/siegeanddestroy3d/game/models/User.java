package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

@JsonObject
public class User implements Serializable {

    @JsonField
    private int id;

    @JsonField
    private String ip;

    @JsonField
    private String name;


    public User(int uId, String uIp, String uName) {
        id = uId;
        ip = uIp;
        name = uName;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
