package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Alexander on 15.04.2018.
 */

public class BattleAreaTests {
    BattleArea area;

    @Before
    public void before() {
        area = new BattleArea(-1, 4);
    }

    @Test
    public void constructor_InitialTiles() throws Exception {
        int size = 4;
        BattleArea a = new BattleArea(-1, size);
        BattleAreaTile[][] tiles = a.getBattleAreaTiles();

        // everything has to be water at the beginning
        int cnt = 0;
        for (int i = 0; i < a.getRowNumber(); ++i)
            for (int j = 0; j < a.getColumnNumber(); ++j)
                if (tiles[i][j].getType() == BattleAreaTile.TileType.WATER)
                    ++cnt;
        Assert.assertEquals(size * size, cnt);
    }

    @Test
    public void isShipInBattleArea_Horizontal_Valid() throws Exception {
        BasicShip ship = new BasicShip(-1, 2, true);
        boolean res;

        // placing the ship in all rows has to be OK since its placed horizontally
        for (int i = 0; i < area.getRowNumber(); ++i) {
            res = area.isShipInBattleArea(ship, i, 0);
            Assert.assertEquals(true, res);
        }

        // first and second column must also be OK
        res = area.isShipInBattleArea(ship, 0, 1);
        Assert.assertEquals(true, res);
        res = area.isShipInBattleArea(ship, 0, 2);
        Assert.assertEquals(true, res);
    }

    @Test
    public void isShipInBattleArea_Horizontal_Invalid() throws Exception {
        BasicShip ship = new BasicShip(-1, 2, true);
        boolean res;

        // third column must not be OK
        res = area.isShipInBattleArea(ship, 0, 3);
        Assert.assertEquals(false, res);
    }

    @Test
    public void isShipInBattleArea_Vertical_Valid() throws Exception {
        BasicShip ship = new BasicShip(-1, 2, false);
        boolean res;

        // placing the ship in all columns has to be OK since its placed vertically
        for (int i = 0; i < area.getColumnNumber(); ++i) {
            res = area.isShipInBattleArea(ship, 0, i);
            Assert.assertEquals(true, res);
        }

        // first and second row must also be OK
        res = area.isShipInBattleArea(ship, 1, 0);
        Assert.assertEquals(true, res);
        res = area.isShipInBattleArea(ship, 2, 0);
        Assert.assertEquals(true, res);
    }

    @Test
    public void isShipInBattleArea_Vertical_Invalid() throws Exception {
        BasicShip ship = new BasicShip(-1, 2, false);
        boolean res;

        // third column must not be OK
        res = area.isShipInBattleArea(ship, 3, 0);
        Assert.assertEquals(false, res);
    }

    @Test
    public void isShipInBattleArea_Invalid() throws Exception {
        BasicShip ship = new BasicShip(-1, 2, true);
        boolean res;

        // something bigger than the board.
        res = area.isShipInBattleArea(ship, 50, 0);
        Assert.assertEquals(false, res);
        res = area.isShipInBattleArea(ship, 0, 50);
        Assert.assertEquals(false, res);
        res = area.isShipInBattleArea(ship, -10, 0);
        Assert.assertEquals(false, res);
        res = area.isShipInBattleArea(ship, 0, -10);
        Assert.assertEquals(false, res);

        res = area.isShipInBattleArea(null, 0, 0);
        Assert.assertEquals(false, res);
    }

    @Test
    public void isShipPositionAvailable_Horizontal_Valid() throws Exception {
        BasicShip blockingShip = new BasicShip(-1, 1, true);
        BasicShip ship = new BasicShip(-1, 2, true);
        boolean res;

        // place the blocking ship everywhere around the desired position for the ship
        for (int i = 0; i < area.getRowNumber(); ++i)
            if (i != 1) // second row should be kept empty
                for (int j = 0; j < area.getColumnNumber(); ++j)
                    area.placeShip(blockingShip, i, j);

        // second row is still empty
        res = area.isShipPositionAvailable(ship, 1, 0);
        Assert.assertEquals(true, res);
        res = area.isShipPositionAvailable(ship, 1, 1);
        Assert.assertEquals(true, res);
        res = area.isShipPositionAvailable(ship, 1, 2);
        Assert.assertEquals(true, res);

        res = area.isShipPositionAvailable(ship, 2, 2);
        Assert.assertEquals(false, res);
    }

    @Test
    public void isShipPositionAvailable_Horizontal_Invalid() throws Exception {
        BasicShip blockingShip = new BasicShip(-1, 1, true);
        BasicShip ship = new BasicShip(-1, 2, true);
        boolean res;

        // place the blocking ship
        area.placeShip(blockingShip, 2, 2);

        res = area.isShipPositionAvailable(ship, 2, 1);
        Assert.assertEquals(false, res);
        res = area.isShipPositionAvailable(ship, 2, 2);
        Assert.assertEquals(false, res);

        res = area.isShipPositionAvailable(ship, 2, 0);
        Assert.assertEquals(true, res);

        // outside the board
        res = area.isShipPositionAvailable(ship, 0, 3);
        Assert.assertEquals(false, res);
    }

    @Test
    public void isShipPositionAvailable_Vertical_Valid() throws Exception {
        BasicShip blockingShip = new BasicShip(-1, 1, true);
        BasicShip ship = new BasicShip(-1, 2, false);
        boolean res;

        // place the blocking ship everywhere around the desired position for the ship
        for (int i = 0; i < area.getRowNumber(); ++i)
            for (int j = 0; j < area.getColumnNumber(); ++j)
                if (j != 1) // second column should be kept empty
                    area.placeShip(blockingShip, i, j);

        // second column is still empty
        res = area.isShipPositionAvailable(ship, 0, 1);
        Assert.assertEquals(true, res);
        res = area.isShipPositionAvailable(ship, 1, 1);
        Assert.assertEquals(true, res);
        res = area.isShipPositionAvailable(ship, 2, 1);
        Assert.assertEquals(true, res);

        res = area.isShipPositionAvailable(ship, 2, 2);
        Assert.assertEquals(false, res);
    }

    @Test
    public void isShipPositionAvailable_Vertical_Invalid() throws Exception {
        BasicShip blockingShip = new BasicShip(-1, 1, true);
        BasicShip ship = new BasicShip(-1, 2, false);
        boolean res;

        // place the blocking ship
        area.placeShip(blockingShip, 2, 2);

        res = area.isShipPositionAvailable(ship, 1, 2);
        Assert.assertEquals(false, res);
        res = area.isShipPositionAvailable(ship, 2, 2);
        Assert.assertEquals(false, res);

        res = area.isShipPositionAvailable(ship, 0, 2);
        Assert.assertEquals(true, res);

        // outside the board
        res = area.isShipPositionAvailable(ship, 3, 0);
        Assert.assertEquals(false, res);
    }

    @Test
    public void isShipPositionAvailable_Invalid() throws Exception {
        BasicShip ship = new BasicShip(-1, 2, true);
        boolean res;

        // outside of area
        res = area.isShipPositionAvailable(ship, -1, 0);
        Assert.assertEquals(false, res);
        res = area.isShipPositionAvailable(ship, 0, -1);
        Assert.assertEquals(false, res);
        res = area.isShipPositionAvailable(ship, 50, 0);
        Assert.assertEquals(false, res);
        res = area.isShipPositionAvailable(ship, 0, 50);
        Assert.assertEquals(false, res);

        res = area.isShipInBattleArea(null, 0, 0);
        Assert.assertEquals(false, res);
    }

    @Test
    public void placeShip_Horizontal() throws Exception {
        BasicShip ship = new BasicShip(-1, 2, true);

        area.placeShip(ship, 1, 1);

        BattleAreaTile[][] tiles = area.getBattleAreaTiles();
        Assert.assertTrue(tiles[1][0].getType() == BattleAreaTile.TileType.WATER);
        Assert.assertTrue(tiles[1][1].getType() == BattleAreaTile.TileType.SHIP_HEALTHY);
        Assert.assertTrue(tiles[1][2].getType() == BattleAreaTile.TileType.SHIP_HEALTHY);
        Assert.assertTrue(tiles[1][3].getType() == BattleAreaTile.TileType.WATER);

        // must result in exactly 2 tiles which are not water anymore
        int cnt = 0;
        for (int i = 0; i < area.getRowNumber(); ++i)
            for (int j = 0; j < area.getColumnNumber(); ++j)
                if (tiles[i][j].getType() == BattleAreaTile.TileType.SHIP_HEALTHY)
                    ++cnt;
        Assert.assertEquals(2, cnt);

        // tiles (2 pieces) also have to be updated in ship obj
        Assert.assertTrue(ship.getTiles()[0].equals(tiles[1][1]));
        Assert.assertTrue(ship.getTiles()[1].equals(tiles[1][2]));
    }

    @Test
    public void placeShip_Vertical() throws Exception {
        BasicShip ship = new BasicShip(-1, 2, false);

        area.placeShip(ship, 1, 1);

        BattleAreaTile[][] tiles = area.getBattleAreaTiles();
        Assert.assertTrue(tiles[0][1].getType() == BattleAreaTile.TileType.WATER);
        Assert.assertTrue(tiles[1][1].getType() == BattleAreaTile.TileType.SHIP_HEALTHY);
        Assert.assertTrue(tiles[2][1].getType() == BattleAreaTile.TileType.SHIP_HEALTHY);
        Assert.assertTrue(tiles[3][1].getType() == BattleAreaTile.TileType.WATER);

        // must result in exactly 2 tiles which are not water anymore
        int cnt = 0;
        for (int i = 0; i < area.getRowNumber(); ++i)
            for (int j = 0; j < area.getColumnNumber(); ++j)
                if (tiles[i][j].getType() == BattleAreaTile.TileType.SHIP_HEALTHY)
                    ++cnt;
        Assert.assertEquals(2, cnt);

        // tiles (2 pieces) also have to be updated in ship obj
        Assert.assertTrue(ship.getTiles()[0].equals(tiles[1][1]));
        Assert.assertTrue(ship.getTiles()[1].equals(tiles[2][1]));
    }
}
