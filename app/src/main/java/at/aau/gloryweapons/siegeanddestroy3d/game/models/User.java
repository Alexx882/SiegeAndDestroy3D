package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

@JsonObject
public class User implements Serializable {

    @JsonField
    private int id;

    @JsonField
    private String name;

    public User(int uId, String uName) {
        id = uId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
