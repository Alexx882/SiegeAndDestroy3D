package at.aau.gloryweapons.siegeanddestroy3d.game.models;


import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonObject
public class GameConfiguration implements Serializable {
    @JsonIgnore
    public static final String INTENT_KEYWORD = "Config";

    @JsonField
    private List<User> userList;

    @JsonField
    private List<BattleArea> battleAreaList;
    private int shots;

    // Test Constructor. Dient nur zum Testen der Positionierung von SpielerLabels
    public GameConfiguration(boolean debug) {
        userList = new ArrayList<User>();
        User u1 = new User(1, "user123.", "user1");
        User u2 = new User(2, "user213.", "user2");
        User u3 = new User(3, "user321.", "user3");
        userList.add(u1);
        userList.add(u2);
        userList.add(u3);

        battleAreaList = new ArrayList<BattleArea>();
        BattleArea ba1 = new BattleArea(1, 9);
        ba1.placeShip(new BasicShip(1, 3, true), 2, 2);

        BattleArea ba2 = new BattleArea(2, 9);
        ba2.placeShip(new BasicShip(2, 4, false), 4, 4);

        BattleArea ba3 = new BattleArea(3, 9);
        ba1.placeShip(new BasicShip(3, 2, true), 5, 1);

        battleAreaList.add(ba1);
        battleAreaList.add(ba2);
        battleAreaList.add(ba3);
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

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }
}
