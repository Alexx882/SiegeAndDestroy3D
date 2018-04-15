package at.aau.gloryweapons.siegeanddestroy3d.gameActivities;

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
import at.aau.gloryweapons.siegeanddestroy3d.gameViews.GameBoardImageView;

public class PlacementActivity extends AppCompatActivity {
    // visual and logical board
    private GameBoardImageView[][] visualBoard = null;
    private BattleArea playerBoard = null;

    // placement as states
    private BasicShip[] ships = null;
    private int idxShipToPlace = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        int nRows = GlobalGameSettings.getCurrent().getNumberRows();
        int nCols = GlobalGameSettings.getCurrent().getNumberColumns();

        // init the visual board with water tiles
        visualBoard = showEmptyBoard(nRows, nCols);

        // init the logical board with water
        playerBoard = new BattleArea(GlobalGameSettings.getCurrent().getPlayerId(), nRows, nCols);

        // let the user place the ships
        placeShips();
    }

    private void placeShips() {
        // 1. load/create all ships
        // 2. display next ship in preview
        // TODO 3. user places ship on board - ASYNC
        // TODO 4. set ship logically and visually
        // TODO 5. if another unplaced ship exists goto(2)
        // TODO 6. send placed ships to server

        // 1)
        ships = new BasicShip[GlobalGameSettings.getCurrent().getNumberShips()];
        for (int i = 0; i < ships.length; ++i)
            ships[i] = new BasicShip(GlobalGameSettings.getCurrent().getPlayerId(),
                    GlobalGameSettings.getCurrent().getShipSizes()[i],
                    true);

        // 2)
        idxShipToPlace = 0;
        placeNextShip();
    }

    private void placeNextShip() {
        // 2)
        placeShipOnPreviewGrid(ships[idxShipToPlace]);

        // 3)
        // listen for ship rotation
        Button rotLeft = findViewById(R.id.buttonRotateLeft);
        Button rotRight = findViewById(R.id.buttonRotateRight);
        View.OnClickListener rotateListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ships[idxShipToPlace].toggleOrientation();
                placeShipOnPreviewGrid(ships[idxShipToPlace]);
            }
        };
        rotLeft.setOnClickListener(rotateListener);
        rotRight.setOnClickListener(rotateListener);
    }

    /**
     * Shows a ship on the preview grid.
     *
     * @param ship The ship to show.
     */
    private void placeShipOnPreviewGrid(BasicShip ship) {
        if (ship == null)
            throw new IllegalArgumentException("ship");

        // prepare preview grid
        GridLayout previewGrid = findViewById(R.id.gridShipPreview);
        previewGrid.removeAllViews();

        // place ship
        int orientationInDegrees = ship.isHorizontal() ? 0 : 90;
        if (ship.isHorizontal()) {
            // place the ship horizontally
            previewGrid.setRowCount(1);
            previewGrid.setColumnCount(ship.getLength());

            int row = 0;
            // first and last is fixed
            addImageToGrid(previewGrid, R.drawable.ship_start, row, 0, orientationInDegrees);
            addImageToGrid(previewGrid, R.drawable.ship_end, row, ship.getLength() - 1, orientationInDegrees);
            // rest is dynamic
            for (int i = 1; i < ship.getLength() - 1; ++i)
                addImageToGrid(previewGrid, R.drawable.ship_middle, row, i, orientationInDegrees);

        } else {
            // place the ship vertically
            previewGrid.setRowCount(ship.getLength());
            previewGrid.setColumnCount(1);

            int col = 0;
            // first and last is fixed
            addImageToGrid(previewGrid, R.drawable.ship_start, 0, col, orientationInDegrees);
            addImageToGrid(previewGrid, R.drawable.ship_end, ship.getLength() - 1, col, orientationInDegrees);
            // rest is dynamic
            for (int i = 1; i < ship.getLength() - 1; ++i)
                addImageToGrid(previewGrid, R.drawable.ship_middle, i, col, orientationInDegrees);
        }
    }

    private void uiTileClicked(int row, int col) {
        Toast.makeText(this, row + " : " + col, Toast.LENGTH_LONG).show();

        // 3)
        // place the start of the ship where the user pressed the button
        BasicShip shipToPlace = ships[idxShipToPlace];

        // if the ship would reach outside the field, move it back in
        if (shipToPlace.isHorizontal()) {
            if (playerBoard.getRowNumber() < col + shipToPlace.getLength())
                col = playerBoard.getRowNumber() - shipToPlace.getLength();
        } else {
            if (playerBoard.getColumnNumber() < row + shipToPlace.getLength())
                row = playerBoard.getColumnNumber() - shipToPlace.getLength();
        }

        // place the board logically and visually
        playerBoard.placeShip(shipToPlace, row, col);
        placeShipOnVisualBoard(shipToPlace, row, col);

    }

    private void placeShipOnVisualBoard(BasicShip shipToPlace, int row, int col) {
        // place the ship by setting the images
        for (int i = 0; i < shipToPlace.getLength(); ++i) {
            // select correct image resource for position
            int imgResource;
            if (i == 0)
                imgResource = R.drawable.ship_start;
            else if (i == shipToPlace.getLength() - 1)
                imgResource = R.drawable.ship_end;
            else
                imgResource = R.drawable.ship_middle;

            // set resource and rotate view
            if (shipToPlace.isHorizontal()) {
                visualBoard[row][col + i].setImageResource(imgResource);
                visualBoard[row][col + i].setRotation(0);
            } else {
                visualBoard[row + i][col].setImageResource(imgResource);
                visualBoard[row + i][col].setRotation(90);
            }
        }
    }

    /**
     * Creates an empty board with only water and displays it.
     *
     * @param nRows
     * @param nCols
     * @return The ImageViews of the visual board.
     */
    private GameBoardImageView[][] showEmptyBoard(int nRows, int nCols) {
        GridLayout grid = findViewById(R.id.gridPlacementBoard);
        grid.setRowCount(nRows);
        grid.setColumnCount(nCols);

        GameBoardImageView[][] visualBoard = new GameBoardImageView[nRows][nCols];

        // set water
        for (int i = 0; i < nRows; ++i)
            for (int j = 0; j < nCols; ++j)
                visualBoard[i][j] = addImageToGrid(grid, R.drawable.water, i, j, 0);

        return visualBoard;
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
    private GameBoardImageView addImageToGrid(GridLayout grid, int imageResource, int row, int col, int orientation) {
        // create image view and set image
        GameBoardImageView view = new GameBoardImageView(this, row, col);
        view.setImageResource(imageResource);
        view.setRotation(orientation);

        // add click listener for the view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameBoardImageView view = (GameBoardImageView) v;
                uiTileClicked(view.getBoardRow(), view.getBoardCol());
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

        // add view to grid
        grid.addView(view);

        // return the created view
        return view;
    }
}
