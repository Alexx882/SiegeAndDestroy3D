package at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect;

import android.app.Activity;
import android.util.Log;

import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.InstructionDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;


public class ClientGameHandlerWifi implements NetworkCommunicator {

    private SalutDataCallback callback;
    private Activity activity;
    private CallbackObject<User> userCallbackObject;
    private SalutDataReceiver dataReceiver;
    private SalutServiceData serviceData;
    private Salut network;
    private static ClientGameHandlerWifi instance;
    private CallbackObject<SalutDevice> salutDeviceCallbackObject;


    public static ClientGameHandlerWifi getInstance() {
        if (instance == null) {
            instance = new ClientGameHandlerWifi();
        }
        return instance;
    }

    public void initClientGameHandler(final Activity activity, CallbackObject<SalutDevice> showServer) {
        this.salutDeviceCallbackObject = showServer;

        SalutDataCallback salutDataCallback = new SalutDataCallback() {
            @Override
            public void onDataReceived(Object o) {
                callbackHandler(o);
            }
        };
        this.dataReceiver = new SalutDataReceiver(activity, salutDataCallback);
        this.serviceData = new SalutServiceData(GlobalGameSettings.getCurrent().getSERVICE_NAME(), GlobalGameSettings.getCurrent().getPort(), "client");

        startSalut();
    }

    private void startSalut() {
        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                Log.e(this.getClass().getName(), "Sorry, but this device does not support WiFi Direct.");
            }
        });

        network.discoverNetworkServices(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice device) {
                Log.d(this.getClass().getName(), "A device has connected with the name " + device.deviceName);
                //registerToHost(device);
                salutDeviceCallbackObject.callback(device);
            }
        }, true);
    }

    public void registerToHost(SalutDevice device, String userName) {
        network.registerWithHost(device, new SalutCallback() {
            @Override
            public void call() {
                Log.d(this.getClass().getName(), "We're now registered.");
            }
        }, new SalutCallback() {
            @Override
            public void call() {
                Log.d(this.getClass().getName(), "We failed to register.");
            }
        });
    }

    public void sendToServer(Object o) {
        network.sendToHost(o, new SalutCallback() {
            @Override
            public void call() {
                Log.e(this.getClass().getName(), "salut failure - Message cannot be sent!");
            }
        });
    }

    private void callbackHandler(Object object) {
        userCallbackObject.callback((User) object);
        Log.i(this.getClass().getName(), ">>>" + object.getClass().getName());
    }

    public void resetNetwork() {
        if (network != null && network.isConnectedToAnotherDevice) {
            network.unregisterClient(true);
            network.stopNetworkService(true);
            network.stopServiceDiscovery(true);
        }
    }

    @Override
    public void sendNameToServer(User user, CallbackObject<User> callback) {
        userCallbackObject = callback;
        //sendToServer(user);
    }

    @Override
    public void sendGameConfigurationToServer(User user, BattleArea userBoard, List<BasicShip> placedShips, CallbackObject<GameConfiguration> callback) {

    }

    @Override
    public void receiveServerMessages(CallbackObject<InstructionDTO> callback) {

    }

    @Override
    public void getUserId(CallbackObject<User> callback) {

    }
}
