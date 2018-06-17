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
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheaterSuspicionDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheaterSuspicionResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.FinishRoundDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.GameConfigurationRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.RequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnInfoDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.WrapperHelper;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorClient;

public class ClientGameHandlerKryoNet implements NetworkCommunicatorClient {

    private Client kryoClient;
    private KryonetHelper kryoHelper;

    private int clientId;
    private WrapperHelper wrapperHelper;

    // callbacks
    private CallbackObject<HandshakeDTO> isConnectedCallback;
    private CallbackObject<User> userNameCallback;
    private CallbackObject<TurnDTO> shotHitCallback;
    private CallbackObject<GameConfiguration> gameConfigCallback;
    private CallbackObject<User> cheaterSuspicionCallback;

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
    public void sendFinish() {
        FinishRoundDTO finish =new FinishRoundDTO();
        sendToServer(finish);
    }

    @Override
    public void sendCheatingSuspicion(CallbackObject<User> callback) {
        cheaterSuspicionCallback = callback;
        CheaterSuspicionDTO cheaterSuspicionDTO = new CheaterSuspicionDTO();
        sendToServer(cheaterSuspicionDTO);
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
        KryonetHelper.registerClassesForEndpoint(kryoClient);
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
                   Log.e("KryoClient", "Error", ex);
                }
            }
        }.start();
    }

    private void initServerCallbackHandler() {
        kryoClient.addListener(new Listener() {
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
                } else if (receivedObject instanceof TurnDTO) {
                    if (shotHitCallback != null)
                        shotHitCallback.callback((TurnDTO) receivedObject);
                } else if (receivedObject instanceof TurnInfoDTO) {
                    handleTurnInfo((TurnInfoDTO) receivedObject);
                }else if(receivedObject instanceof CheaterSuspicionResponseDTO){
                    handleCheatingSuspicionResponse((CheaterSuspicionResponseDTO) receivedObject);
                }else  {
                    Log.e(this.getClass().getName(), "Cannot read object: " + receivedObject.getClass().getName());
                }

            }
        });
    }

    private void handleCheatingSuspicionResponse(CheaterSuspicionResponseDTO receivedObject) {
        if (cheaterSuspicionCallback != null){
            cheaterSuspicionCallback.callback(receivedObject.getUser());
        }
    }

    private void handleUserResponse(UserNameResponseDTO user) {
        if (userNameCallback != null)
            userNameCallback.callback(user.getNewUser());
    }

    private void handleHandshakeResponse(HandshakeDTO handshakeDTO) {
        this.clientId = handshakeDTO.getClientId();
        Log.v(this.getClass().getName(), "handshake: " + handshakeDTO.isConnectionEstablished());
        Log.v(this.getClass().getName(), "handshake - new client ID: " + handshakeDTO.getClientId());
        if (isConnectedCallback != null)
            isConnectedCallback.callback(handshakeDTO);
    }

    private void handleGameConfigResponse(GameConfiguration gameConfig) {
        if (gameConfigCallback != null)
            gameConfigCallback.callback(gameConfig);
    }

    private void handleTurnInfo(TurnInfoDTO receivedObject) {
        // save info in GGS
        GlobalGameSettings.getCurrent().setUserOfCurrentTurn(receivedObject.getPlayerNextTurn());
    }

    private void sendToServer(final RequestDTO object) {
        Log.d(this.getClass().getName(), "send object: " + object.getClass().getName());

        // send the client id with the request
        object.setClientId(clientId);

        new Thread("send") {
            public void run() {
                kryoClient.sendTCP(object);
            }
        }.start();
    }

}
