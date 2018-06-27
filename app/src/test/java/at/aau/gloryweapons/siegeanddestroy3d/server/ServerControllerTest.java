package at.aau.gloryweapons.siegeanddestroy3d.server;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheaterSuspicionResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheatingDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;

import static org.junit.Assert.assertEquals;

public class ServerControllerTest {

    @Test
    public void checkNameValid_accept() throws Exception {
        ServerController serverController = new ServerController();
        User user = serverController.checkName("Laura");

        Assert.assertNotNull(user);
        assertEquals("Laura", user.getName());
        assertEquals(1, user.getId());
    }

    @Test
    public void checkNameValid_decline() throws Exception {
        ServerController serverController = new ServerController();

        User user = serverController.checkName("");
        Assert.assertNull(user); //leer

        user = serverController.checkName(null);
        Assert.assertNull(user); //wenn null

    }

    @Test
    public void checkNameValidMultiple_accept() throws Exception {
        ServerController serverController = new ServerController();
        User user = serverController.checkName("Laura");
        Assert.assertNotNull(user);
        assertEquals("Laura", user.getName());
        assertEquals(1, user.getId());

        user = serverController.checkName("Alex");
        Assert.assertNotNull(user);
        assertEquals("Alex", user.getName());
        assertEquals(2, user.getId());

        user = serverController.checkName("Patrick");
        Assert.assertNotNull(user);
        assertEquals("Patrick", user.getName());
        assertEquals(3, user.getId());

        user = serverController.checkName("Johannes");
        Assert.assertNotNull(user);
        assertEquals("Johannes", user.getName());
        assertEquals(4, user.getId());

    }

    @Test
    public void checkNameValidMultiple_decline() throws Exception {
        ServerController serverController = new ServerController();
        User user = serverController.checkName("Laura");
        Assert.assertNotNull(user);
        assertEquals("Laura", user.getName());
        assertEquals(1, user.getId());

        user = serverController.checkName("Laura");
        Assert.assertNull(user);

        user = serverController.checkName("Patrick");
        Assert.assertNotNull(user);
        assertEquals("Patrick", user.getName());
        assertEquals(2, user.getId());

        user = serverController.checkName("Johannes");
        Assert.assertNotNull(user);
        assertEquals("Johannes", user.getName());
        assertEquals(3, user.getId());

    }

    @Test
    public void checkTile_Miss() {
        ServerController serverController = new ServerController();
        BattleAreaTile t = new BattleAreaTile();

        t.setType(BattleAreaTile.TileType.WATER);
        assertEquals(BattleAreaTile.TileType.NO_HIT, serverController.checkTile(t).getType());

        t.setType(BattleAreaTile.TileType.NO_HIT);
        assertEquals(BattleAreaTile.TileType.NO_HIT, serverController.checkTile(t).getType());

        t.setType(BattleAreaTile.TileType.SHIP_START_DESTROYED);
        assertEquals(BattleAreaTile.TileType.SHIP_START_DESTROYED, serverController.checkTile(t).getType());
    }

    @Test
    public void checkTile_Hit() {
        ServerController serverController = new ServerController();
        BattleAreaTile t = new BattleAreaTile();

        t.setType(BattleAreaTile.TileType.SHIP_START);
        assertEquals(BattleAreaTile.TileType.SHIP_START_DESTROYED, serverController.checkTile(t).getType());

        t.setType(BattleAreaTile.TileType.SHIP_MIDDLE);
        assertEquals(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED, serverController.checkTile(t).getType());

        t.setType(BattleAreaTile.TileType.SHIP_END);
        assertEquals(BattleAreaTile.TileType.SHIP_END_DESTROYED, serverController.checkTile(t).getType());
    }

    @Test
    public void checkShot_miss() {
        ServerController serverController = new ServerController();
        TurnDTO turn = new TurnDTO(TurnDTO.TurnType.SHOT,1);
        turn.setxCoordinates(1);
        turn.setyCoordinates(1);

        BattleArea area = new BattleArea(1,3);
        ArrayList<BattleArea> areas = new ArrayList<>();
        areas.add(area);

        assertEquals(BattleAreaTile.TileType.WATER, area.getBattleAreaTiles()[1][1].getType());
        serverController.checkShot(turn,  areas);
        assertEquals(TurnDTO.TurnType.NO_HIT, turn.getType());
        assertEquals(BattleAreaTile.TileType.NO_HIT, area.getBattleAreaTiles()[1][1].getType());
    }

    @Test
    public void checkShot_hit() {
        ServerController serverController = new ServerController();
        TurnDTO turn = new TurnDTO(TurnDTO.TurnType.SHOT,1);
        turn.setxCoordinates(1);
        turn.setyCoordinates(1);

        BattleArea area = new BattleArea(1,3);
        area.placeShip(new BasicShip(1, 2,true), 1,1);
        ArrayList<BattleArea> areas = new ArrayList<>();
        areas.add(area);

        assertEquals(BattleAreaTile.TileType.SHIP_START, area.getBattleAreaTiles()[1][1].getType());
        serverController.checkShot(turn,  areas);
        assertEquals(TurnDTO.TurnType.HIT, turn.getType());
        assertEquals(BattleAreaTile.TileType.SHIP_START_DESTROYED, area.getBattleAreaTiles()[1][1].getType());
    }

    @Test
    public void checkShot_wrongArea() {
        ServerController serverController = new ServerController();
        TurnDTO turn = new TurnDTO(TurnDTO.TurnType.SHOT,1);
        turn.setxCoordinates(1);
        turn.setyCoordinates(1);

        // area has wrong user id
        BattleArea area = new BattleArea(2,3);
        area.placeShip(new BasicShip(2, 2,true), 1,1);
        ArrayList<BattleArea> areas = new ArrayList<>();
        areas.add(area);

        assertEquals(BattleAreaTile.TileType.SHIP_START, area.getBattleAreaTiles()[1][1].getType());
        serverController.checkShot(turn,  areas);
        // therefore the turnDTO isnt changed
        assertEquals(TurnDTO.TurnType.SHOT, turn.getType());
        assertEquals(BattleAreaTile.TileType.SHIP_START, area.getBattleAreaTiles()[1][1].getType());
    }

    @Test
    public void checkShot_multipleAreas() {
        ServerController serverController = new ServerController();
        TurnDTO turn = new TurnDTO(TurnDTO.TurnType.SHOT,1);
        turn.setxCoordinates(1);
        turn.setyCoordinates(1);

        // two areas, since normally at least two players exist
        ArrayList<BattleArea> areas = new ArrayList<>();
        BattleArea area1 = new BattleArea(1,3);
        area1.placeShip(new BasicShip(1, 2,true), 1,1);
        areas.add(area1);
        BattleArea area2 = new BattleArea(2,3);
        area2.placeShip(new BasicShip(2, 2,true), 1,1);
        areas.add(area2);

        assertEquals(BattleAreaTile.TileType.SHIP_START, area1.getBattleAreaTiles()[1][1].getType());
        assertEquals(BattleAreaTile.TileType.SHIP_START, area2.getBattleAreaTiles()[1][1].getType());

        serverController.checkShot(turn,  areas);

        // one was hit, the other not.
        assertEquals(TurnDTO.TurnType.HIT, turn.getType());
        assertEquals(BattleAreaTile.TileType.SHIP_START_DESTROYED, area1.getBattleAreaTiles()[1][1].getType());
        assertEquals(BattleAreaTile.TileType.SHIP_START, area2.getBattleAreaTiles()[1][1].getType());
    }

    @Test
    public void checkGetUserForNextTurn() {
        ServerController serverController = new ServerController();

        User user1 = new User(1, "userTest_1");
        User user2 = new User(2, "userTest_2");
        serverController.addDataToGameConfig(user1, null, null, null);
        serverController.addDataToGameConfig(user2, null, null, null);

        User testUser1 = serverController.getUserForFirstTurn();
        assertEquals(1, testUser1.getId());

        User testUser2 = serverController.getUserForNextTurn();
        assertEquals(2, testUser2.getId());
    }

    @Test
    public void checkCheckForWinner() {
        ServerController serverController = new ServerController();

        User user1 = new User(1, "userTest_1");
        User user2 = new User(2, "userTest_2");

        serverController.addDataToGameConfig(user1, null, null, null);
        serverController.addDataToGameConfig(user2, null, null, null);

        assertEquals(null, serverController.checkForWinner());

        serverController.setUserDefeated(user2.getId());
        assertEquals(user1, serverController.checkForWinner());
    }
}
