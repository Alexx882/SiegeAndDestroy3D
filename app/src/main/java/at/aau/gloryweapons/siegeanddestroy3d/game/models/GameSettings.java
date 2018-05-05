package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import java.util.ArrayList;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;

public class GameSettings{
    private String hostIp;
    private int myId;
    private ArrayList<User> userList;

    //Test Constructor. Dient nur zum Testen der Positionierung von SpielerLabels
    public GameSettings() {
        userList = new ArrayList<User>();
        User u1 = new User(1, "user123.", "user1");
        User u2 = new User(2, "user213.", "user2");
        User u3 = new User(3, "user321.", "user3");
        hostIp = u1.getIp();
        userList.add(u1);
        userList.add(u2);
        userList.add(u3);
    }

    public GameSettings(User host, int id) {
        userList = new ArrayList<User>();
        hostIp = host.getIp();
        userList.add(host);
        myId = id;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(User u) {
        userList.add(u);
    }

    public User getUserByIndex(int i) {
        return userList.get(i);
    }

    public int getMyId() {
        return myId;
    }
}
