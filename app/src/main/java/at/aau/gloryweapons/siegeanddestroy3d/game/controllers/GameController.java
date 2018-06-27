package at.aau.gloryweapons.siegeanddestroy3d.game.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.ReturnObject;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.ShipContainer;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheaterSuspicionResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheatingResponseDTO;
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
                        // shot hit
                        tile = BattleAreaTile.getDestroyedVersionOfShipTile(enemyArea.getBattleAreaTiles()[row][col].getType());
                        enemyArea.getBattleAreaTiles()[row][col].setType(tile);
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
        if (!enemyArea.getBattleAreaTiles()[row][col].isDestroyed() && enemyArea.getBattleAreaTiles()[row][col].getType() != BattleAreaTile.TileType.NO_HIT) {
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

    public void cheatingSuspicion(final CallbackObject<CheaterSuspicionResponseDTO> cheatingSuspicionCallback) {
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
     *
     * @param callback
     */
    public void sendCheating(CallbackObject<CheatingResponseDTO> callback) {
        communicator.sendCheatingToServer(callback);
    }


    //searches in the Container for the weakest ship and returns it
    public ShipContainer findWeakestShip(BattleArea battleArea) {
        ShipContainer containerWeakestShip = null;

        for (ShipContainer shipDetails: battleArea.getShipList()) {
            checkShip(shipDetails, battleArea);
            if (containerWeakestShip != null && containerWeakestShip.getCurrentLength() > shipDetails.getCurrentLength() && shipDetails.getCurrentLength() != 0 ){
                containerWeakestShip = shipDetails;
            }else if (containerWeakestShip == null && shipDetails.getCurrentLength() > 0){
                containerWeakestShip = shipDetails;
            }
        }

        return containerWeakestShip;
    }

    /**
     * gets Ship, checks if horizontal oder vertical - gets Row and Col
     * checks if alive (no water, not already hit)
     * @param shipDetails
     */
    public void checkShip(ShipContainer shipDetails, BattleArea battleArea) {
        BattleAreaTile[][] battleAreaTiles = battleArea.getBattleAreaTiles();

        List<Integer> randomPosition = new ArrayList<>();
        BasicShip ship = shipDetails.getShip();
        int currentLength = 0;
        for (int i = 0; i < ship.getLength(); i++) {
            if (ship.isHorizontal()){
                if (battleAreaTiles[shipDetails.getRow()][shipDetails.getCol() + i].isAlive()){
                    currentLength++;
                    randomPosition.add(shipDetails.getCol() + i);
                }
            }else {
                if (battleAreaTiles[shipDetails.getRow() + i][shipDetails.getCol()].isAlive()){
                    currentLength++;
                    randomPosition.add(shipDetails.getRow() + i);
                }
            }
        }
        //set alive length
        shipDetails.setCurrentLength(currentLength);

        //set random position for cheating
        if (currentLength > 0){
            setRandom(randomPosition,shipDetails );
        }
    }

    //finding a random point of attack
    private void setRandom(List<Integer> randomPosition, ShipContainer container){
        Random random = new Random();
        int index = random.nextInt(randomPosition.size());

        if (container.getShip().isHorizontal()){
            container.setRowCheating(container.getRow());
            container.setColCheating(randomPosition.get(index));
        }else {
            container.setColCheating(container.getCol());
            container.setRowCheating(randomPosition.get(index));
        }
    }

}
