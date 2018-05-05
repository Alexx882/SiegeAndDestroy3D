package at.aau.gloryweapons.siegeanddestroy3d.game.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameConfiguration implements Serializable {
    public static final String INTENT_KEYWORD = "Config";

    private List<User> userList;
    private List<BattleArea> battleAreaList;

    // Test Constructor. Dient nur zum Testen der Positionierung von SpielerLabels
    public GameConfiguration(boolean debug) {
        userList = new ArrayList<User>();
        User u1 = new User(1, "user123.", "user1");
        User u2 = new User(2, "user213.", "user2");
        User u3 = new User(3, "user321.", "user3");
        userList.add(u1);
        userList.add(u2);
        userList.add(u3);
    }

    public GameConfiguration() {

    }

    public void setBattleAreaList(List<BattleArea> battleAreas) {
        this.battleAreaList = battleAreas;
    }

    public List<BattleArea> getBattleAreaList() {
        return this.battleAreaList;
    }

    public void setUserList(List<User> users) {
        this.userList = users;
    }

    public List<User> getUserList() {
        return userList;
    }

    public User getUserByIndex(int i) {
        return userList.get(i);
    }

}
