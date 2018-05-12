package at.aau.gloryweapons.siegeanddestroy3d.game;

import android.widget.Toast;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;
import at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect.ClientGameHandlerWifi;

public class GameController {

    private NetworkCommunicator communicator = new ClientGameHandlerWifi();
    private static int shotsFired = 0;

    /**
     * @param game
     * @param enemyArea
     * @param enemy
     * @param col
     * @param row
     * @return
     */
    public BattleAreaTile.TileType shotOnEnemy(GameConfiguration game, BattleArea enemyArea, User enemy, int col, int row) {
        BattleAreaTile.TileType tile = null;
        if (checkIfMyTurn()) {
            if (enemy.getId() != GlobalGameSettings.getCurrent().getPlayerId()) {
                if (shotsFired <= game.getShots()) {

                    shotsFired++;
                    TurnDTO t = null;
                    t = communicator.sendShotOnEnemyToServer(enemy, col, row);
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
                }
            }

        } else {
            return null;

        }
        return tile;
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
