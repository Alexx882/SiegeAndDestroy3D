package at.aau.gloryweapons.siegeanddestroy3d.network.interfaces;

import android.app.Activity;

import java.util.List;

public interface NetworkCommunicatorServer {
    /**
     * Initialization of the server
     *
     * @param activity     current activity
     * @param userCallBack a callback for the representation of the clients
     */
    public void initServerGameHandler(final Activity activity, CallbackObject<List<String>> userCallBack);

    /**
     * Stop network and disable wifi direct
     */
    public void resetNetwork();

     /**
     * Returns the number of currently connected players.
     * @return
     */
    public int getNumberOfConnectedPlayers();

}
