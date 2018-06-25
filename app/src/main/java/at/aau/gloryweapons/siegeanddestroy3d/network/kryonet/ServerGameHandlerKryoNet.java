package at.aau.gloryweapons.siegeanddestroy3d.network.kryonet;

import android.app.Activity;
import android.os.Handler;
import android.provider.Settings;
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
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheaterSuspicionDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheaterSuspicionResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheatingDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.FinishRoundDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.FirstUserDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.GameConfigurationRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.QuitGame;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnInfoDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.WinnerDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorClient;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorServer;
import at.aau.gloryweapons.siegeanddestroy3d.server.ServerController;

public class ServerGameHandlerKryoNet implements NetworkCommunicatorServer, NetworkCommunicatorClient {
    private Server kryoServer;

    //instance
    private static ServerGameHandlerKryoNet instance;

    //Client data list
    private HashMap<Integer, ClientData> clientDataMap;

    // callbacks
    private CallbackObject<List<String>> userCallBack;
    private CallbackObject<TurnInfoDTO> currentTurnUserCallback;
    private CallbackObject<User> winnerCallback;
    private CallbackObject<User> cheaterSuspicionCallback;

    private Activity activity;

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
        } catch (IOException ex) {
            Log.e("KryoClient", "Error", ex);
        }
    }

    /**
     * Displays the existing user names in the UI
     */
    private void usernameListToUI() {
        final List<String> userList = new ArrayList<>();
        // add name of host
        if (GlobalGameSettings.getCurrent().getLocalUser() != null)
            userList.add(GlobalGameSettings.getCurrent().getLocalUser().getName());
        // add name of clients
        for (ClientData data : clientDataMap.values()) {
            if (data.getUser() != null) {
                userList.add(data.getUser().getName());
            } else {
                userList.add("client");
            }
        }

        // TODO move to view.
        if (userCallBack != null)
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
        } else if (receivedObject instanceof FinishRoundDTO) {
            handleFinishRoundRequest((FinishRoundDTO) receivedObject);
        } else if (receivedObject instanceof CheaterSuspicionDTO) {
            handleCheatingSuspicion((CheaterSuspicionDTO) receivedObject);
        } else if(receivedObject instanceof CheatingDTO){
            handleCheating((CheatingDTO) receivedObject);
        } else if (receivedObject instanceof FirstUserDTO) {
            handleFirstUserRequest((FirstUserDTO) receivedObject);
        } else {
            Log.e(this.getClass().getName(), "cannot cast class");
        }
    }

    private void handleCheating(CheatingDTO receivedObject) {
        long timestamp = System.currentTimeMillis();
        receivedObject.setIncomingServerTimeStamp(timestamp);
        serverController.addCheating(receivedObject);
    }

    /**
     * check if a user has cheated and send a user object back
     * If no one has cheated, then the user has to suspend a round himself
     *
     * @param receivedObject
     */
    private void handleCheatingSuspicion(final CheaterSuspicionDTO receivedObject) {
        //TODO check and send response
        final ClientData clientData = clientDataMap.get(receivedObject.getClientId());
        final CheaterSuspicionResponseDTO cheaterSuspicionResponseDTO = new CheaterSuspicionResponseDTO();

        // check
        serverController.checkCheating(receivedObject.getClientId(), new CallbackObject<CheatingDTO>() {
            @Override
            public void callback(CheatingDTO param) {
                if (param != null && param.getClientId() != 0 ){
                    cheaterSuspicionResponseDTO.setUser(clientDataMap.get(param.getClientId()).getUser());
                }else {
                    cheaterSuspicionResponseDTO.setUser(clientDataMap.get(receivedObject.getClientId()).getUser());
                }
                sendToClient(clientData, cheaterSuspicionResponseDTO);
            }
        });
    }

    private void handleFirstUserRequest(FirstUserDTO request) {
        TurnInfoDTO response = new TurnInfoDTO();
        response.setPlayerNextTurn(serverController.getUserForFirstTurn());

        // send update to requesting client
        sendToClient(clientDataMap.get(request.getClientId()), response);
    }

    private void handleFinishRoundRequest(FinishRoundDTO finish) {
        sendNextTurnInfo();
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
        // after the game config is finished clients will request the player for the first round themself

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
     * Broadcasts the info that the round ended to clients and server.
     * Also sends the user for the next turn and all shots from the current round.
     */
    private void sendNextTurnInfo() {
        // inform everyone that the current round ended
        User nextUser = serverController.getUserForNextTurn();
        List<TurnDTO> currentShots = serverController.getCurrentShots();

        TurnInfoDTO response = new TurnInfoDTO();
        response.setPlayerNextTurn(nextUser);
        response.setShots(currentShots);

        // send update to all clients
        sendToAllClients(response);

        // save for server
        if (currentTurnUserCallback != null)
            currentTurnUserCallback.callback(response);
        GlobalGameSettings.getCurrent().setUserOfCurrentTurn(nextUser);
    }

    /**
     * Broadcasts the user for the first turn to clients and server.
     */
    private void sendFirstTurnInfo() {
        User nextUser = serverController.getUserForFirstTurn();

        TurnInfoDTO turnInfo = new TurnInfoDTO();
        turnInfo.setPlayerNextTurn(nextUser);

        // send to clients
        sendToAllClients(turnInfo);

        // save server game instance
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.w("KryoServer", e.getMessage(), e);
        }

        if (currentTurnUserCallback != null)
            currentTurnUserCallback.callback(turnInfo);
        GlobalGameSettings.getCurrent().setUserOfCurrentTurn(nextUser);
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

        checkPossibleWinning();
    }

    @Override
    public int getNumberOfConnectedPlayers() {
        return clientDataMap.values().size();
    }

    // Network-Client methods

    @Override
    public void sendNameToServer(String username, CallbackObject<User> callback) {
        User user = serverController.checkName(username);
        callback.callback(user);
        usernameListToUI();
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
        sendQuitGame();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (clientDataMap != null) {
                    clientDataMap.clear();
                }
                kryoServer.close();
                kryoServer.stop();
                GlobalGameSettings.getCurrent().setServer(false);
                instance = new ServerGameHandlerKryoNet();

            }
        }, 2000);

    }

    private void sendQuitGame() {
        QuitGame quitGame = new QuitGame();
        sendToAllClients(quitGame);
    }

    /**
     * handles the shot from the serverplayer.
     *
     * @param area
     * @param col
     * @param row
     * @param callback
     */
    @Override
    public void sendShotOnEnemyToServer(BattleArea area, int col, int row, CallbackObject<TurnDTO> callback) {
        TurnDTO hitType = new TurnDTO(TurnDTO.TurnType.SHOT, area);

        hitType.setxCoordinates(row);
        hitType.setyCoordinates(col);
        hitType = serverController.checkShot(hitType);
        callback.callback(hitType);

        checkPossibleWinning();
    }

    private void checkPossibleWinning() {
        User winner = serverController.checkForWinner();
        if (winner != null) {
            // we have a winner :D

            // send latest shot list
            sendNextTurnInfo();

            // inform everyone
            WinnerDTO wdto = new WinnerDTO();
            wdto.setWinner(winner);

            sendToAllClients(wdto);

            if (winnerCallback != null)
                winnerCallback.callback(winner);
        }
    }

    @Override
    public void sendFinish() {
        // update info about the turn
        sendNextTurnInfo();
    }

    @Override
    public void sendFirstUserRequestToServer() {
        // respond to the server
        User user = serverController.getUserForFirstTurn();

        GlobalGameSettings.getCurrent().setUserOfCurrentTurn(user);

        if (currentTurnUserCallback != null) {
            TurnInfoDTO ti = new TurnInfoDTO();
            ti.setPlayerNextTurn(user);
            currentTurnUserCallback.callback(ti);
        }
    }

    @Override
    public void registerForWinnerInfos(CallbackObject<User> winnerCb) {
        winnerCallback = winnerCb;
    }

    @Override
    public void sendCheatingSuspicion(final CallbackObject<User> callback) {
        int id = GlobalGameSettings.getCurrent().getPlayerId();

        serverController.checkCheating(id, new CallbackObject<CheatingDTO>() {
            @Override
            public void callback(CheatingDTO param) {
                if (param != null ){
                    ClientData data = clientDataMap.get(param.getClientId());
                    callback.callback(data.getUser());
                }
            }
        });
    }

    @Override
    public void registerQuitInfo(CallbackObject<Boolean> callback) {

    }

    @Override
    public void registerForCurrentTurnUserUpdates(CallbackObject<TurnInfoDTO> currentTurnUserCallback) {
        this.currentTurnUserCallback = currentTurnUserCallback;
    }

    //initializes a new CheatingDTO - sets ClientID and the timestamp
    @Override
    public void sendCheatingToServer(CallbackObject<Boolean> callback) {
        CheatingDTO cheatingDTO = new CheatingDTO();
        cheatingDTO.setClientId(GlobalGameSettings.getCurrent().getPlayerId());
        cheatingDTO.setIncomingServerTimeStamp(System.currentTimeMillis());
        serverController.addCheating(cheatingDTO);
    }

}
