package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import android.bluetooth.le.BluetoothLeScanner;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Alexander on 19.06.2018.
 */

public class BasicShipTests {
    BasicShip ship;

    @Before
    public void before() {
        ship = new BasicShip(-1, 2, true);
    }

    @Test
    public void constructorInit() {
        BasicShip s;
        s = new BasicShip(7, 2, false);
        Assert.assertEquals(7, s.getUserId());
        Assert.assertEquals(2, s.getLength());
        Assert.assertFalse(s.isHorizontal());
        Assert.assertNotNull(s.getTiles());
        Assert.assertEquals(2, s.getTiles().length);

        // default values
        s = new BasicShip();
        Assert.assertEquals(-1, s.getUserId());
        Assert.assertEquals(0, s.getLength());
        Assert.assertTrue(s.isHorizontal());
        Assert.assertNotNull(s.getTiles());
        Assert.assertEquals(0, s.getTiles().length);
    }

    @Test
    public void setHorizontal() {
        ship.setHorizontal(true);
        Assert.assertTrue(ship.isHorizontal());

        ship.setHorizontal(false);
        Assert.assertFalse(ship.isHorizontal());
    }

    @Test
    public void toggleOrientation() {
        ship.setHorizontal(true);

        ship.toggleOrientation();
        Assert.assertFalse(ship.isHorizontal());

        ship.toggleOrientation();
        Assert.assertTrue(ship.isHorizontal());

        ship.toggleOrientation();
        Assert.assertFalse(ship.isHorizontal());
    }
}
