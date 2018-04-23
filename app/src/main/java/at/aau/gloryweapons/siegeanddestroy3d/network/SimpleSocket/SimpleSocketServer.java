package at.aau.gloryweapons.siegeanddestroy3d.network.SimpleSocket;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.RequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.ResponseDTO;

public class SimpleSocketServer implements Runnable {

    private ServerSocket serverSocket;

    private boolean isRunning = true;
    private HashMap<Integer, ObjectOutputStream> outputStreams;
    private ServerCallBack callBack;


    public void initServer(ServerCallBack callBack) {
        Log.i(this.getClass().getName(), "start server...");
        this.callBack = callBack;
        outputStreams = new HashMap<>();
    }

    private void listenToClient() {
        while (isRunning) {
            try {
                Socket client = serverSocket.accept();
                Log.i(this.getClass().getName(), "a new client tries to connect...");
                int id = uniqueID();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                outputStreams.put(id, objectOutputStream);

                Thread clientThread = new Thread(new ClientHandler(id, client));
                clientThread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void sendToAllClients(Object object) {
        for (Map.Entry entry : outputStreams.entrySet()) {
            try {
                outputStreams.get(entry).writeObject(object);
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
    }

    public synchronized void sentToClient(int id, Object object) {
        try {
            Log.i(this.getClass().getName(), "send object + " + object.getClass().getName() + " to user " + id);

            outputStreams.get(id).writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void runServer() {
        try {
            if (serverSocket == null) {
                serverSocket = new ServerSocket(GlobalGameSettings.getCurrent().getPort());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int uniqueID() {
        int tmpId = (1000 + (int) (Math.random() * (1000000)));
        if (outputStreams.containsKey(tmpId)) {
            tmpId = uniqueID();
        }
        return tmpId;
    }

    public void destroy() {
        //TODO: close connection
    }

    public List<Integer> getAllUserIds() {
        return new ArrayList<>(outputStreams.keySet());
    }

    @Override
    public void run() {
        runServer();
        listenToClient();
    }

    private class ClientHandler implements Runnable {

        private Socket socket;
        private ObjectInputStream input;
        private int id;

        public ClientHandler(int id, Socket socket) {
            Log.i(this.getClass().getName(), "new client connected - id: " + id);
            this.id = id;
            this.socket = socket;
            try {
                input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Object object;
            try {
                while ((object = input.readObject()) != null) {
                    Log.d(this.getClass().getName(), "new client request: " + object.getClass().getName());
                    if (object instanceof RequestDTO) {
                        RequestDTO requestDTO = (RequestDTO) object;
                        if (requestDTO.getType() == RequestDTO.RequestType.GET_ID) {
                            User user = new User(id, null, null);
                            ResponseDTO responseDTO = new ResponseDTO(ResponseDTO.ResponseType.RESPONSE_GET_ID, user);
                            sentToClient(id, responseDTO);
                        }
                    } else {

                        callBack.callback(object);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}