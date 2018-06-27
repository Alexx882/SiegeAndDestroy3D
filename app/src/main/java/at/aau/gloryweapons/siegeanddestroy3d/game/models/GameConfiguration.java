package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import java.io.Serializable;
import java.util.List;

public class GameConfiguration implements Serializable {
    public static final String INTENT_KEYWORD = "Config";

    private List<User> userList;

    private List<BattleArea> battleAreaList;

    private int shots;

    private boolean cheatingAllowed;

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

    public boolean isCheatingAllowed() {
        return cheatingAllowed;
    }

    public void setCheatingAllowed(boolean cheatingAllowed) {
        this.cheatingAllowed = cheatingAllowed;
    }
}
