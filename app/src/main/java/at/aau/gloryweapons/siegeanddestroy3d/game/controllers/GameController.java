package at.aau.gloryweapons.siegeanddestroy3d.game.controllers;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.ReturnObject;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ClientGameHandlerKryoNet;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorClient;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ServerGameHandlerKryoNet;

public class GameController {

    private NetworkCommunicatorClient communicator;
    private static int shotsFired = 0; // todo remove static ?

    public GameController() {
        communicator = GlobalGameSettings.getCurrent().isServer()
                ? ServerGameHandlerKryoNet.getInstance()
                : ClientGameHandlerKryoNet.getInstance();
    }

    /**
     * @param game
     * @param enemyArea
     * @param enemy
     * @param col
     * @param row
     * @return
     */
    public void shotOnEnemy(GameConfiguration game, final BattleArea enemyArea, User enemy, final int col, final int row, final CallbackObject<ReturnObject> callback) {

        final ReturnObject object = new ReturnObject();
        if (checkIfMyTurn()) {
            if (enemy.getId() != GlobalGameSettings.getCurrent().getPlayerId()) {
                if (enemyArea.getBattleAreaTiles()[row][col].getType() != BattleAreaTile.TileType.SHIP_DESTROYED && enemyArea.getBattleAreaTiles()[row][col].getType() != BattleAreaTile.TileType.NO_HIT) {
                    if (shotsFired < GlobalGameSettings.getCurrent().getNumberShots()) {

                        shotsFired++;

                        communicator.sendShotOnEnemyToServer(enemyArea, col, row, new CallbackObject<TurnDTO>() {
                            @Override
                            public void callback(TurnDTO t) {
                                BattleAreaTile.TileType tile = null;
                                switch (t.getType()) {
                                    case NO_HIT:
                                        tile = BattleAreaTile.TileType.NO_HIT;
                                        enemyArea.getBattleAreaTiles()[row][col].setType(BattleAreaTile.TileType.NO_HIT);
                                        break;
                                    case HIT:
                                        tile = BattleAreaTile.TileType.SHIP_DESTROYED;
                                        enemyArea.getBattleAreaTiles()[row][col].setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                                        break;
                                }
                                object.setTile(tile);
                                callback.callback(object);

                            }
                        });
                        return;
                    } else {
                        object.setI(1);
                        object.setMessage("alle Schüsse abgegeben. runde beenden");
                    }
                } else {
                    object.setI(2);
                    object.setMessage("dieses Feld ist bereits zerstört");
                }
            } else {
                object.setI(3);
                object.setMessage("nix selbstmord. nein :3");
            }
        } else {
            object.setI(4);
            object.setMessage("du nix dran");
        }
        callback.callback(object);

    }

    /**
     * checks if its the turn of the player.
     *
     * @return
     */
    private boolean checkIfMyTurn() {
        if (GlobalGameSettings.getCurrent().getUserOfCurrentTurn() != null)
            if (GlobalGameSettings.getCurrent().getPlayerId() == GlobalGameSettings.getCurrent().getUserOfCurrentTurn().getId()) {
                return true;
            }
        return false;
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
}
