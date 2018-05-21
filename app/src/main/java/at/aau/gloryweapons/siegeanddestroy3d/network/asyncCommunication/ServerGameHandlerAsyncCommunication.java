package at.aau.gloryweapons.siegeanddestroy3d.network.asyncCommunication;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.DataSink;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;
import com.koushikdutta.async.callback.WritableCallback;
import com.peak.salut.SalutDevice;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.GameConfigurationRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.InstructionDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.WrapperHelper;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorServer;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.UserCallBack;
import at.aau.gloryweapons.siegeanddestroy3d.server.ServerController;

import static android.content.Context.WIFI_SERVICE;

public class ServerGameHandlerAsyncCommunication implements NetworkCommunicatorServer, NetworkCommunicator {
    //instance
    private static ServerGameHandlerAsyncCommunication instance;

    //Client data list
    private HashMap<Integer, ClientData> clientDataMap;

    //client name list
    private UserCallBack userCallBack;

    private Activity activity;

    private WrapperHelper wrapperHelper;

    private ServerController serverController;

    private ServerGameHandlerAsyncCommunication() {
        serverController = new ServerController();
    }

    public static ServerGameHandlerAsyncCommunication getInstance() {
        if (instance == null) {
            instance = new ServerGameHandlerAsyncCommunication();
        }
        return instance;
    }

    @Override
    public void initServerGameHandler(Activity activity, UserCallBack userCallBack) {

        wrapperHelper = WrapperHelper.getInstance();
        this.activity = activity;
        this.userCallBack = userCallBack;
        this.clientDataMap = new HashMap<Integer, ClientData>();
        WifiManager wm = (WifiManager) activity.getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        //create instance
        AsyncServer server = new AsyncServer();
        InetAddress address = null;

        GlobalGameSettings.getCurrent().setServer(true);

        Util.SUPRESS_DEBUG_EXCEPTIONS = false;

        //ip adrdress
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        //setup instance
        server.listen(address, GlobalGameSettings.getCurrent().getPort(), new ListenCallback() {
            @Override
            public void onAccepted(AsyncSocket socket) {
                Log.i(this.getClass().getName(), "[Server]: onAccepted...");

                handleConnection(socket);
            }

            @Override
            public void onListening(AsyncServerSocket socket) {
                System.out.println("[Server] Server started listening for connections");
            }

            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully shutdown instance");
            }
        });
    }

    /**
     * @param shotCount
     */
    @Override
    public void sendShotCountToServer(int shotCount) {

    }

    /**
     * Displays the existing user names in the UI
     */
    private void usernameListToUI() {
        final List<String> userList = new ArrayList<>();
        for (ClientData data : clientDataMap.values()) {
            if (data.getUser() != null) {
                userList.add(data.getUser().getName());
            } else {
                userList.add("client");
            }
        }
        // TODO move to view.
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                userCallBack.callback(userList);
            }
        });

    }

    private void handleConnection(final AsyncSocket socket) {
        Log.i(this.getClass().getName(), "socket connection started...");
        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                String request = new String(bb.getAllByteArray());
                Log.i("Server", "new request: " + request);

                //update user list UI
                usernameListToUI();

                Object receivedObject = wrapperHelper.jsonToObject(request);

                // find correct handler
                if (receivedObject instanceof HandshakeDTO) {
                    handleHandshakeDto((HandshakeDTO) receivedObject, socket);
                } else if (receivedObject instanceof UserNameRequestDTO) {
                    handleUserNameRequest((UserNameRequestDTO) receivedObject);
                } else if (receivedObject instanceof TurnDTO) {
                    handleTurnDTO((TurnDTO) receivedObject);
                } else if (receivedObject instanceof GameConfigurationRequestDTO) {
                    handleGameConfigRequest((GameConfigurationRequestDTO) receivedObject);
                } else {
                    Log.e(this.getClass().getName(), "cannot cast class");
                }
            }
        });
    }

    private void handleHandshakeDto(HandshakeDTO handshakeDTO, AsyncSocket socket) {
        ClientData clientData = new ClientData();
        clientData.setSocket(socket);
        clientData.setId(socket.hashCode());

        clientDataMap.put(clientData.getId(), clientData);
        handshakeDTO.setClientId(clientData.getId());
        sendToClient(clientData, handshakeDTO);
    }

    /**
     * Checks if the desired username is unique and returns a user object if the name is unique
     *
     * @param userNameRequestDTO
     */
    private void handleUserNameRequest(UserNameRequestDTO userNameRequestDTO) {
        String requestedName = userNameRequestDTO.getCheckUsername();

        // check if name is available and return it
        User user = serverController.checkName(requestedName);

        if (user == null)
            Log.e(this.getClass().getName(), "The username is already taken");

        // send valid User or null back to client
        ClientData data = clientDataMap.get(userNameRequestDTO.getClientId());
        data.setUser(user);

        UserNameResponseDTO response = new UserNameResponseDTO();
        response.setNewUser(user);
        sendToClient(data, response);

        //update UI
        usernameListToUI();
    }

    private void handleGameConfigRequest(GameConfigurationRequestDTO gameConfigRequestDto) {
        serverController.addDataToGameConfig(gameConfigRequestDto.getUser(),
                gameConfigRequestDto.getBattleArea(),
                gameConfigRequestDto.getPlacedShips(),
                new ClientDataCallbackObject<GameConfiguration>(clientDataMap.get(gameConfigRequestDto.getClientId())) {
                    @Override
                    public void callback(GameConfiguration gameConfig) {
                        // send the complete gameConfig back to the client
                        sendToClient(clientData, gameConfig);
                    }
                }
        );
    }

    /**
     * Sends the object to the client. The object is automatically
     * converted to a json string using the WrapperHelper class.
     * To do this, changes must be made in the class WrapperHelper
     * so that the string can be converted back into an object.
     *
     * @param clientData ClientData Object
     * @param object     Object - Must be implemented in the WrapperHelper class to be converted again
     */
    private void tsendToClient(ClientData clientData, Object object) {
        Log.i(this.getClass().getName(), " try to write message...");

        //Object to json
        final String json = wrapperHelper.ObjectToWrappedJson(object);
        if (json.length() <= 2) {
            Log.e(this.getClass().getName(), "cannot send Object");
            return;
        }
        //send json
        Util.writeAll(clientData.getSocket(), json.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully wrote message: " + json);
            }
        });
    }

    private synchronized void sendToClient(final ClientData clientData, final Object object) {
        final String json = wrapperHelper.ObjectToWrappedJson(object);
        byte[] bytes = json.getBytes();

        final ByteBuffer bb = ByteBufferList.obtain(bytes.length);
        bb.put(bytes);
        bb.flip();
        final ByteBufferList bbl = new ByteBufferList();
        bbl.add(bb);

        Log.i("+++++++++++++", "remaining: " + bbl.remaining());

        final CompletedCallback callback = new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) Log.e(this.getClass().getName(), ex.getMessage());
                Log.i(this.getClass().getName(), "[Server] Successfully wrote message: ");
            }
        };

        //send data
        final WritableCallback wc;
        final DataSink sink = clientData.getSocket();
        sink.setWriteableCallback(wc = new WritableCallback() {
            @Override
            public void onWriteable() {
                while (bbl.remaining() != 0){
                    sink.write(bbl);
                    if (bbl.remaining() == 0 && callback != null) {
                        sink.setWriteableCallback(null);
                        callback.onCompleted(null);
                        return;
                    }
                }

            }
        });
        wc.onWriteable();
    }

    /**
     * Sends the object to all clients. Objects are automatically
     * converted into a json string. The object must be implemented
     * in the class WrapperHelper, otherwise an error occurs when
     * creating json to object.
     *
     * @param object
     */
    private void sendToAllClients(Object object) {
        Log.i(this.getClass().getName(), "try to send to all clients...");
        for (ClientData clientData : clientDataMap.values()) {
            sendToClient(clientData, object);
        }
    }

    private void handleTurnDTO(TurnDTO hitType) {

    }

    @Override
    public int getNumberOfConnectedPlayers() {
        return clientDataMap.values().size();
    }

    // Network-Client methods

    @Override
    public void sendNameToServer(String username, CallbackObject<User> callback) {
        // TODO register this name
    }

    @Override
    public void sendGameConfigurationToServer(User user, BattleArea userBoard, List<BasicShip> placedShips, CallbackObject<GameConfiguration> callback) {
        serverController.addDataToGameConfig(user, userBoard, placedShips, callback);
    }

    @Override
    public void initClientGameHandler(String ip, Activity activity, CallbackObject<HandshakeDTO> isConnected) {
        // not needed on server
    }

    @Override
    public void resetNetwork() {

    }

    @Override
    public void sendShotOnEnemyToServer(BattleArea area, int col, int row, CallbackObject<TurnDTO> callback) {
        // TODO
    }

    /**
     * Used to remember the client data for communication
     */
    private abstract class ClientDataCallbackObject<T> implements CallbackObject<T> {
        protected ClientData clientData;

        public ClientDataCallbackObject(ClientData clientData) {
            this.clientData = clientData;
        }
    }
}
