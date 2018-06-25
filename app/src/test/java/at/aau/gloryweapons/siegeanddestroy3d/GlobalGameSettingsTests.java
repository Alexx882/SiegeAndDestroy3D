package at.aau.gloryweapons.siegeanddestroy3d;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 * Created by Alexander on 19.06.2018.
 */

public class GlobalGameSettingsTests {
    GlobalGameSettings settings;

    @Before
    public void before() {
        // reset singleton instance
        GlobalGameSettings.setCurrent(null);
        settings = GlobalGameSettings.getCurrent();
    }

    @Test
    public void initialValues() {
        Assert.assertFalse(settings.isGameFinished());
        Assert.assertFalse(settings.isServer());
    }

    @Test
    public void localUser_Values() {
        User u = new User(20, "Alex");
        settings.setLocalUser(u);

        Assert.assertNotNull(settings.getLocalUser());
        Assert.assertEquals(u.getId(), settings.getLocalUser().getId());
        Assert.assertEquals(u.getName(), settings.getLocalUser().getName());

        Assert.assertEquals(u.getId(), settings.getPlayerId());
    }

    @Test
    public void singleton_instantiation() {
        // reset instance
        GlobalGameSettings.setCurrent(null);

        // get instance
        GlobalGameSettings inst = GlobalGameSettings.getCurrent();
        Assert.assertNotNull(inst);
        Assert.assertEquals(inst, GlobalGameSettings.getCurrent());
        Assert.assertEquals(inst, GlobalGameSettings.getCurrent());

        // get new instance
        GlobalGameSettings.setCurrent(null);
        Assert.assertNotSame(inst, GlobalGameSettings.getCurrent());
    }
}
