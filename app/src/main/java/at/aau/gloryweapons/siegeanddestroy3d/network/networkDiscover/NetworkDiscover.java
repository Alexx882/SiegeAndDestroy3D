package at.aau.gloryweapons.siegeanddestroy3d.network.networkDiscover;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Johannes on 26.03.2018.
 */
public class NetworkDiscover {

    private int myPort;
    private final String SERVICE_NAME = "siegeAndDestroy3D";
    private final String SERVICE_TYPE = "_siegeAndDestroy3D._tcp";


    private Context context;

    private NsdManager nsdManager;
    private NsdManager.RegistrationListener registrationListener;
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;

    //Server
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean hostServer;

    private Socket socket;

    /**
     * @param context the context -> getApplicationContext
     */
    public NetworkDiscover(Context context) {
        this.context = context;
    }


    /**
     * @param registrationListener NsdManager.RegistrationListener this interface contains callbacks
     *                             <p>
     *                             TODO: implement server
     */
    public void createServer(NsdManager.RegistrationListener registrationListener) {
        this.registrationListener = registrationListener;
        hostServer = true;

        this.serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //0 = random port
                    serverSocket = new ServerSocket(0);
                    myPort = serverSocket.getLocalPort();
                    Log.v(this.getClass().getName() + " createServer()", "serverport:" + myPort);

                    //reg. Service
                    registerService();

                    while (hostServer) {
                        socket = serverSocket.accept();
                        //TODO: do thomething....
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        serverThread.start();
    }


    //stop Server
    public void stopServer() {
        try {
            hostServer = false;
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unregisterService();

    }


    //unregister Service
    private void unregisterService() {

        if (nsdManager == null) {

            nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        }
        nsdManager.unregisterService(registrationListener);
    }


    //register network service
    private void registerService() {

        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(myPort);

        if (nsdManager == null) {

            nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        }
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);

    }


    /**
     * @param discoveryListener NsdManager.DiscoveryListener this interface contains callbacks
     *                          discover service
     */
    public void onDiscover(NsdManager.DiscoveryListener discoveryListener) {
        hostServer = false;
        this.discoveryListener = discoveryListener;

        if (nsdManager == null) {
            nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        }
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);

    }


    /**
     * @param nsdServiceInfo
     * @param resolveListener this interface contains callbacks
     */
    public void onResolve(NsdServiceInfo nsdServiceInfo, NsdManager.ResolveListener resolveListener) {
        this.resolveListener = resolveListener;


        if (nsdManager == null) {
            nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        }
        nsdManager.resolveService(nsdServiceInfo, resolveListener);

    }

}
