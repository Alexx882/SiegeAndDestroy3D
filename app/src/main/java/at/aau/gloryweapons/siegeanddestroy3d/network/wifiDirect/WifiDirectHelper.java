package at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect;

public class WifiDirectHelper {

    private static WifiDirectHelper instance;

    public static WifiDirectHelper getInstance() {
        if (instance == null) {
            instance = new WifiDirectHelper();
        }
        return instance;
    }

    public void resetWifiDirect() {
        ServerGameHandlerWifi.getInstance().resetNetwork();
        ClientGameHandlerWifi.getInstance().resetNetwork();
    }
}
