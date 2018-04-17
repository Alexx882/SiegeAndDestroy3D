package at.aau.gloryweapons.siegeanddestroy3d.game.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.Arrays;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.R;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.views.GameBoardImageView;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.DummyNetworkCommunicator;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;

public class PlacementActivity extends AppCompatActivity {
    // visual and logical board
    private GameBoardImageView[][] visualBoard = null;
    private BattleArea playerBoard = null;

    // placement as states
    private BasicShip[] ships = null;
    private int idxShipToPlace = 0;
    private boolean placementInProgress = false;

    // btns
    Button btnRotateLeft;
    Button btnRotateRight;

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
        placementInProgress = true;
        /*
        Process:
        1. load/create all ships
        2. display next ship in preview
        3. user places ship on board - ASYNC
        4. set ship logically and visually
        5. if another unplaced ship exists goto(2)
        6. check if user is satisfied with placement configuration
        7. send placed ships to server
        */

        // (1) load/create all ships
        ships = new BasicShip[GlobalGameSettings.getCurrent().getNumberShips()];
        for (int i = 0; i < ships.length; ++i)
            ships[i] = new BasicShip(GlobalGameSettings.getCurrent().getPlayerId(),
                    GlobalGameSettings.getCurrent().getShipSizes()[i],
                    true);

        // (2) display first ship in preview
        idxShipToPlace = 0;
        prepareNextShipPlacement();

        btnRotateLeft = findViewById(R.id.buttonRotateLeft);
        btnRotateRight = findViewById(R.id.buttonRotateRight);
        // (3) listen for ship rotation
        View.OnClickListener rotateListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placementInProgress) {
                    ships[idxShipToPlace].toggleOrientation();
                    placeShipOnPreviewGrid(ships[idxShipToPlace]);
                }
            }
        };
        btnRotateLeft.setOnClickListener(rotateListener);
        btnRotateRight.setOnClickListener(rotateListener);
    }

    private void prepareNextShipPlacement() {
        // (2) display next ship in preview
        placeShipOnPreviewGrid(ships[idxShipToPlace]);
    }

    /**
     * Shows a ship on the preview grid.
     *
     * @param ship The ship to show. Null clears the grid.
     */
    private void placeShipOnPreviewGrid(BasicShip ship) {
        // prepare preview grid
        GridLayout previewGrid = findViewById(R.id.gridShipPreview);
        previewGrid.removeAllViews();

        // return if the method was called to clear the grid
        if (ship == null)
            return;

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

    /**
     * Used to handle taps from the user.
     *
     * @param row
     * @param col
     */
    private void uiTileTapped(int row, int col) {
        if (!placementInProgress)
            return;

        // (3) user wants to place ship
        BasicShip shipToPlace = ships[idxShipToPlace];

        // check input
        String msg = null;
        if (!playerBoard.isShipInBattleArea(shipToPlace, row, col))
            msg = "Bitte das Schiff vollständig ins Spielfeld setzen.";
        else if (!playerBoard.isShipPositionAvailable(shipToPlace, row, col))
            msg = "Bitte das Schiff nicht mit anderen Schiffen überlappen.";

        if (msg != null) {
            showToast(msg);
            return;
        }

        // (4) place ship logically and visually
        playerBoard.placeShip(shipToPlace, row, col);
        placeShipOnVisualBoard(shipToPlace, row, col);

        // (5) prepare placing the next ship
        if (++idxShipToPlace < ships.length) {
            prepareNextShipPlacement();
        } else {
            finishShipPlacement();
        }
    }

    private void finishShipPlacement() {
        placementInProgress = false;
        btnRotateLeft.setClickable(false);
        btnRotateRight.setClickable(false);
        placeShipOnPreviewGrid(null);

        // TODO (6) let user accept the config or restart placement

        // (7) send ships to server
        NetworkCommunicator comm = new DummyNetworkCommunicator();
        comm.sendGameConfigurationToServer(null, playerBoard, Arrays.asList(ships), null); // TODO
    }

    /**
     * Shows the ship on the views which are located on the gridPlacementBoard.
     *
     * @param shipToPlace
     * @param row
     * @param col
     */
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
                uiTileTapped(view.getBoardRow(), view.getBoardCol());
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
