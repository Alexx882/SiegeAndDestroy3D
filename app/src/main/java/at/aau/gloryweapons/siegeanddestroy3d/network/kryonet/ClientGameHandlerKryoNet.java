package at.aau.gloryweapons.siegeanddestroy3d.network.kryonet;

import android.app.Activity;
import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.GameConfigurationRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.RequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.WrapperHelper;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;

public class ClientGameHandlerKryoNet implements NetworkCommunicator {
    private Client kryoClient;
    private KryonetHelper kryoHelper;

    private int clientId;
    private WrapperHelper wrapperHelper;

    // callbacks
    // TODO check foreach if null before calling
    private CallbackObject<HandshakeDTO> isConnectedCallback;
    private CallbackObject<User> userNameCallback;
    private CallbackObject<TurnDTO> shotHitCallback;
    private CallbackObject<GameConfiguration> gameConfigCallback;

    private static ClientGameHandlerKryoNet instance;

    private ClientGameHandlerKryoNet() {

    }

    public static ClientGameHandlerKryoNet getInstance() {
        if (instance == null) {
            instance = new ClientGameHandlerKryoNet();
        }
        return instance;
    }

    @Override
    public void sendNameToServer(String username, CallbackObject<User> callback) {
        userNameCallback = callback;

        UserNameRequestDTO nameRequestDTO = new UserNameRequestDTO();
        nameRequestDTO.setCheckUsername(username);

        sendToServer(nameRequestDTO);
    }

    @Override
    public void sendGameConfigurationToServer(User user, BattleArea userBoard, List<BasicShip> placedShips, CallbackObject<GameConfiguration> callback) {
        gameConfigCallback = callback;

        GameConfigurationRequestDTO request = new GameConfigurationRequestDTO();
        request.setUser(user);
        request.setBattleArea(userBoard);
        request.setPlacedShips(placedShips);

        sendToServer(request);
    }

    @Override
    public void resetNetwork() {

    }

    @Override
    public void sendShotOnEnemyToServer(BattleArea area, int col, int row, CallbackObject<TurnDTO> callback) {
        shotHitCallback = callback;
        TurnDTO hitType = new TurnDTO(TurnDTO.TurnType.SHOT, area);
        hitType.setxCoordinates(row);
        hitType.setyCoordinates(col);
        sendToServer(hitType);
    }

    @Override
    public void initClientGameHandler(final String ip, Activity activity, CallbackObject<HandshakeDTO> isConnectedCallback) {
        GlobalGameSettings.getCurrent().setServer(false);
        this.isConnectedCallback = isConnectedCallback;

        com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
        Log.i(this.getClass().getName(), "init client connection...");
        Log.i(this.getClass().getName(), "try to connect to " + ip);

        // init wrapper
        wrapperHelper = WrapperHelper.getInstance();

        // init kryo
        kryoClient = new Client();
        kryoHelper = new KryonetHelper(kryoClient);
        kryoClient.start();
        // init before starting to send
        initServerCallbackHandler();

        // connect to server
        new Thread("Connection") {
            public void run() {
                try {
                    // this is blocking
                    kryoClient.connect(5000, ip, GlobalGameSettings.getCurrent().getPort());

                    //build and send handshake
                    HandshakeDTO handshakeDTO = new HandshakeDTO();
                    handshakeDTO.setConnectionEstablished(true);
                    sendToServer(handshakeDTO);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    private void initServerCallbackHandler() {
        kryoClient.addListener(new Listener(){
            public void received(Connection serverConnection, Object receivedObject) {

                //json to object
//                Object receivedObject = wrapperHelper.jsonToObject(receivedMessage);

                System.out.println("[Client] Received Message " + receivedObject.getClass().getName());

                //map object
                if (receivedObject instanceof HandshakeDTO) {
                    handleHandshakeResponse((HandshakeDTO) receivedObject);
                } else if (receivedObject instanceof UserNameResponseDTO) {
                    handleUserResponse((UserNameResponseDTO) receivedObject);
                } else if (receivedObject instanceof GameConfiguration) {
                    handleGameConfigResponse((GameConfiguration) receivedObject);
                }
            }
        });
    }

    private void handleUserResponse(UserNameResponseDTO user) {
        userNameCallback.callback(user.getNewUser());
    }

    private void handleHandshakeResponse(HandshakeDTO handshakeDTO) {
        this.clientId = handshakeDTO.getClientId();
        Log.v(this.getClass().getName(), "handshake: " + handshakeDTO.isConnectionEstablished());
        Log.v(this.getClass().getName(), "handshake - new client ID: " + handshakeDTO.getClientId());
        isConnectedCallback.callback(handshakeDTO);
    }

    private void handleGameConfigResponse(GameConfiguration gameConfig) {
        gameConfigCallback.callback(gameConfig);
    }

    private void sendToServer(final RequestDTO object) {
        Log.d(this.getClass().getName(), "send object: " + object.getClass().getName());

        // send the client id with the request
        object.setClientId(clientId);

        String json = wrapperHelper.ObjectToWrappedJson(object);
        if (json.length() <= 2) {
            Log.e(this.getClass().getName(), "Cannot send object");
            return;
        }

        new Thread("send"){
            public void run(){
                kryoClient.sendTCP(object);
            }
        }.start();
    }
}