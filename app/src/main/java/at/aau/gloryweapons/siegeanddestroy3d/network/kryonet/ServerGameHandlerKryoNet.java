package at.aau.gloryweapons.siegeanddestroy3d.network.kryonet;

import android.app.Activity;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.GameConfigurationRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnInfoDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.WrapperHelper;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorClient;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorServer;
import at.aau.gloryweapons.siegeanddestroy3d.server.ServerController;

public class ServerGameHandlerKryoNet implements NetworkCommunicatorServer, NetworkCommunicatorClient {
    private Server kryoServer;
    private KryonetHelper kryoHelper;

    //instance
    private static ServerGameHandlerKryoNet instance;

    //Client data list
    private HashMap<Integer, ClientData> clientDataMap;

    //client name list
    private CallbackObject<List<String>> userCallBack;

    CallbackObject<User> turnInfoUpdateCallback;

    private Activity activity;

    private WrapperHelper wrapperHelper;

    private ServerController serverController;

    private ServerGameHandlerKryoNet() {
        serverController = new ServerController();
    }

    public static ServerGameHandlerKryoNet getInstance() {
        if (instance == null) {
            instance = new ServerGameHandlerKryoNet();
        }
        return instance;
    }

    @Override
    public void initServerGameHandler(Activity activity, CallbackObject<List<String>> userCallBack) {
        GlobalGameSettings.getCurrent().setServer(true);

        wrapperHelper = WrapperHelper.getInstance();
        this.activity = activity;
        this.userCallBack = userCallBack;
        this.clientDataMap = new HashMap<Integer, ClientData>();

        com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);

        // init kryo
        kryoServer = new Server();
        KryonetHelper.registerClassesForEndpoint(kryoServer);
        kryoServer.start();

        try {
            kryoServer.bind(GlobalGameSettings.getCurrent().getPort());

            kryoServer.addListener(new Listener() {
                public void received(Connection connection, Object object) {
                    Log.d(this.getClass().getName(), "received object: " + object.getClass().getName());
                    handleConnection(connection, object);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param shotCount
     */
    @Override
    public void sendShotCountToServer(int shotCount) {
        serverController.sentShots(shotCount);
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

    private void handleConnection(Connection clientConnection, Object receivedObject) {
//                Object receivedObject = wrapperHelper.jsonToObject(request);

        // find correct handler
        if (receivedObject instanceof HandshakeDTO) {
            handleHandshakeDto((HandshakeDTO) receivedObject, clientConnection);
            usernameListToUI();
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

    private void handleHandshakeDto(HandshakeDTO handshakeDTO, Connection clientConnection) {
        ClientData clientData = new ClientData();
        clientData.setConnection(clientConnection);
        clientData.setId(clientConnection.hashCode());

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
        // after the gameConfig finished the clients should get info about the first turn
        // just overwrite every time, bc its just needed once
        serverController.registerForGameConfigCompletion(new CallbackObject<User>() {
            @Override
            public void callback(User nextUser) {
                sendFirstTurnInfo(nextUser);
            }
        });

        // add data for every client
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
     * Sends the info about the first turn to the clients.
     */
    private void sendFirstTurnInfo(User nextUser) {
        TurnInfoDTO turnInfo = new TurnInfoDTO();
        turnInfo.setPlayerNextTurn(nextUser);

        // send to clients
        sendToAllClients(turnInfo);

        // send to server game instance
        turnInfoUpdateCallback.callback(nextUser);
    }
    
    /**
     * Sends the info about the next turn to the clients.
     */
    private void sendNextTurnInfo() {
        // TODO send the next turn info somewhere
        User nextUser = serverController.getUserForNextTurn();

        TurnInfoDTO turnInfo = new TurnInfoDTO();
        turnInfo.setPlayerNextTurn(nextUser);

        sendToAllClients(turnInfo);
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
    private void sendToClient(final ClientData clientData, final Object object) {
        Log.d(this.getClass().getName(), "send object: " + object.getClass().getName());

        new Thread("send") {
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
        hitType = serverController.checkShot(hitType);
        ClientData client = clientDataMap.get(hitType.getClientId());
        sendToClient(client, hitType);
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
        // todo remove
    }

    @Override
    public void sendShotOnEnemyToServer(BattleArea area, int col, int row, CallbackObject<TurnDTO> callback) {
        // TODO
    }

    @Override
    public void registerForTurnInfos(CallbackObject<User> nextUserCallback) {
        this.turnInfoUpdateCallback = nextUserCallback;
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
