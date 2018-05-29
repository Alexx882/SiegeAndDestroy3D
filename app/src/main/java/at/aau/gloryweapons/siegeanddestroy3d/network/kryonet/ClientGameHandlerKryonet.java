package at.aau.gloryweapons.siegeanddestroy3d.network.kryonet;

import android.app.Activity;
import android.net.Network;
import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;

import static com.esotericsoftware.minlog.Log.LEVEL_DEBUG;
import static com.esotericsoftware.minlog.Log.LEVEL_TRACE;

public class ClientGameHandlerKryonet implements NetworkCommunicator {
    private Client client;
    private int clientId;
    private KryonetHelper helper;

    private Connection serverConnection;

    //callbacks
    private CallbackObject<HandshakeDTO> isConnected;
    private CallbackObject<User> userNameCallback;

    private CallbackObject<TurnDTO> shotHit;
    private CallbackObject<GameConfiguration> gameConfigCallback;

    private static ClientGameHandlerKryonet instance;

    private ClientGameHandlerKryonet() {

    }

    public static ClientGameHandlerKryonet getInstance() {
        if (instance == null) {
            instance = new ClientGameHandlerKryonet();
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
        shotHit = callback;
        TurnDTO hitType = new TurnDTO(TurnDTO.TurnType.SHOT, area);
        hitType.setxCoordinates(row);
        hitType.setyCoordinates(col);
        sendToServer(hitType);
    }

    @Override
    public void initClientGameHandler(String ip, Activity activity, CallbackObject<HandshakeDTO> isConnected) {
        GlobalGameSettings.getCurrent().setServer(false);

        this.isConnected = isConnected;

        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Log.i(this.getClass().getName(), "init client game handler async connection...");
        Log.i(this.getClass().getName(), "try to connect to " + ip);

        GlobalGameSettings.getCurrent().setServer(false);
        com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
        client = new Client();
        this.helper = new KryonetHelper(client);
        client.start();

        final InetAddress finalAddress = address;
        new Thread("Connection") {
            public void run() {
                try {
                    client.connect(5000, finalAddress, GlobalGameSettings.getCurrent().getPort());

                    //build and send handshake
                    HandshakeDTO handshakeDTO = new HandshakeDTO();
                    handshakeDTO.setConnectionEstablished(true);
                    sendToServer(handshakeDTO);

                    initServerCallbackHandler();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();



    }

    private void initServerCallbackHandler() {
        client.addListener(new Listener() {
            public void received(Connection serverConnection, Object receivedObject) {

                System.out.println("[Client] Received Message " + receivedObject.getClass().getName());

                //map object
                if (receivedObject instanceof HandshakeDTO) {
                    handleHandshake((HandshakeDTO) receivedObject);
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

    private void handleHandshake(HandshakeDTO handshakeDTO) {
        this.clientId = handshakeDTO.getClientId();
        Log.v(this.getClass().getName(), "handshake: " + handshakeDTO.isConnectionEstablished());
        Log.v(this.getClass().getName(), "handshake - new client ID: " + handshakeDTO.getClientId());
        isConnected.callback(handshakeDTO);
    }

    private void handleGameConfigResponse(GameConfiguration gameConfig) {
        gameConfigCallback.callback(gameConfig);
    }

    private void sendToServer(final RequestDTO object) {
        Log.d(this.getClass().getName(), "send object: " + object.getClass().getName());
        // send the client id with the request
        object.setClientId(clientId);

        new Thread("send"){
            public void run(){
                client.sendTCP(object);
            }
        }.start();

    }
}
