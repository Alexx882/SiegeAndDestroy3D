package at.aau.gloryweapons.siegeanddestroy3d.server;

import org.junit.Assert;
import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheaterSuspicionResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheatingDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;

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


    @Test
    public void checkGetUserForNextTurn(){
        ServerController serverController = new ServerController();

        User user1 = new User(1,"userTest_1");
        User user2 = new User(2,"userTest_2");
        serverController.addDataToGameConfig(user1,null,null,null);
        serverController.addDataToGameConfig(user2,null,null,null);

        User testUser1 = serverController.getUserForFirstTurn();
        Assert.assertEquals(1,testUser1.getId());

        User testUser2 = serverController.getUserForNextTurn();
        Assert.assertEquals(2,testUser2.getId());
    }

    @Test
    public void checkCheckForWinner(){
        ServerController serverController = new ServerController();

        User user1 = new User(1,"userTest_1");
        User user2 = new User(2,"userTest_2");

        serverController.addDataToGameConfig(user1,null,null,null);
        serverController.addDataToGameConfig(user2,null,null,null);

        Assert.assertEquals(null,serverController.checkForWinner());

        serverController.setUserDefeated(user2.getId());
        Assert.assertEquals(user1,serverController.checkForWinner());
    }
}
