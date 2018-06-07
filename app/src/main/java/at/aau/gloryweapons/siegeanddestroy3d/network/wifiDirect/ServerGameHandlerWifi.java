package at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect;

import android.app.Activity;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorServer;

// TODO remove
public class ServerGameHandlerWifi implements NetworkCommunicatorServer {

    private SalutDataCallback callback;
    private Activity activity;
    private Salut network;
    private SalutDataReceiver dataReceiver;
    private SalutServiceData serviceData;
    private CallbackObject<List<String>> userCallBack;

    private Map<String, String> userMapper; // 1. String = Salut device.readableName (UUID), 2.Username (default = client)

    private static ServerGameHandlerWifi instance;

    public static ServerGameHandlerWifi getInstance() {
        if (instance == null) {
            instance = new ServerGameHandlerWifi();
        }
        return instance;
    }

    /**
     * Initialization of the salut server
     *
     * @param activity     current activity
     * @param userCallBack a callback for the representation of the clients
     */
    public void initServerGameHandler(final Activity activity, CallbackObject<List<String>> userCallBack) {

        //reset client mode
        ClientGameHandlerWifi.getInstance().resetNetwork();

        userMapper = new HashMap<>();

        this.userCallBack = userCallBack;

        SalutDataCallback salutDataCallback = new SalutDataCallback() {
            @Override
            public void onDataReceived(Object object) {
                Log.d(this.getClass().getName(), "request from client " + (String) object.toString());
                callbackHandler(object);
            }
        };

        this.dataReceiver = new SalutDataReceiver(activity, salutDataCallback);
        this.serviceData = new SalutServiceData(GlobalGameSettings.getCurrent().getServiceName(), GlobalGameSettings.getCurrent().getPort(), "server");

        GlobalGameSettings.getCurrent().setServer(true);

        startSalute();
    }

    /**
     *
     */
    private void startSalute() {
        this.network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                Log.e(this.getClass().getName(), "Sorry, but this device does not support WiFi Direct.");
            }
        });

        this.network.startNetworkService(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice device) {
                userMapper.put(device.readableName, "client");
                usernameListToUI();
                Log.d(this.getClass().getName(), device.deviceName + " -- " + device.readableName + " has connected!!");
            }
        });

    }

    /**
     * Displays the existing user names in the UI
     */
    private void usernameListToUI() {
        userCallBack.callback(new ArrayList<>(userMapper.values()));
    }

    /**
     * adds a new user name
     *
     * @param deviceName
     * @param username
     */
    private void setUsername(String deviceName, String username) {
        userMapper.put(deviceName, username);
        usernameListToUI();
    }

    /**
     * it will be checked if a username already exists
     *
     * @param name
     * @return name already exists: true, false
     */
    private boolean nameIsAvailable(String name) {
        return userMapper.containsValue(name);
    }

    /**
     * stop network and disable wifi direct
     */
    public void resetNetwork() {
        if (this.network != null) {
            this.network.stopNetworkService(true);
        }
    }

    @Override
    public int getNumberOfConnectedPlayers() {
        return 0;
    }

    /**
     * Processes the various requests
     *
     * @param object
     */
    private void callbackHandler(Object object) {
        try {
            String json = (String) object;
            UserNameRequestDTO userNameRequestDTO = LoganSquare.parse(json, UserNameRequestDTO.class);

            //TODO: check username

        } catch (IOException ex) {
            Log.e(this.getClass().getName(), "Failed to parse network data.");
        }

    }

    /**
     * @param userNameRequestDTO
     */
    private void userNameRequestDtoHandler(UserNameRequestDTO userNameRequestDTO) {
        //TODO: return the user object if the name is unique
    }


    /**
     * @return returns the Salut object
     */
    public Salut getNetwork() {
        return network;
    }


    /**
     * Sends an object to a specific client
     *
     * @param deviceName the device name is not the user name!
     * @param object     Important: Objects must be JsonObjects! The objects must have the notation @JsonObject and @JsonField
     */
    private void sendToClient(String deviceName, Object object) {
        for (SalutDevice device : network.registeredClients) {
            if (device.readableName == deviceName) {
                network.sendToDevice(device, object, new SalutCallback() {
                    @Override
                    public void call() {
                        Log.e(this.getClass().getName(), "failure - object could not be sent!");
                    }
                });
            }
        }
    }


    /**
     * sends an object to all clients
     *
     * @param object Important: Objects must be JsonObjects! The objects must have the notation @JsonObject and @JsonField
     */
    private void sendToAllClients(Object object) {
        network.sendToAllDevices(object, new SalutCallback() {
            @Override
            public void call() {
                Log.e(this.getClass().getName(), "failure - object could not be sent to all clients!");
            }
        });
    }
}
