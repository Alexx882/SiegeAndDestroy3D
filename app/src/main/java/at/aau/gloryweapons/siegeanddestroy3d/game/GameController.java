package at.aau.gloryweapons.siegeanddestroy3d.game;

import android.widget.Toast;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.asyncCommunication.ClientGameHandlerAsyncCommunication;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;

public class GameController {

    private NetworkCommunicator communicator = ClientGameHandlerAsyncCommunication.getInstance();
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
        if (GlobalGameSettings.getCurrent().getPlayerId() == GlobalGameSettings.getCurrent().getActualUser().getId()) {
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
}
