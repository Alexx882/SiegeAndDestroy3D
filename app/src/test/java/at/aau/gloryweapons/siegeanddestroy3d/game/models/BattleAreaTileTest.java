package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BattleAreaTileTest {

    @Test
    public void BattleAreaTileIsHorizontalTest() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setHorizontal(true);

        assertEquals(true, tile.isHorizontal());
    }

    @Test
    public void BattleAreaTileIsNotHorizontalTest() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setHorizontal(false);

        assertEquals(false, tile.isHorizontal());
    }

    @Test
    public void BattleAreaTileTileTestWater() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.WATER);
        assertEquals(BattleAreaTile.TileType.WATER, tile.getType());
    }

    @Test
    public void BattleAreaTileTileTestNoHit() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.NO_HIT);
        assertEquals(BattleAreaTile.TileType.NO_HIT, tile.getType());
    }

    @Test
    public void BattleAreaTileTileTestShipStart() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_START);
        assertEquals(BattleAreaTile.TileType.SHIP_START, tile.getType());
    }

    @Test
    public void BattleAreaTileTileTestShipMiddle() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_MIDDLE);
        assertEquals(BattleAreaTile.TileType.SHIP_MIDDLE, tile.getType());
    }

    @Test
    public void BattleAreaTileTileTestShipEnd() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_END);
        assertEquals(BattleAreaTile.TileType.SHIP_END, tile.getType());
    }

    @Test
    public void BattleAreaTileTileTestShipDestroyed() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_DESTROYED);
        assertEquals(BattleAreaTile.TileType.SHIP_DESTROYED, tile.getType());
    }

    @Test
    public void isAlive_true() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_START);
        assertTrue(tile.isAlive());

        tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_MIDDLE);
        assertTrue(tile.isAlive());

        tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_END);
        assertTrue(tile.isAlive());
    }

    @Test
    public void isAlive_false() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_DESTROYED);
        assertFalse(tile.isAlive());

        tile.setType(BattleAreaTile.TileType.WATER);
        assertFalse(tile.isAlive());

        tile.setType(BattleAreaTile.TileType.NO_HIT);
        assertFalse(tile.isAlive());
    }

}