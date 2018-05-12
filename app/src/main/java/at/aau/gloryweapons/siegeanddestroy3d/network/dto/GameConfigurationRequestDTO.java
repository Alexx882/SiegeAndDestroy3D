package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import javax.security.auth.callback.Callback;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * Created by Alexander on 12.05.2018.
 */

@JsonObject
public class GameConfigurationRequestDTO {

    @JsonField
    private User user;

    @JsonField
    private BattleArea battleArea;

    @JsonField
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
