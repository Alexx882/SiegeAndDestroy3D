package at.aau.gloryweapons.siegeanddestroy3d.network.asyncCommunication;

import com.koushikdutta.async.AsyncSocket;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * ...Gather all important data about the client
 */
public class ClientData {

    private AsyncSocket socket;
    private User user;
    private String id;
    private BattleArea battleArea;
    private List<BasicShip> placedShips;

    public AsyncSocket getSocket() {
        return socket;
    }

    public void setSocket(AsyncSocket socket) {
        this.socket = socket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
