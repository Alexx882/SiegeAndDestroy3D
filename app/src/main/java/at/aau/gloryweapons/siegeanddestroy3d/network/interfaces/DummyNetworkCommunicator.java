package at.aau.gloryweapons.siegeanddestroy3d.network.interfaces;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * Created by Alexander on 17.04.2018.
 */

public class DummyNetworkCommunicator implements NetworkCommunicator {
    @Override
    public void sendNameToServer(String name, CallbackObject<User> callback) {

    }

    @Override
    public <T> void sendGameConfigurationToServer(User user, BattleArea userBoard, List<BasicShip> placedShips, CallbackObject<T> callback) {

    }
}
