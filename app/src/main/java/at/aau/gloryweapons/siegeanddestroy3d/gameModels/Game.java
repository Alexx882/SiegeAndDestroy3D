package at.aau.gloryweapons.siegeanddestroy3d.gameModels;

import java.util.ArrayList;

public class Game {
    private String ip;
    private ArrayList<User> userList;
//Test Constructor. Dient nur zum Testen der Positionierung von SpielerLabels
    public Game()
    {
        userList=new ArrayList<User>();
        User u1 = new User("user1.1", "user123.", "user1");
        User u2 = new User("user2.1", "user213.", "user2");
        User u3 = new User("user3.1", "user321.", "user3");
        ip=u1.getIp();
        userList.add(u1);
        userList.add(u2);
        userList.add(u3);
    }
    public Game(User host)
    {
        userList=new ArrayList<User>();
        ip=host.getIp();
        userList.add(host);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(User  u) {
        userList.add(u);
    }
    public User getUserByIndex(int i)
    {
       return userList.get(i);
    }
}
