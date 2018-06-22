package at.aau.gloryweapons.siegeanddestroy3d.server;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.validation.ValidationHelperClass;

// TODO more tests for creating the GameConfig etc
public class ServerController {

    private AtomicInteger id = new AtomicInteger(1);
    private ArrayList<String> names = new ArrayList<>();
    private GameConfiguration gameConfig;
    private ArrayList<User> users = new ArrayList<>(4);
    private List<BattleArea> battleAreas = new ArrayList<>(4);
    private List<CallbackObject<GameConfiguration>> callbacks = new ArrayList<>(4);
    private int shots = 0;
    private ArrayList<User> penaltyList = new ArrayList<>();

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

    private int getId() {
        return id.getAndAdd(1);
    }

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

        Log.i(this.getClass().toString(), "addDataToGameConfig number players: " + GlobalGameSettings.getCurrent().getNumberPlayers());

        if (users.size() == GlobalGameSettings.getCurrent().getNumberPlayers()) {
            // all players finished placement
            gameConfig = new GameConfiguration();
            gameConfig.setUserList(users);
            gameConfig.setBattleAreaList(battleAreas);
            gameConfig.setShots(GlobalGameSettings.getCurrent().getNumberShots());

            // inform the clients about the game config
            for (CallbackObject<GameConfiguration> cb : callbacks)
                if (cb != null)
                    cb.callback(gameConfig);

            callbacks.clear();
        }
    }

    private int userIdxForCurrentTurn = 0;

    /**
     * A user is decided to be the first player. Decision is random.
     *
     * @return The user to use first
     */
    public User getUserForFirstTurn() {
        // todo
        // decide per random so nobody is preferred
        userIdxForCurrentTurn = 0;//new Random().nextInt(users.size());
        return users.get(userIdxForCurrentTurn);
    }

    /**
     * The next user in the order is returned.
     *
     * @return
     */
    public User getUserForNextTurn() {
        // just loop through
        userIdxForCurrentTurn = (userIdxForCurrentTurn + 1) % users.size();
        if (suspendATurn(users.get(userIdxForCurrentTurn))) {
            getUserForNextTurn();
        }
        return users.get(userIdxForCurrentTurn);
    }

    /**
     * Checks if a user has to suspend a turn.
     *
     * @param user
     * @return
     */
    private boolean suspendATurn(User user) {
        return penaltyList.remove(user);
    }

    /**
     * checks the tile and sets the type of the TurnDTO
     *
     * @param hit
     * @return
     */
    public TurnDTO checkShot(TurnDTO hit) {

        BattleAreaTile tile;
        for (BattleArea area : battleAreas) {
            if (hit.getUserId() == area.getUserId()) {
                tile = area.getBattleAreaTiles()[hit.getxCoordinates()][hit.getyCoordinates()];
                tile = checkTile(tile);
                if (tile.getType() == BattleAreaTile.TileType.NO_HIT) {
                    hit.setType(TurnDTO.TurnType.NO_HIT);
                    area.getBattleAreaTiles()[hit.getxCoordinates()][hit.getyCoordinates()].setType(BattleAreaTile.TileType.NO_HIT);
                } else {
                    hit.setType(TurnDTO.TurnType.HIT);
                    area.getBattleAreaTiles()[hit.getxCoordinates()][hit.getyCoordinates()].setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                    checkBattleAreaForDefeat(area);
                }
            }
        }

        return hit;
    }

    /**
     * checks the type of the enemies Tile on the server
     *
     * @param tile
     * @return
     */
    private BattleAreaTile checkTile(BattleAreaTile tile) {
        switch (tile.getType()) {
            case WATER:
                tile.setType(BattleAreaTile.TileType.NO_HIT);
                break;
            case NO_HIT:
                break;
            case SHIP_START:
                tile.setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                break;
            case SHIP_MIDDLE:
                tile.setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                break;
            case SHIP_END:
                tile.setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                break;
            case SHIP_DESTROYED:
                break;
        }
        return tile;

    }

    /**
     * saves Shots in gameConfig
     *
     * @param shots
     */
    public void sentShots(int shots) {

        this.shots = shots;
    }

    /**
     * Checks if the BattleArea is destroyed. Also updates the User to defeated.
     *
     * @param area
     * @return
     */
    public boolean checkBattleAreaForDefeat(BattleArea area) {
        if (area == null)
            return false;

        if (area.remainingFields() == 0) {
            setUserDefeated(area.getUserId());
            return true;
        } else
            return false;
    }

    public void setUserDefeated(int userId) {
        for (User u : users)
            if (u.getId() == userId)
                u.setDefeated(true);
    }

    /**
     * Checks if someone won already.
     *
     * @return User who won, null otherwise.
     */
    public User checkForWinner() {
        User uNotDefeated = null;

        // check for all users if they are defeated. If more than one is not defeated, there is no winner yet.
        for (User u : users) {
            if (!u.isDefeated())
                if (uNotDefeated == null) {
                    // this is the first one.
                    uNotDefeated = u;
                } else {
                    // there was already another
                    return null;
                }
        }

        return uNotDefeated;
    }
}

