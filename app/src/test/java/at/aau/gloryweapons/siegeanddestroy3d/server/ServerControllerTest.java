package at.aau.gloryweapons.siegeanddestroy3d.server;

import org.junit.Assert;
import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

public class ServerControllerTest {

    @Test
    public void checkNameValid_accept() throws Exception {
        ServerController serverController = new ServerController();
        User user = serverController.checkName("Laura");

        Assert.assertNotNull(user);
        Assert.assertEquals("Laura", user.getName());
        Assert.assertEquals(1, user.getId());
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
        Assert.assertEquals("Laura", user.getName());
        Assert.assertEquals(1, user.getId());

        user = serverController.checkName("Alex");
        Assert.assertNotNull(user);
        Assert.assertEquals("Alex", user.getName());
        Assert.assertEquals(2, user.getId());

        user = serverController.checkName("Patrick");
        Assert.assertNotNull(user);
        Assert.assertEquals("Patrick", user.getName());
        Assert.assertEquals(3, user.getId());

        user = serverController.checkName("Johannes");
        Assert.assertNotNull(user);
        Assert.assertEquals("Johannes", user.getName());
        Assert.assertEquals(4, user.getId());

    }

    @Test
    public void checkNameValidMultiple_decline() throws Exception {
        ServerController serverController = new ServerController();
        User user = serverController.checkName("Laura");
        Assert.assertNotNull(user);
        Assert.assertEquals("Laura", user.getName());
        Assert.assertEquals(1, user.getId());

        user = serverController.checkName("Laura");
        Assert.assertNull(user);

        user = serverController.checkName("Patrick");
        Assert.assertNotNull(user);
        Assert.assertEquals("Patrick", user.getName());
        Assert.assertEquals(2, user.getId());

        user = serverController.checkName("Johannes");
        Assert.assertNotNull(user);
        Assert.assertEquals("Johannes", user.getName());
        Assert.assertEquals(3, user.getId());

    }
}
