package at.aau.gloryweapons.siegeanddestroy3d.network;

import org.junit.Assert;
import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

public class ServerGameHandlerTest {

    private ServerGameHandler gameHandler = new ServerGameHandler(GlobalGameSettings.getCurrent());

    @Test
    public void addUser() {
        for (int i = 1; i <= 4; i++) {
            User user = new User(i, "10.0.0." +i, "user_" +i);
            gameHandler.addUser(user);

            //test
            Assert.assertEquals(gameHandler.getUsers().get(i -1).getId(), i);
            Assert.assertEquals(gameHandler.getUsers().get(i -1).getIp(), "10.0.0." +i);
            Assert.assertEquals(gameHandler.getUsers().get(i -1).getName(), "user_" +i);
        }


    }

    @Test
    public void addBattleArea() {
        int userId = 123;
        BattleArea battleArea = new BattleArea(123, 9);


        for (int i = 0; i < battleArea.getBattleAreaTiles().length ; i++) {
            for (int j = 0; j <battleArea.getBattleAreaTiles().length ; j++) {
                battleArea.getBattleAreaTiles()[i][j] = new BattleAreaTile();
            }
        }

        Assert.assertEquals(false, gameHandler.addBattleArea(battleArea));

    }

    @Test
    public void getCurrentUser() {
        User user = new User(555, "10.0.0.555" , "user_555");
        gameHandler.addUser(user);

        Assert.assertEquals(gameHandler.getCurrentUser().getId(), 555);
        Assert.assertEquals(gameHandler.getCurrentUser().getIp(), "10.0.0.555");
        Assert.assertEquals(gameHandler.getCurrentUser().getName(), "user_555");

    }

    @Test
    public void nextUser() {
        User user1 = new User(111, "10.0.0.111" , "user_111");
        User user2 = new User(222, "10.0.0.222" , "user_222");
        gameHandler.addUser(user1);
        gameHandler.addUser(user2);

        Assert.assertEquals(gameHandler.getCurrentUser().getId(), 111);
        Assert.assertEquals(gameHandler.getCurrentUser().getIp(), "10.0.0.111");
        Assert.assertEquals(gameHandler.getCurrentUser().getName(), "user_111");

        gameHandler.nextUser();

        Assert.assertEquals(gameHandler.getCurrentUser().getId(), 222);
        Assert.assertEquals(gameHandler.getCurrentUser().getIp(), "10.0.0.222");
        Assert.assertEquals(gameHandler.getCurrentUser().getName(), "user_222");

        gameHandler.nextUser();

        Assert.assertEquals(gameHandler.getCurrentUser().getId(), 111);
        Assert.assertEquals(gameHandler.getCurrentUser().getIp(), "10.0.0.111");
        Assert.assertEquals(gameHandler.getCurrentUser().getName(), "user_111");
    }


}