package at.aau.gloryweapons.siegeanddestroy3d.network.asyncCommunication;

import android.app.Activity;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.peak.salut.SalutDevice;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.GameConfigurationRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.InstructionDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.WrapperHelper;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;

public class ClientGameHandlerAsyncCommunication implements NetworkCommunicator {
    private AsyncSocket server;
    private String thisDeviceName;
    private WrapperHelper wrapperHelper;

    //callbacks
    private CallbackObject<HandshakeDTO> isConnected;
    private CallbackObject<User> userNameCallback;

    private CallbackObject<TurnDTO> shotHit;
    private CallbackObject<GameConfiguration> gameConfigCallback;


    private static ClientGameHandlerAsyncCommunication instance;

    private ClientGameHandlerAsyncCommunication() {

    }

    public static ClientGameHandlerAsyncCommunication getInstance() {
        if (instance == null) {
            instance = new ClientGameHandlerAsyncCommunication();
        }
        return instance;
    }

    @Override
    public void sendNameToServer(String username, CallbackObject<User> callback) {
        userNameCallback = callback;

        UserNameRequestDTO nameRequestDTO = new UserNameRequestDTO();
        nameRequestDTO.setCheckUsername(username);
        nameRequestDTO.setDeviceName(thisDeviceName);

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
    public void receiveServerMessages(CallbackObject<InstructionDTO> callback) {

    }

    @Override
    public void getUserId(CallbackObject<User> callback) {

    }

    @Override
    public void initClientGameHandler(Activity activity, CallbackObject<SalutDevice> showServer) {
        Log.i(this.getClass().getName(), "WTF??!!");
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
        Log.i(this.getClass().getName(), "init client game handler async connection...");
        Log.i(this.getClass().getName(), "try to connect to " + ip);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, 61616);
        Log.i("*********", inetSocketAddress.getAddress() + ":" + inetSocketAddress.getPort());
        this.isConnected = isConnected;

        thisDeviceName = UUID.randomUUID().toString();
        //TODO save UUID

        wrapperHelper = WrapperHelper.getInstance();

        AsyncServer.getDefault().connectSocket(inetSocketAddress, new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, AsyncSocket socket) {
                if (ex != null) {
                    Log.e(this.getClass().getName(), ex.getMessage());
                }
                Log.v(this.getClass().getName(), " connection completed...");
                server = socket;

                //build handshake
                HandshakeDTO handshakeDTO = new HandshakeDTO();
                handshakeDTO.setConnectionEstablished(true);
                handshakeDTO.setId(thisDeviceName);
                String json = wrapperHelper.ObjectToWrappedJson(handshakeDTO);

                //send handshake
                Util.writeAll(server, json.getBytes(), new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {

                    }
                });
                receivedFromServer();

            }
        });


    }

    private void receivedFromServer() {
        server.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                String receivedMessage = new String(bb.getAllByteArray());
                System.out.println("[Client] Received Message " + receivedMessage);
                //TODO handle request

                //json to object
                Object receivedObject = wrapperHelper.jsonToObject(receivedMessage);

                //map object
                if (receivedObject instanceof HandshakeDTO) {
                    handleHandshake((HandshakeDTO) receivedObject);
                } else if (receivedObject instanceof User) {
                    handleUserResponse((User) receivedObject);
                } else if (receivedObject instanceof GameConfiguration) {
                    handleGameConfigResponse((GameConfiguration) receivedObject);
                }

            }
        });
    }

    private void handleUserResponse(User user) {
        userNameCallback.callback(user);
    }

    private void handleHandshake(HandshakeDTO handshakeDTO) {
        Log.v(this.getClass().getName(), "handshake: " + handshakeDTO.isConnectionEstablished());
        isConnected.callback(handshakeDTO);
    }

    private void handleGameConfigResponse(GameConfiguration gameConfig) {
        gameConfigCallback.callback(gameConfig);
    }


    private void sendToServer(Object object) {
        String json = wrapperHelper.ObjectToWrappedJson(object);
        if (json.length() <= 2) {
            Log.e(this.getClass().getName(), "Cannot send object");
            return;
        }
        Util.writeAll(server, json.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully wrote message");
            }
        });
    }


}
