package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReturnObjectTest {
    ReturnObject r;

    @Test
    public void ReturnObjectIdTest() {
        ReturnObject r = new ReturnObject();
        r.setI(1);
        assertEquals(1, r.getI());
    }

    @Test
    public void ReturnObjectNameTest() {
        ReturnObject r = new ReturnObject();
        r.setMessage("Hallo");
        assertEquals("Hallo", r.getMessage());
    }

    @Test
    public void ReturnObjectTileTestDestroyed() {
        ReturnObject r = new ReturnObject();
        r.setTile(BattleAreaTile.TileType.SHIP_START_DESTROYED);
        assertEquals(BattleAreaTile.TileType.SHIP_START_DESTROYED, r.getTile());
    }

    @Test
    public void ReturnObjectTileTestNoHit() {
        ReturnObject r = new ReturnObject();
        r.setTile(BattleAreaTile.TileType.NO_HIT);
        assertEquals(BattleAreaTile.TileType.NO_HIT, r.getTile());
    }

    @Test
    public void ReturnObjectTileTestShipEnd() {
        ReturnObject r = new ReturnObject();
        r.setTile(BattleAreaTile.TileType.SHIP_END);
        assertEquals(BattleAreaTile.TileType.SHIP_END, r.getTile());
    }

    @Test
    public void ReturnObjectTileTestShipMiddle() {
        ReturnObject r = new ReturnObject();
        r.setTile(BattleAreaTile.TileType.SHIP_MIDDLE);
        assertEquals(BattleAreaTile.TileType.SHIP_MIDDLE, r.getTile());
    }

    @Test
    public void ReturnObjectTileTestShipStart() {
        ReturnObject r = new ReturnObject();
        r.setTile(BattleAreaTile.TileType.SHIP_START);
        assertEquals(BattleAreaTile.TileType.SHIP_START, r.getTile());
    }

    @Test
    public void ReturnObjectTileTestWater() {
        ReturnObject r = new ReturnObject();
        r.setTile(BattleAreaTile.TileType.WATER);
        assertEquals(BattleAreaTile.TileType.WATER, r.getTile());
    }

}