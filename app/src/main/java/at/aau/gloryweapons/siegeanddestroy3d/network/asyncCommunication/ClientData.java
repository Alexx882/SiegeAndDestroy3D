package at.aau.gloryweapons.siegeanddestroy3d.network.asyncCommunication;

import com.esotericsoftware.kryonet.Connection;
import com.koushikdutta.async.AsyncSocket;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * ...Gather all important data about the client
 */
public class ClientData {

    private AsyncSocket socket;
    private User user;
    private int id;
    private Connection connection;

    // TODO remove socket field
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
