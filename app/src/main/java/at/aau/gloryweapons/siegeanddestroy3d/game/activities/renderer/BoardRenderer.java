package at.aau.gloryweapons.siegeanddestroy3d.game.activities.renderer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.GridLayout;

import at.aau.gloryweapons.siegeanddestroy3d.game.views.GameBoardImageView;

/**
 * Renderer for the visual boards.
 */
public class BoardRenderer {
    private Context contextActivity;

    public BoardRenderer(Context contextActivity) {
        if (contextActivity == null)
            throw new IllegalArgumentException("contextActivity");

        this.contextActivity = contextActivity;
    }

    /**
     * Adds an image to the grid with provided coordinates either horizontally or vertically.
     *
     * @param grid          The grid to show the image.
     * @param imageResource The image.
     * @param row           The row of the grid to use.
     * @param col           The column of the grid to use.
     * @param orientation   The orientation of the image in degrees.
     * @return The newly created ImageView.
     */
    public GameBoardImageView addImageToGrid(GridLayout grid, int imageResource,
                                             int row, int col, int orientation) {
        // create image view and set image
        GameBoardImageView view = new GameBoardImageView(contextActivity, row, col);
        view.setImageResource(imageResource);
        view.setRotation(orientation);

        // create params for grid
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = GridLayout.LayoutParams.WRAP_CONTENT;
        param.width = GridLayout.LayoutParams.WRAP_CONTENT;
        param.topMargin = 5;
        param.rightMargin = 5;
        param.setGravity(Gravity.CENTER);
        param.rowSpec = GridLayout.spec(row);
        param.columnSpec = GridLayout.spec(col);

        // apply params
        view.setLayoutParams(param);

        // add view to grid
        grid.addView(view);

        // return the created view
        return view;
    }
}
