package at.aau.gloryweapons.siegeanddestroy3d.network.SimpleSocket;


import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;

public class SimpleSocketClient implements Runnable {
    private Socket socket;
    private String serverIp;

    private ClientCallBack clientCallBack;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public SimpleSocketClient() {
    }

    //@Deprecated
    public void initClient(String serverIp, ClientCallBack callBack) {
        this.serverIp = serverIp;
        this.clientCallBack = callBack;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }


    private void connect() {
        Log.i(this.getClass().getName(), "connect to server ip: " + serverIp);
        try {
            socket = new Socket(serverIp, GlobalGameSettings.getCurrent().getPort());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sentToServer(Object object) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (object == null) {
            Log.e(this.getClass().getName(), "cannot write a null object!");
            return;
        }
        try {
            Log.i(this.getClass().getName(), "send " + object.getClass().getName() + " Object");
            Log.i(this.getClass().getName(), outputStream == null ? "null" : "init" + "// " + outputStream);
            outputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleObject(Object object) {
        this.clientCallBack.callback(object);
    }

    @Override
    public void run() {
        connect();
        Thread thread = new Thread(new ServerListner());
        thread.start();

    }

    private class Sender extends AsyncTask<Object, Void, Void> {


        @Override
        protected Void doInBackground(Object... objects) {
            try {
                Log.i(this.getClass().getName(), "send " + objects[0].getClass().getName() + " Object");
                Log.i(this.getClass().getName(), outputStream == null ? "null" : "init" + "// " + outputStream);
                outputStream.writeObject(objects[0]);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class ServerListner implements Runnable {

        @Override
        public void run() {

            try {
                while (true) {
                    handleObject(inputStream.readObject());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

}
