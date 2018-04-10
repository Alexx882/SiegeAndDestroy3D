package at.aau.gloryweapons.siegeanddestroy3d.gameActivities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.R;
import at.aau.gloryweapons.siegeanddestroy3d.gameModels.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.gameModels.BattleArea;

public class PlacementActivity extends AppCompatActivity {
    private int _idxShipToPlace = 0;
    private boolean _placeHorizontal = true;
    private BasicShip[] _ships = new BasicShip[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        _ships = new BasicShip[GlobalGameSettings.getCurrent().getNumberShips()];

        // inti everything as water
        initBoardWithWater();

        // start placement of first ship
        _idxShipToPlace = 0;
        _placeHorizontal = true;
        placeNextShip();
    }

    private void placeNextShip() {
        GridLayout grid = findViewById(R.id.gridShipPreview);
        grid.removeAllViews();

        // place ship initially
        _ships[_idxShipToPlace] = new BasicShip(GlobalGameSettings.getCurrent().getPlayerId(), GlobalGameSettings.getCurrent().getShipSizes()[_idxShipToPlace], true);
        placeShipOnPreviewGrid(grid, _ships[_idxShipToPlace], _placeHorizontal);

        // listen for ship rotation
        Button rotLeft = findViewById(R.id.buttonRotateLeft);
        Button rotRight = findViewById(R.id.buttonRotateRight);
        View.OnClickListener rotateListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GridLayout grid = findViewById(R.id.gridShipPreview);
                grid.removeAllViews();

                _placeHorizontal = !_placeHorizontal;
                placeShipOnPreviewGrid((GridLayout) findViewById(R.id.gridShipPreview), _ships[_idxShipToPlace], _placeHorizontal);
            }
        };
        rotLeft.setOnClickListener(rotateListener);
        rotRight.setOnClickListener(rotateListener);
    }

    private void placeShipOnPreviewGrid(GridLayout previewGrid, BasicShip ship, boolean horizontal) {
        if (horizontal) {
            previewGrid.setRowCount(1);
            previewGrid.setColumnCount(ship.getLength());

            int row = 0;
            // first and last is fixed
            addImageViewToGrid(previewGrid, R.drawable.ship_start, row, 0);
            addImageViewToGrid(previewGrid, R.drawable.ship_end, row, ship.getLength() - 1);
            // rest is dyn
            for (int i = 1; i < ship.getLength() - 1; ++i)
                addImageViewToGrid(previewGrid, R.drawable.ship_middle, row, i);
        } else { // vertical
            previewGrid.setRowCount(ship.getLength());
            previewGrid.setColumnCount(1);

            int col = 0;
            // first and last is fixed
            addImageViewToGrid(previewGrid, R.drawable.ship_start, 0, col, false);
            addImageViewToGrid(previewGrid, R.drawable.ship_end, ship.getLength() - 1, col, false);
            // rest is dyn
            for (int i = 1; i < ship.getLength() - 1; ++i)
                addImageViewToGrid(previewGrid, R.drawable.ship_middle, i, col, false);
        }
    }

    private void initBoardWithWater() {
        int nRows = GlobalGameSettings.getCurrent().getNumberRows();
        int nCols = GlobalGameSettings.getCurrent().getNumberColumns();

        GridLayout grid = findViewById(R.id.gridPlacementBoard);
        grid.setRowCount(nRows);
        grid.setColumnCount(nCols);

        // init new board with water
        BattleArea board = new BattleArea(GlobalGameSettings.getCurrent().getPlayerId(), nRows, nCols);

        for (int i = 0; i < nRows; ++i)
            for (int j = 0; j < nCols; ++j)
                addImageViewToGrid(grid, R.drawable.water, i, j);
    }

    private void addImageViewToGrid(GridLayout grid, int imageResource, int row, int col) {
        // "default" value for parameter
        addImageViewToGrid(grid, imageResource, row, col, true);
    }

    private void addImageViewToGrid(GridLayout grid, int imageResource, int row, int col, boolean horizontal) {
        // create imageview
        ImageView view = new ImageView(this);
        view.setImageResource(imageResource);
        if (!horizontal)
            view.setRotation(90);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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

        // add to grid
        grid.addView(view);
    }
}
