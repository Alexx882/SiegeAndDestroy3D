package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

import static org.junit.Assert.*;

public class TurnInfoDTOTest {

    @Test
    public void TurnInfoDTOTest(){
        User u = new User();
        TurnInfoDTO turninfo= new TurnInfoDTO();
        turninfo.setPlayerNextTurn(u);

        assertEquals(u,turninfo.getPlayerNextTurn());
    }

}