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

    @JsonField
    private int shots;

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
