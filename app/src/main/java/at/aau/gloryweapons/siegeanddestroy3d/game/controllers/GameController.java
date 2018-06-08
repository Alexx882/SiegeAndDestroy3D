package at.aau.gloryweapons.siegeanddestroy3d.game.controllers;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ClientGameHandlerKryoNet;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorClient;

public class GameController {

    private NetworkCommunicatorClient communicator = ClientGameHandlerKryoNet.getInstance();
    private static int shotsFired = 0;

    /**
     * @param game
     * @param enemyArea
     * @param enemy
     * @param col
     * @param row
     * @return
     */
    public void shotOnEnemy(GameConfiguration game, final BattleArea enemyArea, User enemy, final int col, final int row, final CallbackObject<BattleAreaTile.TileType> callback) {

        if (checkIfMyTurn()) {
            if (enemy.getId() != GlobalGameSettings.getCurrent().getPlayerId()) {
                if (enemyArea.getBattleAreaTiles()[row][col].getType() != BattleAreaTile.TileType.SHIP_DESTROYED) {
                    if (shotsFired <= game.getShots()) {

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
                                callback.callback(tile);
                            }
                        });

                    }
                }
            }

        } else {
            callback.callback(null);
        }

    }

    /**
     * checks if its the turn of the player.
     *
     * @return
     */
    private boolean checkIfMyTurn() {
        if (GlobalGameSettings.getCurrent().getPlayerId() == GlobalGameSettings.getCurrent().getUserOfCurrentTurn().getId()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ends the players turn; checks if all available shots are fired before
     *
     * @param game
     * @return
     */
    public boolean endTurn(GameConfiguration game) {
        if (shotsFired == game.getShots()) {
            shotsFired = 0;
            //todo server shit.. xD

            return true;
        } else {
            return false;
        }
    }

    public void RegisterForTurnInfoUpdates(){
        communicator.registerForTurnInfos(new CallbackObject<User>() {
            @Override
            public void callback(User param) {
                // TODO handle next user for turn
            }
        });
    }
}
