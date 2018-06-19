package at.aau.gloryweapons.siegeanddestroy3d.game.views;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.aau.gloryweapons.siegeanddestroy3d.game.activities.renderer.BoardRenderer;

/**
 * Created by Alexander on 19.06.2018.
 */

@RunWith(AndroidJUnit4.class)
public class GameBoardImageViewTests {
    Context appContext;

    @Before
    public void before() {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void constructor() {
        GameBoardImageView view;
        view = new GameBoardImageView(appContext, 0, 0);
        Assert.assertEquals(0, view.getBoardRow());
        Assert.assertEquals(0, view.getBoardCol());


        view = new GameBoardImageView(appContext, 3, 7);
        Assert.assertEquals(3, view.getBoardRow());
        Assert.assertEquals(7, view.getBoardCol());
    }
}
