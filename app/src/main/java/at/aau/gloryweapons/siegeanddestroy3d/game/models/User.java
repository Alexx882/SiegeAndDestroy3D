package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import java.io.Serializable;

public class User implements Serializable {

    private int id;

    private String name;

    private boolean isDefeated = false;

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

    public boolean isDefeated() {
        return isDefeated;
    }

    public void setDefeated(boolean defeated) {
        isDefeated = defeated;
    }
}
