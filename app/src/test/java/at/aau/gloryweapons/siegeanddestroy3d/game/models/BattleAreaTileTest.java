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
    public void BattleAreaTileTileTestShipDestroyedStart() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_START_DESTROYED);
        assertEquals(BattleAreaTile.TileType.SHIP_START_DESTROYED, tile.getType());
    }

    @Test
    public void BattleAreaTileTileTestShipDestroyedMiddle() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED);
        assertEquals(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED, tile.getType());
    }

    @Test
    public void BattleAreaTileTileTestShipDestroyedEnd() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_END_DESTROYED);
        assertEquals(BattleAreaTile.TileType.SHIP_END_DESTROYED, tile.getType());
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
        tile.setType(BattleAreaTile.TileType.SHIP_START_DESTROYED);
        assertFalse(tile.isAlive());
        tile.setType(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED);
        assertFalse(tile.isAlive());
        tile.setType(BattleAreaTile.TileType.SHIP_END_DESTROYED);
        assertFalse(tile.isAlive());

        tile.setType(BattleAreaTile.TileType.WATER);
        assertFalse(tile.isAlive());

        tile.setType(BattleAreaTile.TileType.NO_HIT);
        assertFalse(tile.isAlive());
    }

    @Test
    public void isDestroyed_true() {
        BattleAreaTile tile = new BattleAreaTile();

        tile.setType(BattleAreaTile.TileType.SHIP_START_DESTROYED);
        assertTrue(tile.isDestroyed());

        tile.setType(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED);
        assertTrue(tile.isDestroyed());

        tile.setType(BattleAreaTile.TileType.SHIP_END_DESTROYED);
        assertTrue(tile.isDestroyed());
    }

    @Test
    public void isDestroyed_false() {
        BattleAreaTile tile = new BattleAreaTile();
        tile.setType(BattleAreaTile.TileType.SHIP_START);
        assertFalse(tile.isDestroyed());
        tile.setType(BattleAreaTile.TileType.SHIP_MIDDLE);
        assertFalse(tile.isDestroyed());
        tile.setType(BattleAreaTile.TileType.SHIP_END);
        assertFalse(tile.isDestroyed());

        tile.setType(BattleAreaTile.TileType.WATER);
        assertFalse(tile.isDestroyed());

        tile.setType(BattleAreaTile.TileType.NO_HIT);
        assertFalse(tile.isDestroyed());
    }

    public void getDestroyedVersionOfShipTile_validInput(){
        assertEquals(BattleAreaTile.TileType.SHIP_START_DESTROYED,
        BattleAreaTile.getDestroyedVersionOfShipTile(BattleAreaTile.TileType.SHIP_START));

        assertEquals(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED,
                BattleAreaTile.getDestroyedVersionOfShipTile(BattleAreaTile.TileType.SHIP_MIDDLE));

        assertEquals(BattleAreaTile.TileType.SHIP_END_DESTROYED,
                BattleAreaTile.getDestroyedVersionOfShipTile(BattleAreaTile.TileType.SHIP_END));
    }

    public void getDestroyedVersionOfShipTile_invalidInput(){
        assertEquals(BattleAreaTile.TileType.SHIP_START_DESTROYED,
                BattleAreaTile.getDestroyedVersionOfShipTile(BattleAreaTile.TileType.SHIP_START_DESTROYED));

        assertEquals(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED,
                BattleAreaTile.getDestroyedVersionOfShipTile(BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED));

        assertEquals(BattleAreaTile.TileType.SHIP_END_DESTROYED,
                BattleAreaTile.getDestroyedVersionOfShipTile(BattleAreaTile.TileType.SHIP_END_DESTROYED));

        assertEquals(BattleAreaTile.TileType.NO_HIT,
                BattleAreaTile.getDestroyedVersionOfShipTile(BattleAreaTile.TileType.NO_HIT));

        assertEquals(BattleAreaTile.TileType.WATER,
                BattleAreaTile.getDestroyedVersionOfShipTile(BattleAreaTile.TileType.WATER));
    }
}