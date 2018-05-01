package at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;

public class WifiDirectHelper {

    private Activity activity;

    public WifiDirectHelper( Activity activity){
        this.activity = activity;
    }

    public void resetWifiDirect() {
        ServerGameHandlerWifi.getInstance().resetNetwork();
        ClientGameHandlerWifi.getInstance().resetNetwork();
        GlobalGameSettings.getCurrent().setServer(false);
    }
}
