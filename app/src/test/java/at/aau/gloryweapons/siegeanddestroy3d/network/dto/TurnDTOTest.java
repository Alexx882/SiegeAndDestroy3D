package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;

import static org.junit.Assert.*;

public class TurnDTOTest {

    @Test
    public void TurnDTOYCoordinateTest() {
        TurnDTO turn = new TurnDTO();
        turn.setyCoordinates(1);
        assertEquals(1, turn.getyCoordinates());
    }

    @Test
    public void TurnDTOXCoordinateTest() {
        TurnDTO turn = new TurnDTO();
        turn.setxCoordinates(1);
        assertEquals(1, turn.getxCoordinates());
    }

    @Test
    public void TurnDTOUserIdTest() {
        BattleArea area = new BattleArea();
        area.setUserId(1);
        TurnDTO turn = new TurnDTO();
        turn.setUserId(1);
        assertEquals(1, turn.getUserId());

        turn = new TurnDTO(null, area);
        assertEquals(1, turn.getUserId());
    }

    @Test
    public void TurnDTONoHitTest() {
        TurnDTO turn = new TurnDTO();
        turn.setType(TurnDTO.TurnType.NO_HIT);
        assertEquals(TurnDTO.TurnType.NO_HIT, turn.getType());
    }

    @Test
    public void TurnDTOErrorTest() {
        TurnDTO turn = new TurnDTO();
        turn.setType(TurnDTO.TurnType.ERROR);
        assertEquals(TurnDTO.TurnType.ERROR, turn.getType());
    }

    @Test
    public void TurnDTOPowerUpTest() {
        TurnDTO turn = new TurnDTO();
        turn.setType(TurnDTO.TurnType.POWERUP);
        assertEquals(TurnDTO.TurnType.POWERUP, turn.getType());
    }

    @Test
    public void TurnDTOHitTest() {
        TurnDTO turn = new TurnDTO();
        turn.setType(TurnDTO.TurnType.HIT);
        assertEquals(TurnDTO.TurnType.HIT, turn.getType());
    }

    @Test
    public void TurnDTOShotTest() {
        TurnDTO turn = new TurnDTO();
        turn.setType(TurnDTO.TurnType.SHOT);
        assertEquals(TurnDTO.TurnType.SHOT, turn.getType());
    }

}