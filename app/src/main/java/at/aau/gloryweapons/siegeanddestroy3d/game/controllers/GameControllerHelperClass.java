package at.aau.gloryweapons.siegeanddestroy3d.game.controllers;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;

public class GameControllerHelperClass {

    public static void updateBattleAreaFromShotList(BattleArea area, List<TurnDTO> shots) {

        if (area == null || shots == null)
            return;


        for (TurnDTO shot : shots) {
            if (area.getUserId() == shot.getUserId()) {
                // set the shot in the area
                if (shot.getType() == TurnDTO.TurnType.HIT)
                    area.getBattleAreaTiles()[shot.getxCoordinates()][shot.getyCoordinates()]
                            .setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                else if (shot.getType() == TurnDTO.TurnType.NO_HIT)
                    area.getBattleAreaTiles()[shot.getxCoordinates()][shot.getyCoordinates()]
                            .setType(BattleAreaTile.TileType.NO_HIT);
            }
        }
    }
}
