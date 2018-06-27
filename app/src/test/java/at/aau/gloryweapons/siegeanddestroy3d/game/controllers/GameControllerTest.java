package at.aau.gloryweapons.siegeanddestroy3d.game.controllers;

import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.ShipContainer;
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
    public void checkIfTileDestroyedTileStartDestroyed() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);
        BattleAreaTile[][] tile = area.getBattleAreaTiles();
        tile[1][1].setType(BattleAreaTile.TileType.SHIP_START_DESTROYED);
        area.setBattleAreaTiles(tile);

        String message = null;
        message = gc.checkIfTileDestroyed(area, 1, 1);
        assertEquals("dieses Feld ist bereits zerstört", message);
    }

    @Test
    public void checkIfTileDestroyedTileMiddleDestroyed() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);
        BattleAreaTile[][] tile = area.getBattleAreaTiles();
        tile[1][1].setType(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED);
        area.setBattleAreaTiles(tile);

        String message = null;
        message = gc.checkIfTileDestroyed(area, 1, 1);
        assertEquals("dieses Feld ist bereits zerstört", message);
    }

    @Test
    public void checkIfTileDestroyedTileEndDestroyed() {
        GameController gc = new GameController();
        BattleArea area = new BattleArea(1, 9, 9);
        BattleAreaTile[][] tile = area.getBattleAreaTiles();
        tile[1][1].setType(BattleAreaTile.TileType.SHIP_END_DESTROYED);
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

    @Test
    public void checkFindWeakestShip(){
        GameController gc = new GameController();

        BattleArea battleArea = new BattleArea(2,10);

        BasicShip ship1 = new BasicShip(2,4,true);
        BasicShip ship2 = new BasicShip(2,2,true);

        int row1 = 1;
        int row2 = 4;
        int col1 = 3;
        int col2 = 0;

        ShipContainer ship1Container = new ShipContainer();
        ship1Container.setRow(row1);
        ship1Container.setCol(col1);
        ship1Container.setShip(ship1);

        ShipContainer ship2Container = new ShipContainer();
        ship2Container.setRow(row2);
        ship2Container.setCol(col2);
        ship2Container.setShip(ship2);

        battleArea.placeShip(ship1,row1,col1);
        battleArea.addShipToContainerList(ship1Container);

        battleArea.placeShip(ship2,row2,col2);
        battleArea.addShipToContainerList(ship2Container);

        ShipContainer testContainer = gc.findWeakestShip(battleArea);

        assertEquals(2, testContainer.getShip().getLength() );
        assertEquals(2, testContainer.getCurrentLength() );
    }

    @Test
    public void checkCheckShip(){
        GameController gc = new GameController();
        int row = 2;
        int col = 3;

        BattleArea battleArea = new BattleArea(2,10);

        BasicShip ship1 = new BasicShip(2,4,true);

        ShipContainer ship1Container = new ShipContainer();
        ship1Container.setRow(row);
        ship1Container.setCol(col);
        ship1Container.setShip(ship1);

        battleArea.placeShip(ship1,row,col);
        battleArea.addShipToContainerList(ship1Container);

        gc.checkShip(ship1Container, battleArea);

        assertEquals(4, ship1Container.getCurrentLength() );
    }
}