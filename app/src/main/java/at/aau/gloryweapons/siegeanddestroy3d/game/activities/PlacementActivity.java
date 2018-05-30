package at.aau.gloryweapons.siegeanddestroy3d.game.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.R;
import at.aau.gloryweapons.siegeanddestroy3d.game.activities.renderer.BoardRenderer;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.views.GameBoardImageView;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ClientGameHandlerAsyncCommunication;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ServerGameHandlerAsyncCommunication;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;

public class PlacementActivity extends AppCompatActivity {
    private BoardRenderer gridRenderer;

    // visual and logical board
    private GameBoardImageView[][] visualBoard = null;
    private BattleArea playerBoard = null;

    // placement as states
    private BasicShip[] ships = null;
    private int idxShipToPlace = 0;
    private boolean placementInProgress = false;

    // ui elements
    Button btnRotateShip;
    Button btnReady;
    Button btnRestartPlacement;
    TextView txtInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        // init the renderer
        gridRenderer = new BoardRenderer(this);

        // init the ui elements
        btnRotateShip = findViewById(R.id.buttonRotateShip);
        btnReady = findViewById(R.id.buttonReady);
        btnRestartPlacement = findViewById(R.id.buttonRestartPlacement);
        txtInformation = findViewById(R.id.textViewInformation);

        // let the user place the ships
        initShipPlacementProcess();
    }

    /**
     * Switches from this activity to the GameTurnsActivity
     *
     * @param gameConfig
     */
    private void switchToGameActivity(GameConfiguration gameConfig) {
        Intent switchActivityIntent = new Intent(this, GameTurnsActivity.class);
        switchActivityIntent.putExtra(GameConfiguration.INTENT_KEYWORD, gameConfig);
        startActivity(switchActivityIntent);

        // remove this activity from the history stack - user shouldnt be able to get back to placement
        this.finish();
    }

    /**
     * Inits the ship placement process and starts with the first ship.
     */
    private void initShipPlacementProcess() {
        int nRows = GlobalGameSettings.getCurrent().getNumberRows();
        int nCols = GlobalGameSettings.getCurrent().getNumberColumns();

        // init the visual board with water tiles
        visualBoard = showEmptyBoard(nRows, nCols);

        // init the logical board with water tiles
        playerBoard = new BattleArea(GlobalGameSettings.getCurrent().getPlayerId(), nRows, nCols);

        placementInProgress = true;
        updateButtonVisibility(true);

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

        // (1) create all ships
        ships = new BasicShip[GlobalGameSettings.getCurrent().getNumberShips()];
        for (int i = 0; i < ships.length; ++i)
            ships[i] = new BasicShip(GlobalGameSettings.getCurrent().getPlayerId(),
                    GlobalGameSettings.getCurrent().getShipSizes()[i],
                    true);

        // (2) display first ship in preview
        idxShipToPlace = 0;
        prepareNextShipPlacement();

        // (3) listen for ship rotation
        btnRotateShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placementInProgress) {
                    ships[idxShipToPlace].toggleOrientation();
                    placeShipOnPreviewGrid(ships[idxShipToPlace]);
                }
            }
        });

        // (6) restart the placement process if the user is not satisfied.
        btnRestartPlacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // (6) restart the placement process if the user is not satisfied.
                initShipPlacementProcess();
            }
        });
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
     * The method places the ship and starts initialization for the next ship placement or finishes up if the last ship was placed.
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
        ++idxShipToPlace;
        if (idxShipToPlace < ships.length) {
            prepareNextShipPlacement();
        } else {
            finishShipPlacement();
        }
    }

    private void finishShipPlacement() {
        placementInProgress = false;
        // update the layout to indicate the placement process is complete
        updateButtonVisibility(false);
        placeShipOnPreviewGrid(null);

        // (7) send ships to server
        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // diable ui interactions
                btnRotateShip.setVisibility(View.GONE);
                btnRestartPlacement.setVisibility(View.GONE);
                btnReady.setVisibility(View.GONE);
                txtInformation.setText("Warten auf Mitspieler...");

                sendConfigurationToServer();
            }
        });
    }

    private void sendConfigurationToServer() {
        NetworkCommunicator comm;

        // should be inited already
        if (GlobalGameSettings.getCurrent().isServer())
            comm = ServerGameHandlerAsyncCommunication.getInstance();
        else
            comm = ClientGameHandlerAsyncCommunication.getInstance();

        CallbackObject<GameConfiguration> callback = new CallbackObject<GameConfiguration>() {
            @Override
            public void callback(GameConfiguration param) {
                switchToGameActivity(param);
            }
        };
        comm.sendGameConfigurationToServer(GlobalGameSettings.getCurrent().getLocalUser(), playerBoard, Arrays.asList(ships), callback);
    }

    /**
     * Updates the visibility of the buttons. Depending on the parameter the buttons are either shown or hidden.
     *
     * @param placementActive Indicates if the placement process is currently active.
     */
    private void updateButtonVisibility(boolean placementActive) {
        int placementButtonsVisibility = placementActive ? View.VISIBLE : View.INVISIBLE;
        btnRotateShip.setVisibility(placementButtonsVisibility);

        int finishButtonsVisibility = !placementActive ? View.VISIBLE : View.INVISIBLE;
        btnReady.setVisibility(finishButtonsVisibility);
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
     * Creates an empty board with only water and displays it on the placement grid.
     *
     * @param nRows
     * @param nCols
     * @return The ImageViews of the visual board.
     */
    private GameBoardImageView[][] showEmptyBoard(int nRows, int nCols) {
        GridLayout grid = findViewById(R.id.gridPlacementBoard);
        grid.removeAllViews();
        grid.setRowCount(nRows);
        grid.setColumnCount(nCols);

        GameBoardImageView[][] visBoard = new GameBoardImageView[nRows][nCols];

        // set water
        for (int i = 0; i < nRows; ++i)
            for (int j = 0; j < nCols; ++j) {
                visBoard[i][j] = addImageToGrid(grid, R.drawable.water, i, j, 0);

                // add click listener for the view
                visBoard[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GameBoardImageView view = (GameBoardImageView) v;
                        uiTileTapped(view.getBoardRow(), view.getBoardCol());
                    }
                });
            }

        return visBoard;
    }

    private GameBoardImageView addImageToGrid(GridLayout grid, int imageResource, int row, int col, int orientation) {
        return gridRenderer.addImageToGrid(grid, imageResource, row, col, orientation);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
