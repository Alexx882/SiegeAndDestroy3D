package at.aau.gloryweapons.siegeanddestroy3d.game.activities.renderer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.GridLayout;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.aau.gloryweapons.siegeanddestroy3d.R;
import at.aau.gloryweapons.siegeanddestroy3d.game.views.GameBoardImageView;

/**
 * Created by Alexander on 19.06.2018.
 */

@RunWith(AndroidJUnit4.class)
public class BoardRendererTests {
    Context appContext;
    BoardRenderer renderer;

    @Before
    public void before() {
        appContext = InstrumentationRegistry.getTargetContext();
        renderer = new BoardRenderer(appContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_invalid() {
        BoardRenderer renderer = new BoardRenderer(null);
    }

    @Test
    public void constructor_valid() {
        Context context = InstrumentationRegistry.getTargetContext();
        BoardRenderer r = new BoardRenderer(context);
        Assert.assertNotNull(r);
    }

    @Test
    public void addImageToGrid() {
        GridLayout grid = new GridLayout(appContext);
        grid.setRowCount(3);
        grid.setColumnCount(3);

        GameBoardImageView view;
        view = renderer.addImageToGrid(grid, R.drawable.water_tiles, 0, 0, 0);
        Assert.assertEquals(0, view.getBoardRow());
        Assert.assertEquals(0, view.getBoardCol());
        Assert.assertEquals(1, grid.getChildCount());

        view = renderer.addImageToGrid(grid, R.drawable.water_tiles, 1, 2, 0);
        Assert.assertEquals(1, view.getBoardRow());
        Assert.assertEquals(2, view.getBoardCol());
        Assert.assertEquals(2, grid.getChildCount());
    }
}
