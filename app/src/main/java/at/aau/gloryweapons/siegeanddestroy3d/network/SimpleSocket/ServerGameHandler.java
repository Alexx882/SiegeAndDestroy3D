package at.aau.gloryweapons.siegeanddestroy3d.network.SimpleSocket;

import android.app.Activity;
import android.util.Log;

import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import java.util.ArrayList;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.InstructionDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.RequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.ResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;


public class ServerGameHandler {

    private List<BattleArea> battleAreas;
    private List<User> users;
    private User currentUser;
    private boolean gameStarted;
    private GlobalGameSettings globalGameSettings;
    private Activity activity;
    private SalutDataCallback dataCallback;
    private Salut network;
    private NetworkType currentType = null;


    //SimpleServerSocket
    private SimpleSocketServer socketServer;
    private ServerCallBack request;

    private static ServerGameHandler instance;

    public static ServerGameHandler getInstance() {
        if (instance == null) {
            instance = new ServerGameHandler();
        }
        return instance;
    }

    public enum NetworkType {
        WIFI_DIRECT_SALUT,
        SOCKET,
        TEST
    }

    /**
     * initializes a server with a simple socket connection
     */
    public void initSimpleSocketServer() {
        currentType = NetworkType.SOCKET;
        socketServer = new SimpleSocketServer();

        socketServer.initServer(new ServerCallBack() {
            @Override
            public void callback(Object object) {
                requestMapper(object);
            }
        });
        Thread thread = new Thread(socketServer);
        thread.start();

    }

    /**
     * send to client
     *
     * @param id     User id
     * @param object
     */
    private void sendToClient(int id, Object object) {
        switch (currentType) {
            case SOCKET:
                socketServer.sentToClient(id, object);
                break;
            case WIFI_DIRECT_SALUT:
                //TODO send to client
                break;
            case TEST:
                //TODO test
            default:
                Log.e(this.getClass().getName(), "no connection type selected");
                break;
        }
    }

    /***
     * send to all users
     *
     * @param object
     */
    private void sendToAllClients(Object object) {
        switch (currentType) {
            case SOCKET:
                socketServer.sendToAllClients(object);
                break;
            case WIFI_DIRECT_SALUT:
                //TODO send to client
                break;
            case TEST:
                //TODO test
            default:
                Log.e(this.getClass().getName(), "no connection type selected");
                break;
        }
    }

    //incoming client request
    private void requestMapper(Object object) {
        Log.i(this.getClass().getName(), "request mapper called...");
        if (object instanceof RequestDTO) {
            RequestDTO requestDTO = (RequestDTO) object;
            requestDtoHandler(requestDTO);
        } else {
            Log.e(this.getClass().getName(), "cannot read " + object.getClass().getName());
        }

        /*
        if (isCurrentUser(turn.getUser())) {
            switch (turn.getType()) {
                case SHOT:
                    checkAndProcessShot(turn);
                    break;

                case POWERUP:
                    checkAndProcessPowerUp(turn);
                    break;

                default:
                    //TODO Error
                    break;
            }
        } else {
            turn.setType(TurnDTO.TurnType.ERROR);
            response(turn);
        }
        */
    }

    private void requestDtoHandler(RequestDTO requestDTO) {
        switch (requestDTO.getType()) {
            case CHECK_USERNAME:
                User user = (User) requestDTO.getTransferObject();

                if (userNameIsAvailable(user)) {
                    ResponseDTO responseDTO = new ResponseDTO(ResponseDTO.ResponseType.RESPONSE_CHECK_USERNAME, user);
                    sendToClient(user.getId(), responseDTO);
                } else {
                    user.setName(null);
                    ResponseDTO responseDTO = new ResponseDTO(ResponseDTO.ResponseType.RESPONSE_CHECK_USERNAME, user);
                    sendToClient(user.getId(), user);
                }
                break;
            default:
                break;
        }
    }


    /***
     * init a server with wifi direct(salut)
     * @param activity
     */
    public void initServer(Activity activity) {
        this.battleAreas = new ArrayList<>();
        this.users = new ArrayList<>();
        this.globalGameSettings = GlobalGameSettings.getCurrent();
        this.gameStarted = false;
        this.activity = activity;
        this.dataCallback = dataCallback;


        //init service
        SalutDataReceiver dataReceiver = new SalutDataReceiver(activity, new SalutDataCallback() {
            @Override
            public void onDataReceived(Object o) {
                Log.i(this.getClass().getName(), o.toString());
            }
        });
        SalutServiceData serviceData = new SalutServiceData(GlobalGameSettings.getCurrent().getSERVICE_NAME(),
                GlobalGameSettings.getCurrent().getPort(),
                "Host");

        //wifi direct support
        this.network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                Log.e(this.getClass().getName(), "Sorry, but this device does not support WiFi Direct.");
            }
        });

        //network discover
        network.startNetworkService(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice device) {
                Log.i(this.getClass().getName(), device.readableName + " has connected!");
            }
        });
    }

    /***
     * returns a true value if the game has started
     * @return
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        this.gameStarted = true;
    }

    @Deprecated
    public void addUser(User user) {
        users.add(user);
        if (users.size() == 1) {
            currentUser = users.get(0);
        }
    }

    private boolean userNameIsAvailable(User checkUsername) {
        for (User user : users) {
            if (checkUsername.getName().toLowerCase().equals(user.getName().toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    private List<User> getUsers() {
        return users;
    }

    public boolean addBattleArea(BattleArea battleArea) {
        if (checktBattleArea(battleArea)) {
            battleAreas.add(battleArea);
            return true;
        } else if (!userExists(battleArea.getUserId())) {
            //TODO send ERROR
            return false;
        } else {
            //TODO send ERROR
            return false;
        }
    }

    public User nextUser() {
        if (currentUser.getId() == users.get(users.size() - 1).getId()) {
            currentUser = users.get(0);
            return currentUser;
        } else {
            currentUser = users.get(users.indexOf(currentUser) + 1);
            return currentUser;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private boolean isCurrentUser(User user) {
        if (user.getId() == currentUser.getId()) {
            return true;
        } else {
            return false;
        }
    }

    //check battlearea
    private boolean checktBattleArea(BattleArea battleArea) {
        int requiredTiles = 0;
        int occupiedTiels = 0;

        for (int size : globalGameSettings.getShipSizes()) {
            requiredTiles += size;
        }

        for (int i = 0; i < battleArea.getBattleAreaTiles().length; i++) {
            for (int j = 0; j < battleArea.getBattleAreaTiles().length; j++) {
                if (battleArea.getBattleAreaTiles()[i][j].getType() == BattleAreaTile.TileType.SHIP_HEALTHY) {
                    occupiedTiels += 1;
                }
            }
        }

        if (occupiedTiels != requiredTiles) {
            return false;
        } else {
            return true;
        }
    }

    private boolean userExists(int id) {
        if (users != null) {
            for (User user : users) {
                if (user.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    private BattleArea getBattleAreaByUserId(int id) {
        if (battleAreas != null) {
            for (BattleArea battleArea : battleAreas) {
                if (battleArea.getUserId() == id) {
                    return battleArea;
                }
            }
        }
        return null;
    }


    private void checkAndProcessShot(TurnDTO turnDTO) {
        if ((getBattleAreaByUserId(turnDTO.getAttacksUserID()) != null) && userExists(turnDTO.getAttacksUserID())) {

            BattleArea attackedArea = getBattleAreaByUserId(turnDTO.getAttacksUserID());
            BattleAreaTile attackedTile = attackedArea.getBattleAreaTiles()[turnDTO.getxCoordinates()][turnDTO.getyCoordinates()];

            switch (attackedTile.getType()) {
                case WATER:
                    attackedTile.setType(BattleAreaTile.TileType.NO_HIT);
                    turnDTO.setType(TurnDTO.TurnType.NO_HIT);
                    break;
                case SHIP_HEALTHY:
                    attackedTile.setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                    turnDTO.setType(TurnDTO.TurnType.HIT);
                    break;
                case SHIP_DESTROYED:
                    turnDTO.setType(TurnDTO.TurnType.HIT);
                    break;
                default:
                    turnDTO.setType(TurnDTO.TurnType.ERROR);
                    break;
            }

        } else {
            turnDTO.setType(TurnDTO.TurnType.ERROR);
        }

        //send turnDTO back
        //sendToClient(turnDTO);
        if (getUserById(turnDTO.getAttacksUserID()) != null && isAlive(getUserById(turnDTO.getAttacksUserID()))) {
            nextUser();
        } else {
            InstructionDTO instructionToall = new InstructionDTO(InstructionDTO.InstructionType.USER_DEAD);
            //TODO send to client / all
        }

        sendInstructionRequestToClient();

    }

    private void checkAndProcessPowerUp(TurnDTO turnDTO) {
        //TODO do something
    }

    private User getUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }


    public void sendToAll(InstructionDTO instruction) {
        //TODO send instruction to all clients
    }

    //checks whether the user still has a ship available
    private boolean isAlive(User user) {
        BattleArea area = getBattleAreaByUserId(user.getId());
        if (area != null) {

            for (int i = 0; i < area.getBattleAreaTiles().length; i++) {
                for (int j = 0; j < area.getBattleAreaTiles().length; j++) {
                    if (area.getBattleAreaTiles()[i][j].getType() == BattleAreaTile.TileType.SHIP_HEALTHY) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    private void sendInstructionRequestToClient() {
        InstructionDTO instruction = new InstructionDTO(InstructionDTO.InstructionType.DO_TURN);
        //TODO send to next client
    }

}
