package at.aau.gloryweapons.siegeanddestroy3d.game.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    User user;

    @Test
    public void userIdTest() {
        User u = new User();
        u.setId(1);
        assertEquals(1, u.getId());
    }

    @Test
    public void userNameTest() {
        User u = new User();
        u.setName("Klaus");
        assertEquals("Klaus", u.getName());
    }

}