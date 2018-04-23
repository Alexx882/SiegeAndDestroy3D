package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import java.io.Serializable;

public class User implements Serializable{
    private static final long serialVersionUID = 17802657802356L;
    private int id;
    private String ip;
    private String name;

    public User(int uId, String uIp, String uName)
    {
        id=uId;
        ip=uIp;
        name=uName;

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
