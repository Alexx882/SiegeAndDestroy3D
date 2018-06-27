package at.aau.gloryweapons.siegeanddestroy3d.game.activities;



import org.junit.Test;

import at.aau.gloryweapons.siegeanddestroy3d.R;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.views.GameBoardImageView;

import static at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile.TileType.NO_HIT;
import static at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile.TileType.SHIP_START_DESTROYED;
import static at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile.TileType.SHIP_MIDDLE_DESTROYED;
import static at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile.TileType.SHIP_END_DESTROYED;
import static at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile.TileType.SHIP_END;
import static at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile.TileType.SHIP_MIDDLE;
import static at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile.TileType.SHIP_START;
import static at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile.TileType.WATER;
import static org.junit.Assert.*;

public class GameTurnsActivityTest {

    @Test
    public void onCreate() {
    }

    @Test
    public void useSensorsforCheating() {
    }

    @Test
    public void onPause() {
    }

    @Test
    public void onResume() {
    }
    @Test
    public void TestTheReturnedTileIfNO_HIT()
    {
        GameTurnsActivity gta = new GameTurnsActivity();
        assertEquals(R.drawable.no_hitx,gta.getTheRightTile(NO_HIT));
    }
    @Test
    public void TestTheReturnedTileIfWater()
    {
        GameTurnsActivity gta = new GameTurnsActivity();
        assertEquals(R.drawable.water_tiles,gta.getTheRightTile(WATER));
    }
    @Test
    public void TestTheReturnedTileIfSHIP_START()
    {
        GameTurnsActivity gta = new GameTurnsActivity();
        assertEquals(R.drawable.shipbig_start,gta.getTheRightTile(SHIP_START));
    }
    @Test
    public void TestTheReturnedTileIfSHIP_MIDDLE()
    {
        GameTurnsActivity gta = new GameTurnsActivity();
        assertEquals(R.drawable.shipbig_middle,gta.getTheRightTile(SHIP_MIDDLE));
    }
    @Test
    public void TestTheReturnedTileIfSHIP_END()
    {
        GameTurnsActivity gta = new GameTurnsActivity();
        assertEquals(R.drawable.shipbig_end,gta.getTheRightTile(SHIP_END));
    }

    @Test
    public void TestTheReturnedTileIfSHIP_START_DESTROYED()
    {
        GameTurnsActivity gta = new GameTurnsActivity();
        assertEquals(R.drawable.ship_destroyed_explo,gta.getTheRightTile(SHIP_START_DESTROYED));
    }
    @Test
    public void TestTheReturnedTileIfSHIP_MIDDLE_DESTROYED()
    {
        GameTurnsActivity gta = new GameTurnsActivity();
        assertEquals(R.drawable.ship_destroyed_explo,gta.getTheRightTile(SHIP_MIDDLE_DESTROYED));
    }
    @Test
    public void TestTheReturnedTileIfSHIP_END_DESTROYED()
    {
        GameTurnsActivity gta = new GameTurnsActivity();
        assertEquals(R.drawable.ship_destroyed_explo,gta.getTheRightTile(SHIP_END_DESTROYED));
    }

    /**
     * not possible, because of ENUM
     */
    @Test
    public void TestTheReturnedTileIfWrongTile()
    {

    }
}