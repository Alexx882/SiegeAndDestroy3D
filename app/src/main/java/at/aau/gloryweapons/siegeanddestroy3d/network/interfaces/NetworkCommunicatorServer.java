package at.aau.gloryweapons.siegeanddestroy3d.network.interfaces;

import android.app.Activity;

public interface NetworkCommunicatorServer {
    /**
     * Initialization of the server
     *
     * @param activity     current activity
     * @param userCallBack a callback for the representation of the clients
     */
    public void initServerGameHandler(final Activity activity, UserCallBack userCallBack);

    /**
     * Stop network and disable wifi direct
     */
    public void resetNetwork();

    /**
     * @param shotCount
     */
    public void sendShotCountToServer(int shotCount);
}
