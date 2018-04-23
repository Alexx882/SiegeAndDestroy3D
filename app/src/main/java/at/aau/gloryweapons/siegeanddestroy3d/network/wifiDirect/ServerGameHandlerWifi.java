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

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.RequestDTO;

public class ServerGameHandlerWifi {

    private SalutDataCallback callback;
    private Activity activity;
    private Salut network;
    private SalutDataReceiver dataReceiver;
    private SalutServiceData serviceData;

    private static ServerGameHandlerWifi instance;

    public static ServerGameHandlerWifi getInstance() {
        if (instance == null) {
            instance = new ServerGameHandlerWifi();
        }
        return instance;
    }

    public void initServerGameHandler(final Activity activity) {
        SalutDataCallback salutDataCallback = new SalutDataCallback() {
            @Override
            public void onDataReceived(Object o) {
                callbackHandler(o);
            }
        };

        this.dataReceiver = new SalutDataReceiver(activity, salutDataCallback);
        this.serviceData = new SalutServiceData(GlobalGameSettings.getCurrent().getSERVICE_NAME(), GlobalGameSettings.getCurrent().getPort(), "server");

        startSalute();
    }

    private void startSalute() {
        Salut network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                Log.e(this.getClass().getName(), "Sorry, but this device does not support WiFi Direct.");
            }
        });

        network.startNetworkService(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice device) {
                Log.d(this.getClass().getName(), device.readableName + " has connected!");
            }
        });

        Log.i(this.getClass().getName(), "salut: running host - " + network.isRunningAsHost);
    }

    public void resetNetwork() {
        if (network != null && network.isRunningAsHost) {
            network.stopNetworkService(true);
            network.stopServiceDiscovery(true);
        }
    }


    private void callbackHandler(Object object) {
        Log.i(this.getClass().getName(), ">>>" + object.getClass().getName());
        if (object instanceof RequestDTO) {
            requestDtoHandler((RequestDTO) object);
        } else {
            Log.e(this.getClass().getName(), "Cannot read object");
        }
    }

    private void requestDtoHandler(RequestDTO requestDTO) {
        if (requestDTO.getType() == RequestDTO.RequestType.CHECK_USERNAME) {
            /*      test
             *       TODO: handle request
             * */
            User user = (User) requestDTO.getTransferObject();
            Log.i(this.getClass().getName(), "user info: " + user.getName() + " " + user.getId() + " " + user.getIp());
            showLogDevices();

        }
    }

    private void showLogDevices() {
        for (SalutDevice device : network.registeredClients) {
            Log.i(this.getClass().getName(), device.deviceName + " " + device.readableName + " " + device.instanceName + " " + device.serviceName);
        }
    }


}
