package at.aau.gloryweapons.siegeanddestroy3d.game;

import android.widget.Toast;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;
import at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect.ClientGameHandlerWifi;

public class GameController {

    private NetworkCommunicator communicator = new ClientGameHandlerWifi();

    /**
     * returns if hit or no hit
     *
     * @param game
     * @param enemy
     * @param col
     * @param row
     * @return
     */
    public BattleAreaTile.TileType shotOnEnemy(GameConfiguration game, User enemy, int col, int row) {
        BattleAreaTile.TileType tile = null;
        if (enemy.getId() != GlobalGameSettings.getCurrent().getPlayerId()) {
            TurnDTO t = null;
            t = communicator.sendShotOnEnemyToServer(enemy, col, row);
            switch (t.getType()) {
                case NO_HIT:
                    tile = BattleAreaTile.TileType.NO_HIT;
                    break;
                case HIT:
                    tile = BattleAreaTile.TileType.SHIP_DESTROYED;
                    break;
            }

        } else {
            return null;

        }
        return tile;
    }
}
