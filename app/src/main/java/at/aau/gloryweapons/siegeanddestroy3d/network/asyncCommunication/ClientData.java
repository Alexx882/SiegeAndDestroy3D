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
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
