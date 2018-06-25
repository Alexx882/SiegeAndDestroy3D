package at.aau.gloryweapons.siegeanddestroy3d.game.controllers;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.ReturnObject;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnInfoDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ClientGameHandlerKryoNet;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorClient;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ServerGameHandlerKryoNet;

public class GameController {

    private NetworkCommunicatorClient communicator;
    private int shotsFired = 0;

    public GameController() {
        communicator = GlobalGameSettings.getCurrent().isServer()
                ? ServerGameHandlerKryoNet.getInstance()
                : ClientGameHandlerKryoNet.getInstance();
    }

    public GameController(CallbackObject<Boolean> endGame) {
        this();
        communicator.registerQuitInfo(endGame);
    }

    /**
     * checks the shot
     *
     * @param enemyArea
     * @param enemy
     * @param col
     * @param row
     * @param callback
     */
    public void shotOnEnemy(final BattleArea enemyArea, User enemy, final int col, final int row, final CallbackObject<ReturnObject> callback) {

        final ReturnObject object = new ReturnObject();
        String message = null;


        message = checkIfMyTurn();
        if (message != null) {
            object.setI(4);
            object.setMessage(message);
        }


        message = checkIfSuicide(enemy);
        if (message != null && object.getMessage() == null) {
            object.setI(3);
            object.setMessage(message);
        }


        message = checkIfTileDestroyed(enemyArea, col, row);
        if (message != null && object.getMessage() == null) {
            object.setI(2);
            object.setMessage(message);
        }

        message = checkIfShots();
        if (message != null && object.getMessage() == null) {
            object.setI(2);
            object.setMessage(message);
        }

        if (checkIfMyTurn() == null && checkIfSuicide(enemy) == null && checkIfTileDestroyed(enemyArea, col, row) == null && checkIfShots() == null) {

            shotsFired++;

            communicator.sendShotOnEnemyToServer(enemyArea, col, row, new CallbackObject<TurnDTO>() {
                @Override
                public void callback(TurnDTO t) {
                    BattleAreaTile.TileType tile = null;
                    if (t.getType() == TurnDTO.TurnType.NO_HIT) {
                        tile = BattleAreaTile.TileType.NO_HIT;
                        enemyArea.getBattleAreaTiles()[row][col].setType(BattleAreaTile.TileType.NO_HIT);
                    } else {
                        tile = BattleAreaTile.TileType.SHIP_DESTROYED;
                        enemyArea.getBattleAreaTiles()[row][col].setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                    }
                    object.setTile(tile);
                    callback.callback(object);

                }
            });
            return;
        }
        callback.callback(object);
    }

    /**
     * check if shots left
     *
     * @return
     */
    public String checkIfShots() {
        if (shotsFired < GlobalGameSettings.getCurrent().getNumberShots()) {
            return null;
        } else {
            return "alle Schüsse abgegeben. runde beenden";
        }
    }

    /**
     * checks if the Field is already destroyed
     *
     * @param enemyArea
     * @param col
     * @param row
     * @return
     */
    public String checkIfTileDestroyed(BattleArea enemyArea, int col, int row) {
        if (enemyArea.getBattleAreaTiles()[row][col].getType() != BattleAreaTile.TileType.SHIP_DESTROYED && enemyArea.getBattleAreaTiles()[row][col].getType() != BattleAreaTile.TileType.NO_HIT) {
            return null;
        } else {
            return "dieses Feld ist bereits zerstört";
        }
    }

    /**
     * checks if the player tries to shoot himself
     *
     * @param enemy
     * @return
     */
    public String checkIfSuicide(User enemy) {
        if (enemy.getId() != GlobalGameSettings.getCurrent().getPlayerId()) {
            return null;
        } else {
            return "kein Selbstbeschuss";
        }

    }

    /**
     * checks if its the turn of the player.
     *
     * @return
     */
    public String checkIfMyTurn() {
        if (GlobalGameSettings.getCurrent().getUserOfCurrentTurn() != null && GlobalGameSettings.getCurrent().getPlayerId() == GlobalGameSettings.getCurrent().getUserOfCurrentTurn().getId()) {

            return null;
        }

        return "Nicht deine Runde";
    }

    /**
     * ends the players turn; checks if all available shots are fired before
     *
     * @return
     */
    public boolean endTurn() {
        if (shotsFired == GlobalGameSettings.getCurrent().getNumberShots()) {
            shotsFired = 0;
            communicator.sendFinish();

            return true;
        } else {
            return false;
        }
    }

    public void cheatingSuspicion(final CallbackObject<User> cheatingSuspicionCallback) {
        communicator.sendCheatingSuspicion(cheatingSuspicionCallback);
    }


    public void registerForWinningInfos(CallbackObject<User> winnerCallback) {
        communicator.registerForWinnerInfos(winnerCallback);
    }

    public void cleanup() {
        communicator.resetNetwork();
    }

    /**
     * Registers for current turn user updates and requests the first update from the server.
     *
     * @param currentTurnUserCallback
     */
    public void registerForCurrentTurnUserUpdates(CallbackObject<TurnInfoDTO> currentTurnUserCallback) {
        communicator.registerForCurrentTurnUserUpdates(currentTurnUserCallback);

        communicator.sendFirstUserRequestToServer();
    }

    public void updateBattleAreaFromShotList(BattleArea area, List<TurnDTO> shots) {
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);
    }

    /**
     * sends cheating to server
     * @param callback
     */
    public void sendCheating(CallbackObject<Boolean> callback){
        communicator.sendCheatingToServer(callback);
    }
}
