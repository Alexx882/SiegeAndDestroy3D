package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * Created by Alexander on 12.05.2018.
 */

public class GameConfigurationRequestDTO extends RequestDTO {
    private User user;

    private BattleArea battleArea;

    private List<BasicShip> placedShips;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BattleArea getBattleArea() {
        return battleArea;
    }

    public void setBattleArea(BattleArea battleArea) {
        this.battleArea = battleArea;
    }

    public List<BasicShip> getPlacedShips() {
        return placedShips;
    }

    public void setPlacedShips(List<BasicShip> placedShips) {
        this.placedShips = placedShips;
    }
}
