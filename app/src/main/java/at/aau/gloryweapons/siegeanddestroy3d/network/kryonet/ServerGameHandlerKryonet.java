package at.aau.gloryweapons.siegeanddestroy3d.network.kryonet;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.asyncCommunication.ClientData;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.GameConfigurationRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorServer;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.UserCallBack;
import at.aau.gloryweapons.siegeanddestroy3d.server.ServerController;

import static android.content.Context.WIFI_SERVICE;

public class ServerGameHandlerKryonet implements NetworkCommunicatorServer, NetworkCommunicator {

    private static ServerGameHandlerKryonet instance;
    private Server server;
    private Activity activity;
    private UserCallBack userCallBack;

    //Client data list
    private HashMap<Integer, ClientData> clientDataMap;

    private ServerController serverController;

    private ServerGameHandlerKryonet() {
        serverController = new ServerController();
    }


    public static ServerGameHandlerKryonet getInstance() {
        if (instance == null) {
            instance = new ServerGameHandlerKryonet();
        }
        return instance;
    }

    @Override
    public void initServerGameHandler(Activity activity, UserCallBack userCallBack) {
        GlobalGameSettings.getCurrent().setServer(true);

        this.clientDataMap = new HashMap<Integer, ClientData>();

        this.activity = activity;
        this.userCallBack = userCallBack;

        com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
        WifiManager wm = (WifiManager) activity.getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        server = new Server();
        server.start();
        try {
            // InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved(GlobalGameSettings.getCurrent().getPort());
            server.bind(GlobalGameSettings.getCurrent().getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                Log.d(this.getClass().getName(), "received object: " + object.getClass().getName());
                handleConnection(connection, object);
            }
        });


        KryonetHelper helper = new KryonetHelper(server);
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

    private void handleConnection(Connection connection, Object object) {
        if (object instanceof HandshakeDTO) {
            handleHandshakeDto((HandshakeDTO) object, connection);

            //update user list UI
            usernameListToUI();

        } else if (object instanceof UserNameRequestDTO) {
            handleUserNameRequest((UserNameRequestDTO) object);
        } else if (object instanceof GameConfigurationRequestDTO) {
            handleGameConfigRequest((GameConfigurationRequestDTO) object);
        } else if (object instanceof TurnDTO) {
            handleTurnDTO((TurnDTO) object);
        } else {
            Log.e(this.getClass().getName(), "cannot cast class " + object.getClass().getName());
        }

    }

    private void handleHandshakeDto(HandshakeDTO handshakeDTO, Connection connection) {
        ClientData clientData = new ClientData();
        clientData.setConnection(connection);
        clientData.setId(connection.hashCode());

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
                });
    }


    private synchronized void sendToClient(final ClientData clientData, final Object object) {
        new Thread() {
            public void run() {
                clientData.getConnection().sendTCP(object);
            }
        }.start();

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
