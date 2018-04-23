package at.aau.gloryweapons.siegeanddestroy3d.network.SimpleSocket;

import android.app.Activity;
import android.util.Log;

import com.peak.salut.Salut;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.InstructionDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.RequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.ResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;

public class ClientGameHandler implements NetworkCommunicator {

    private Salut network;
    private static ClientGameHandler instance;
    private ServerGameHandler.NetworkType type;
    private SimpleSocketClient serverConnection;
    private Activity activity;

    //callbacks
    private CallbackObject<User> userIdDTOCallback;
    private CallbackObject<User> userNameDTOCallback;

    private CallbackObject<InstructionDTO> userInstructionCallback;

    public static ClientGameHandler getInstance() {
        if (instance == null) {
            instance = new ClientGameHandler();
        }
        return instance;
    }

    /***
     * init a simple socket client
     * @param ip    Server IP
     */
    public void initSimpleSocketClient(String ip) {
        type = ServerGameHandler.NetworkType.SOCKET;

        serverConnection = new SimpleSocketClient();
        serverConnection.initClient(ip, new ClientCallBack() {
            @Override
            public void callback(Object object) {
                //TODO implment
            }
        });
        Thread thread = new Thread(serverConnection);
        thread.start();


    }


    @Override
    public void sendNameToServer(User user, CallbackObject<User> callback) {
        userNameDTOCallback = callback;

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setType(RequestDTO.RequestType.CHECK_USERNAME);
        requestDTO.setTransferObject(user);
        sendToServer(requestDTO);
    }

    @Override
    public void sendGameConfigurationToServer(User user, BattleArea userBoard, List<BasicShip> placedShips, CallbackObject<GameConfiguration> callback) {

    }

    @Override
    public void receiveServerMessages(CallbackObject<InstructionDTO> callback) {
        this.userInstructionCallback = callback;
    }

    @Override
    public void getUserId(CallbackObject<User> callback) {
        userIdDTOCallback = callback;

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setType(RequestDTO.RequestType.GET_ID);
        sendToServer(requestDTO);
    }

    /***
     * Send request to the server
     * @param object
     */
    private void sendToServer(Object object) {
        serverConnection.sentToServer(object);
    }

    /***
     *
     *
     * @param object
     */
    private void requestMapper(Object object) {

        if (object instanceof ResponseDTO) {
            responseDTOcallBackMapper((ResponseDTO) object);
        } else if (object instanceof InstructionDTO) {
            //TODO
        } else {
            Log.e(this.getClass().getName(), "class error! Class cannot be assigned...:" + object.getClass().getName());
        }
    }

    private void responseDTOcallBackMapper(ResponseDTO responseDTO) {
        switch (responseDTO.getType()) {
            case RESPONSE_GET_ID:
                User userResponseID = (User) responseDTO.getTransferObject();
                userIdDTOCallback.callback(userResponseID);
                break;
            case RESPONSE_CHECK_USERNAME:
                User userResponseName = (User) responseDTO.getTransferObject();
                userNameDTOCallback.callback(userResponseName);
                break;
            default:
                break;
        }
    }
}
