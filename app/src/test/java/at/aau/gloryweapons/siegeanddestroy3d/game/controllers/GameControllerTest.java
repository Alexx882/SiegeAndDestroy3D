package at.aau.gloryweapons.siegeanddestroy3d.game.controllers;

import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

import static org.junit.Assert.*;

public class GameControllerTest {


    @Test
    public void checkIfTileDestroyedTileWater() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);

        String message = null;
        message = gc.checkIfTileDestroyed(area, 1, 1);
        assertEquals(null, message);
    }

    @Test
    public void checkIfTileDestroyedTileDestroyed() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);
        BattleAreaTile[][] tile = area.getBattleAreaTiles();
        tile[1][1].setType(BattleAreaTile.TileType.SHIP_DESTROYED);
        area.setBattleAreaTiles(tile);

        String message = null;
        message = gc.checkIfTileDestroyed(area, 1, 1);
        assertEquals("dieses Feld ist bereits zerstört", message);
    }

    @Test
    public void checkIfTileDestroyedTileNoHit() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);
        BattleAreaTile[][] tile = area.getBattleAreaTiles();
        tile[1][1].setType(BattleAreaTile.TileType.NO_HIT);
        area.setBattleAreaTiles(tile);

        String message = null;
        message = gc.checkIfTileDestroyed(area, 1, 1);
        assertEquals("dieses Feld ist bereits zerstört", message);
    }

    @Test
    public void checkIfTileDestroyedTileShipStart() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);
        BattleAreaTile[][] tile = area.getBattleAreaTiles();
        tile[1][1].setType(BattleAreaTile.TileType.SHIP_START);
        area.setBattleAreaTiles(tile);

        String message = null;
        message = gc.checkIfTileDestroyed(area, 1, 1);
        assertEquals(null, message);
    }

    @Test
    public void checkIfTileDestroyedTileShipMiddle() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);
        BattleAreaTile[][] tile = area.getBattleAreaTiles();
        tile[1][1].setType(BattleAreaTile.TileType.SHIP_MIDDLE);
        area.setBattleAreaTiles(tile);

        String message = null;
        message = gc.checkIfTileDestroyed(area, 1, 1);
        assertEquals(null, message);
    }

    @Test
    public void checkIfTileDestroyedTileShipEnd() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);
        BattleAreaTile[][] tile = area.getBattleAreaTiles();
        tile[1][1].setType(BattleAreaTile.TileType.SHIP_END);
        area.setBattleAreaTiles(tile);

        String message = null;
        message = gc.checkIfTileDestroyed(area, 1, 1);
        assertEquals(null, message);
    }

    @Test
    public void checkIfSuicide() {
        User u = new User();
        u.setName("mao");
        u.setId(1);
        GlobalGameSettings.getCurrent().setLocalUser(u);
        GameController gc = new GameController();
        String message = null;

        message = gc.checkIfSuicide(u);
        assertEquals("kein Selbstbeschuss", message);
    }

    @Test
    public void checkIfSuicide2() {
        User u = new User();
        u.setName("mao");
        u.setId(1);
        User u2 = new User();
        u.setName("mao2");
        u.setId(2);
        GlobalGameSettings.getCurrent().setLocalUser(u);
        GameController gc = new GameController();
        String message = null;

        message = gc.checkIfSuicide(u2);
        assertEquals(null, message);
    }

    @Test
    public void checkIfMyTurn() {
        User u = new User();
        u.setName("mao");
        u.setId(1);
        GlobalGameSettings.getCurrent().setLocalUser(u);
        GlobalGameSettings.getCurrent().setUserOfCurrentTurn(u);
        GameController gc = new GameController();
        String message = null;

        message = gc.checkIfMyTurn();
        assertEquals(null, message);
    }

    @Test
    public void checkIfMyTurn2() {
        User u = new User();
        u.setName("mao");
        u.setId(1);
        User u2 = new User();
        u.setName("mao2");
        u.setId(2);
        GlobalGameSettings.getCurrent().setLocalUser(u);
        GlobalGameSettings.getCurrent().setUserOfCurrentTurn(u2);
        GameController gc = new GameController();
        String message = null;

        message = gc.checkIfMyTurn();
        assertEquals("Nicht deine Runde", message);
    }
}