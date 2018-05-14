package at.aau.gloryweapons.siegeanddestroy3d.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.validation.ValidationHelperClass;

// TODO more tests for creating the GameConfig etc
public class ServerController {

    private AtomicInteger id = new AtomicInteger(1);
    private ArrayList<String> names = new ArrayList<>();

    /**
     * Checks if the name for an user is available.
     *
     * @param name The name to check
     * @return User object if the name is available.
     */
    public synchronized User checkName(String name) {
        //check name
        if (ValidationHelperClass.isUserNameValid(name)) {

        } else {
            return null;
        }

        //check if name is unique
        for (String n : names) {
            if (n.equals(name)) {
                return null;

            }
        }

        //name is unique, return user object
        names.add(name);
        User user = new User();
        user.setName(name);
        user.setId(getId());
        return user;
    }

    //getID and always add 1
    private int getId() {
        return id.getAndAdd(1);
    }

    private GameConfiguration gameConfig;
    private List<User> users = new ArrayList<>(4);
    private List<BattleArea> battleAreas = new ArrayList<>(4);
    private List<CallbackObject<GameConfiguration>> callbacks = new ArrayList<>(4);

    /**
     * Adds the user and his battle area to the gameconfig.
     * If all connected players registered their areas the created gameconfig is returned in the callback.
     *
     * @param user
     * @param battleArea
     * @param placedShips
     * @param callback    Returns the complete gameconfig once all players are ready.
     */
    public synchronized void addDataToGameConfig(User user, BattleArea battleArea,
                                                 List<BasicShip> placedShips, CallbackObject<GameConfiguration> callback) {
        // prepare data for config
        users.add(user);
        battleAreas.add(battleArea);
        callbacks.add(callback);

        if (users.size() == GlobalGameSettings.getCurrent().getNumberPlayers()) {
            // all players finished placement
            gameConfig = new GameConfiguration();
            gameConfig.setUserList(users);
            gameConfig.setBattleAreaList(battleAreas);

            for (CallbackObject<GameConfiguration> cb : callbacks)
                if (cb != null)
                    cb.callback(gameConfig);
        }
    }

}

