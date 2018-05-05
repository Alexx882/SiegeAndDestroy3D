package at.aau.gloryweapons.siegeanddestroy3d.game;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;
import at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect.ClientGameHandlerWifi;

public class GameController {

    private NetworkCommunicator communicator = new ClientGameHandlerWifi();

    public BattleAreaTile.TileType shotOnEnemy(GameSettings game, User enemy, int col, int row) {
        BattleAreaTile.TileType tile = null;
        if (enemy.getId() != game.getMyId()) {
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
            //not your turn
        }
        return tile;
    }
}
