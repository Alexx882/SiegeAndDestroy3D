package at.aau.gloryweapons.siegeanddestroy3d.game.controllers;

import android.service.quicksettings.Tile;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;

/**
 * It is assumed that the server only sends correct locations for HIT and NO_HIT.
 * ie. if a shot hit on (1,2) there is a ship on (1,2) for the userId. vice versa for NO_HIT.
 */
public class GameControllerHelperClassTests {
    @Test
    public void updateBattleAreaFromShotList_nullValues() {
        // must not throw a exception

        GameControllerHelperClass.updateBattleAreaFromShotList(null, new ArrayList<TurnDTO>());

        GameControllerHelperClass.updateBattleAreaFromShotList(new BattleArea(), null);

        GameControllerHelperClass.updateBattleAreaFromShotList(null, null);
    }

    @Test
    public void updateBattleAreaFromShotList_singleShot_correctId_hit() {
        // create a area and place a ship
        BattleArea area = new BattleArea(1, 3);
        area.placeShip(new BasicShip(1, 1, true), 2, 2);
        Assert.assertEquals(1, area.remainingFields());

        // hit
        TurnDTO shot = createShot(1, TurnDTO.TurnType.HIT, 2, 2);
        ArrayList<TurnDTO> shots = new ArrayList<>();
        shots.add(shot);

        // apply shot
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);

        Assert.assertEquals(0, area.remainingFields());
    }

    @Test
    public void updateBattleAreaFromShotList_singleShot_correctId_miss() {
        // create a area and place a ship
        BattleArea area = new BattleArea(1, 3);
        area.placeShip(new BasicShip(1, 1, true), 2, 2);
        Assert.assertEquals(1, area.remainingFields());

        // miss
        TurnDTO shot = createShot(1, TurnDTO.TurnType.NO_HIT, 1, 1);
        ArrayList<TurnDTO> shots = new ArrayList<>();
        shots.add(shot);

        // apply shot
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);
        Assert.assertEquals(1, area.remainingFields());
    }

    @Test
    public void updateBattleAreaFromShotList_singleShot_wrongId_hit() {
        // create a area and place a ship
        BattleArea area = new BattleArea(1, 3);
        area.placeShip(new BasicShip(1, 1, true), 2, 2);
        Assert.assertEquals(1, area.remainingFields());

        // hit but for a different userId
        TurnDTO shot = createShot(2, TurnDTO.TurnType.HIT, 2, 2);
        ArrayList<TurnDTO> shots = new ArrayList<>();
        shots.add(shot);

        // apply shot
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);

        Assert.assertEquals(1, area.remainingFields());
    }

    @Test
    public void updateBattleAreaFromShotList_singleShot_wrongId_miss() {
        // create a area and place a ship
        BattleArea area = new BattleArea(1, 3);
        area.placeShip(new BasicShip(1, 1, true), 2, 2);
        Assert.assertEquals(1, area.remainingFields());

        // miss and for different userId
        TurnDTO shot = createShot(2, TurnDTO.TurnType.NO_HIT, 1, 1);
        ArrayList<TurnDTO> shots = new ArrayList<>();
        shots.add(shot);

        // apply shot
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);
        Assert.assertEquals(1, area.remainingFields());
    }

    @Test
    public void updateBattleAreaFromShotList_multipleShots_correctId() {
        // create a area and place a ship
        BattleArea area = new BattleArea(1, 3);
        area.placeShip(new BasicShip(1, 2, true), 0, 0);
        Assert.assertEquals(2, area.remainingFields());

        ArrayList<TurnDTO> shots = new ArrayList<>();
        TurnDTO shot1 = createShot(1, TurnDTO.TurnType.NO_HIT, 1, 1);
        shots.add(shot1);
        TurnDTO shot2 = createShot(1, TurnDTO.TurnType.HIT, 0, 1);
        shots.add(shot2);
        TurnDTO shot3 = createShot(1, TurnDTO.TurnType.NO_HIT, 2, 2);
        shots.add(shot3);

        // apply shots
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);
        Assert.assertEquals(1, area.remainingFields());
        Assert.assertEquals(1, countShotsOnArea(area, BattleAreaTile.TileType.SHIP_DESTROYED));
        Assert.assertEquals(2, countShotsOnArea(area, BattleAreaTile.TileType.NO_HIT));
    }

    @Test
    public void updateBattleAreaFromShotList_multipleShots_wrongId() {
        // create a area and place a ship
        BattleArea area = new BattleArea(1, 3);
        area.placeShip(new BasicShip(1, 2, true), 0, 0);
        Assert.assertEquals(2, area.remainingFields());

        ArrayList<TurnDTO> shots = new ArrayList<>();
        TurnDTO shot1 = createShot(2, TurnDTO.TurnType.NO_HIT, 1, 1);
        shots.add(shot1);
        TurnDTO shot2 = createShot(2, TurnDTO.TurnType.HIT, 0, 1);
        shots.add(shot2);
        TurnDTO shot3 = createShot(2, TurnDTO.TurnType.NO_HIT, 2, 2);
        shots.add(shot3);

        // apply shots
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);
        Assert.assertEquals(2, area.remainingFields());
        Assert.assertEquals(0, countShotsOnArea(area, BattleAreaTile.TileType.SHIP_DESTROYED));
        Assert.assertEquals(0, countShotsOnArea(area, BattleAreaTile.TileType.NO_HIT));
    }

    @Test
    public void updateBattleAreaFromShotList_multipleShots_variousValues() {
        // create a area and place a ship
        BattleArea area = new BattleArea(1, 3);
        area.placeShip(new BasicShip(1, 2, false), 0, 0);
        Assert.assertEquals(2, area.remainingFields());

        ArrayList<TurnDTO> shots = new ArrayList<>();
        TurnDTO shot1 = createShot(1, TurnDTO.TurnType.NO_HIT, 2, 1);
        shots.add(shot1);
        TurnDTO shot2 = createShot(2, TurnDTO.TurnType.HIT, 1, 1);
        shots.add(shot2);
        TurnDTO shot3 = createShot(1, TurnDTO.TurnType.HIT, 0, 1);
        shots.add(shot3);
        TurnDTO shot4 = createShot(5, TurnDTO.TurnType.NO_HIT, 0, 1);
        shots.add(shot4);

        // apply shots
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);
        Assert.assertEquals(1, area.remainingFields());
        Assert.assertEquals(1, countShotsOnArea(area, BattleAreaTile.TileType.SHIP_DESTROYED));
        Assert.assertEquals(1, countShotsOnArea(area, BattleAreaTile.TileType.NO_HIT));
    }

    @Test
    public void updateBattleAreaFromShotList_invalidTurnTypes() {
        // create a area and place a ship
        BattleArea area = new BattleArea(1, 3);
        area.placeShip(new BasicShip(1, 2, false), 0, 1);
        Assert.assertEquals(2, area.remainingFields());

        ArrayList<TurnDTO> shots = new ArrayList<>();
        TurnDTO shot1 = createShot(1, TurnDTO.TurnType.ERROR, 2, 1);
        shots.add(shot1);
        TurnDTO shot2 = createShot(2, TurnDTO.TurnType.ERROR, 2, 1);
        shots.add(shot2);
        TurnDTO shot3 = createShot(1, TurnDTO.TurnType.POWERUP, 1, 1);
        shots.add(shot3);
        TurnDTO shot4 = createShot(2, TurnDTO.TurnType.POWERUP, 0, 1);
        shots.add(shot4);
        TurnDTO shot5 = createShot(1, TurnDTO.TurnType.SHOT, 0, 1);
        shots.add(shot5);
        TurnDTO shot6 = createShot(2, TurnDTO.TurnType.SHOT, 0, 1);
        shots.add(shot6);

        // apply shots
        GameControllerHelperClass.updateBattleAreaFromShotList(area, shots);
        Assert.assertEquals(2, area.remainingFields());
        Assert.assertEquals(0, countShotsOnArea(area, BattleAreaTile.TileType.SHIP_DESTROYED));
        Assert.assertEquals(0, countShotsOnArea(area, BattleAreaTile.TileType.NO_HIT));
    }

    private TurnDTO createShot(int userId, TurnDTO.TurnType type, int x, int y) {
        TurnDTO shot = new TurnDTO();
        shot.setUserId(userId);
        shot.setType(type);
        shot.setxCoordinates(x);
        shot.setyCoordinates(x);

        return shot;
    }

    private int countShotsOnArea(BattleArea area, BattleAreaTile.TileType type) {
        int cnt = 0;

        for (BattleAreaTile[] tileRow : area.getBattleAreaTiles()) {
            for (BattleAreaTile tile : tileRow) {
                if (tile.getType() == type)
                    ++cnt;
            }
        }
        return cnt;
    }
}
