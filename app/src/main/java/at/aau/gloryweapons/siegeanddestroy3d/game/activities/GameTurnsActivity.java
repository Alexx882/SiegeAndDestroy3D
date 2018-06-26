package at.aau.gloryweapons.siegeanddestroy3d.game.activities;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.R;

import at.aau.gloryweapons.siegeanddestroy3d.game.controllers.GameController;
import at.aau.gloryweapons.siegeanddestroy3d.game.activities.renderer.BoardRenderer;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.ReturnObject;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.ShipContainer;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.game.views.GameBoardImageView;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheaterSuspicionResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.CheatingResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnInfoDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.sensors.CheatEventListener;

public class GameTurnsActivity extends AppCompatActivity {
    private GameConfiguration gameSettings = null;
    private GameController controller = null;
    private BoardRenderer board = null;
    private User actualUser = null;
    private BattleArea actualBattleArea = null;
    private boolean shooting = false;
    private Button cheaterSuspectedButton;
    private Button applyShotsButton;
    private List<TextView> userLabels = new ArrayList<>(4);
    private GameBoardImageView[][] currentBoardView;
    TextView textViewUserTurn = null;
    private long initTimeStamp = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enemy_turn);
        GameBoardImageView[][] visualBoard = null;
        textViewUserTurn = (TextView) findViewById(R.id.textViewUserTurn);

        // receive and set parameters
        gameSettings = (GameConfiguration) getIntent().getExtras().get(GameConfiguration.INTENT_KEYWORD);
        GlobalGameSettings.setCurrent((GlobalGameSettings) getIntent().getExtras().get(GlobalGameSettings.INTENT_KEYWORD));
        GlobalGameSettings.getCurrent().setNumberShots(gameSettings.getShots());

        board = new BoardRenderer(this);
        controller = new GameController(quitGameMessage());

        final int nRows = GlobalGameSettings.getCurrent().getNumberRows();
        final int nCols = GlobalGameSettings.getCurrent().getNumberColumns();

        //gets the right user
        for (User u : gameSettings.getUserList()) {
            if (u.getId() == GlobalGameSettings.getCurrent().getPlayerId()) {
                actualUser = u;
            }
        }

        //gets the BattleArea of the Client.
        for (BattleArea area : gameSettings.getBattleAreaList()) {
            if (area.getUserId() == actualUser.getId()) {
                visualBoard = loadBattleArea(area, nRows, nCols);
                actualBattleArea = area;
            }
        }

        //sets the OnClickListener for the Button to end the turn
        applyShotsButton = findViewById(R.id.buttonApplyShots);
        applyShotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!controller.endTurn()) {
                    Toast.makeText(GameTurnsActivity.this, "first finish your turn", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerCheaterSuspicion();

        //adding playerLabels to ConstraintLayout.
        ConstraintLayout userLayout = findViewById(R.id.UserLayout);

        for (int i = 0; i < gameSettings.getUserList().size(); i++) {
            //create textview + setText with Username + ontouchlistener
            TextView v = createUserLabel(nRows, nCols, i);
            //set params for view
            ConstraintLayout.LayoutParams viewParam = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

            userLayout.addView(v, viewParam);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(userLayout);

            int marginTop = 8 + i * 95;
            constraintSet.connect(v.getId(), ConstraintSet.RIGHT, userLayout.getId(), ConstraintSet.RIGHT, 20);
            constraintSet.connect(v.getId(), ConstraintSet.LEFT, userLayout.getId(), ConstraintSet.LEFT, 20);
            constraintSet.connect(v.getId(), ConstraintSet.TOP, userLayout.getId(), ConstraintSet.TOP, marginTop);

            constraintSet.applyTo(userLayout);

            userLabels.add(v);
        }

        useSensorsforCheating();

        registerForWinningInfos();

        registerForCurrentUserUpdates();
    }

    private void registerCheaterSuspicion() {

        cheaterSuspectedButton = findViewById(R.id.buttonCheaterSuspect);
        cheaterSuspectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               controller.cheatingSuspicion(new CallbackObject<CheaterSuspicionResponseDTO>() {
                    @Override
                    public void callback(CheaterSuspicionResponseDTO param) {
                        if (param.getUserWhoCheats() != null) {
                            toastOnUi(param.getUserWhoCheats().getName() + " hat gecheatet!");
                        } else {
                            toastOnUi("Keiner hat geschummelt, eine Runde aussetzten!");
                        }
                    }
                });
            }
        });

    }

    private CallbackObject<Boolean> quitGameMessage() {
        CallbackObject<Boolean> quitGameCallback = new CallbackObject<Boolean>() {
            @Override
            public void callback(Boolean param) {
                toastOnUi("Der Server hat die Verbindung beendet!");
                finish();
            }
        };
        return quitGameCallback;
    }

    private void registerForWinningInfos() {
        controller.registerForWinningInfos(new CallbackObject<User>() {
            @Override
            public void callback(final User winner) {
                if (winner != null) {
                    GlobalGameSettings.getCurrent().setGameFinished(true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWinner(winner);
                            disableButtons();
                        }
                    });
                }
            }
        });
    }

    private void disableButtons() {
        applyShotsButton.setEnabled(false);
        cheaterSuspectedButton.setEnabled(false);
    }

    private void registerForCurrentUserUpdates() {
        controller.registerForCurrentTurnUserUpdates(new CallbackObject<TurnInfoDTO>() {
            @Override
            public void callback(final TurnInfoDTO ti) {
                if (ti != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewUserTurn.setText("Zug von: " + ti.getPlayerNextTurn().getName());

                            updateLocalBattleArea(ti.getShots());
                        }
                    });
                }
            }
        });
    }

    /**
     * Updates the battle area for this user with the hits she has taken.
     *
     * @param shots
     */
    private void updateLocalBattleArea(List<TurnDTO> shots) {
        for (BattleArea area : gameSettings.getBattleAreaList()) {
            if (area.getUserId() == GlobalGameSettings.getCurrent().getPlayerId()) {
                // local player area found
                controller.updateBattleAreaFromShotList(area, shots);

                // redraw if currently displayed
                if (area.equals(actualBattleArea))
                    loadBattleArea(area,
                            GlobalGameSettings.getCurrent().getNumberRows(),
                            GlobalGameSettings.getCurrent().getNumberColumns());

                break;
            }
        }

    }

    /**
     * Shows the winner in the view.
     *
     * @param winner
     */
    private void showWinner(User winner) {
        if (winner == null)
            return;

        textViewUserTurn.setTextColor(Color.GREEN);
        textViewUserTurn.setText("Gewinner: " + winner.getName());
    }

    CheatEventListener cheatListener;

    /**
     * create the labels for the users. is used in onCreate.
     *
     * @return
     */
    private TextView createUserLabel(final int nRows, final int nCols, int i) {
        TextView v = new TextView(this);
        v.setText(gameSettings.getUserByIndex(i).getName());
        v.setTextSize(25);
        v.setId(i);
        // set the ontouchlistener; methode loads the field of the selected User
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i("fish", "" + view.getId());
                actualUser = gameSettings.getUserByIndex(view.getId());
                // methodcall in controller like: changeGridToBattleAreaUser(int id) ...id of the view because the id of the view is the same as the position in the userList (ArrayList in GameSettings)
                for (BattleArea area : gameSettings.getBattleAreaList()) {
                    if (area.getUserId() == gameSettings.getUserByIndex(view.getId()).getId()) {
                        loadBattleArea(area, nRows, nCols);
                        actualBattleArea = area;
                    }
                }

                return false;
            }
        });
        return v;

    }

    /**
     * register Sensors
     */
    public void useSensorsforCheating() {
        cheatListener = new CheatEventListener(this);
        cheatListener.registerForChanges(new CallbackObject<Boolean>() {
            @Override
            public void callback(Boolean param) {
                if (param == true && !schummelnAktiv) {

                    // to avoid exceptions
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startSchummeln();
                        }
                    });
                } /*else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            endSchummeln();
                        }
                    });
//                    Toast.makeText(GameTurnsActivity.this, "Sensor active", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    private boolean schummelnAktiv = false;
    private ShipContainer currentCheat;

    private void startSchummeln() {
        if (!cheatingIsAvailable())
            return;

        // if active user is the user, return
        if (actualUser == null || actualUser.getId() == GlobalGameSettings.getCurrent().getPlayerId()) {
            toastOnUi("Du kannst dich nicht selbst beschummeln!");
            return;
        }

        // finds the weakest ship
        if (currentCheat == null){
            currentCheat = actualBattleArea.findWeakestShip();
        }

        //Toast.makeText(this, "schummeln aktiv", Toast.LENGTH_SHORT);

        // show ship for 5 sec
        showShipForShortTime(currentCheat);

        // sends to server that "thisUser" has cheated
        controller.sendCheating(new CallbackObject<CheatingResponseDTO>() {
            @Override
            public void callback(CheatingResponseDTO param) {
                Log.i(this.getClass().getName(), "cheating response: " + param);
                //TODO impl. cheating callback
                toastOnUi("Du wurdest von " + param.getCaughtByUser().getName() + " erwischt!");
            }
        });
    }

    /**
     *
     * @param shipContainer
     * gets the Rows and Cols from the Battle Area and calls the timer
     */
    private void showShipForShortTime(ShipContainer shipContainer){
        if (shipContainer == null)
            return;
        timer();
        BattleAreaTile[][] tile = actualBattleArea.getBattleAreaTiles();
        final int drawable = getTheRightTile(tile[shipContainer.getRowCheating()][shipContainer.getColCheating()].getType());
        currentBoardView[shipContainer.getRowCheating()][shipContainer.getColCheating()].setImageResource(drawable);
        int orientation = tile[shipContainer.getRowCheating()][shipContainer.getColCheating()].isHorizontal() ? 0 : 90;
        currentBoardView[shipContainer.getRowCheating()][shipContainer.getColCheating()].setRotation(orientation);
    }

    /**
     * Hides the ship after 5 sec cheating time
     */
    private void timer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //stop cheating after 5 sec
                hideShip();
            }
        }, 5000);
    }

    private void hideShip(){
        if (currentCheat == null)
            return;
        final int row = currentCheat.getRowCheating();
        final int col = currentCheat.getColCheating();
        final int drawable;
        if (actualBattleArea.getBattleAreaTiles()[row][col].getType() != BattleAreaTile.TileType.NO_HIT ||
                actualBattleArea.getBattleAreaTiles()[row][col].getType() != BattleAreaTile.TileType.SHIP_DESTROYED){
            drawable = getTheRightTile(BattleAreaTile.TileType.WATER);
        }else {
            drawable = getTheRightTile(actualBattleArea.getBattleAreaTiles()[row][col].getType());
        }

        //currentCheat = null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentBoardView[row][col].setImageResource(drawable);
                currentBoardView[row][col].setRotation(0);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        cheatListener.unregisterSensors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cheatListener.registerSensors();
    }

    /**
     * loads the battleAreas and sets the OnClickListener.
     *
     * @param area
     * @param nRows
     * @param nCols
     * @return
     */
    private GameBoardImageView[][] loadBattleArea(BattleArea area, int nRows, int nCols) {
        final GridLayout grid = findViewById(R.id.gridEnemyTurnBoard);
        grid.removeAllViews();
        grid.setRowCount(nRows);
        grid.setColumnCount(nCols);

        final GameBoardImageView[][] gView = new GameBoardImageView[nRows][nCols];
        BattleAreaTile[][] tiles = area.getBattleAreaTiles();

        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nCols; ++j) {
                int orientation = 0;
                if (!tiles[i][j].isHorizontal()) {
                    orientation = 90;
                }
                if (area.getUserId() != GlobalGameSettings.getCurrent().getPlayerId()) {
                    orientation=0;
                    switch (tiles[i][j].getType()) {
                        case SHIP_DESTROYED:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_destroyed, i, j, orientation);
                            break;
                        case NO_HIT:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.no_hit, i, j, orientation);
                            break;
                        default:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.water, i, j, orientation);
                            break;
                    }
                } else {
                    switch (tiles[i][j].getType()) {
                        case WATER:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.water, i, j, orientation);
                            break;
                        case NO_HIT:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.no_hit, i, j, orientation);
                            break;
                        case SHIP_START:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_start, i, j, orientation);
                            break;
                        case SHIP_MIDDLE:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_middle, i, j, orientation);
                            break;
                        case SHIP_END:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_end, i, j, orientation);
                            break;
                        case SHIP_DESTROYED:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_destroyed, i, j, orientation);
                            break;
                        default:
                            gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_start, i, j, orientation);
                            break;
                    }
                }
                //sets the onClickListener for the tiles of the gridLayout
                setOnClickListenerForGridTiles(gView, i, j);
            }
        }
        currentBoardView = gView;
        return gView;
    }

    /**
     * sets the OnClickListener for the tiles of the GridLayout. is part of the loadBattleArea methode
     *
     * @param gView
     * @param i
     * @param j
     */
    private void setOnClickListenerForGridTiles(final GameBoardImageView[][] gView, int i, int j) {

        //OnClickListener for the GridLayout. Calls the method shotOnEnemy in the GameController
        gView[i][j].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if the game finished
                if (GlobalGameSettings.getCurrent().isGameFinished())
                    return;

                final GameBoardImageView v = (GameBoardImageView) view;
                Log.i("fish", "" + v.getBoardCol() + v.getBoardRow());
                //controlls the returnvalue of controller.shotOnEnemy
                if (!shooting) {
                    shooting = true;
                    controller.shotOnEnemy(actualBattleArea, actualUser, v.getBoardCol(), v.getBoardRow(), new CallbackObject<ReturnObject>() {
                        @Override
                        public void callback(ReturnObject param) {
                            if (param.getI() == 0) {
                                if (param.getTile() == BattleAreaTile.TileType.SHIP_DESTROYED){
                                    currentCheatHit(v.getBoardRow(),v.getBoardCol());
                                }
                                final int drawable = getTheRightTile(param.getTile());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        gView[v.getBoardRow()][v.getBoardCol()].setImageResource(drawable);
                                    }
                                });

                            } else {
                                Toast.makeText(GameTurnsActivity.this, param.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            shooting = false;
                        }
                    });
                }
            }
        });

    }

    /**
     * returns the int Value of the selected Tile
     *
     * @param tile
     * @return
     */
    public int getTheRightTile(BattleAreaTile.TileType tile) {
        int drawable = -1;

        switch (tile) {
            case WATER:
                drawable = R.drawable.water;
                break;
            case NO_HIT:
                drawable = R.drawable.no_hit;
                break;
            case SHIP_START:
                drawable = R.drawable.ship_start;
                break;
            case SHIP_MIDDLE:
                drawable = R.drawable.ship_middle;
                break;
            case SHIP_END:
                drawable = R.drawable.ship_end;
                break;
            case SHIP_DESTROYED:
                drawable = R.drawable.ship_destroyed;
                break;
        }
        return drawable;
    }

    private void currentCheatHit(int row, int col){
        if (currentCheat == null)
            return;
        if (currentCheat.getRowCheating() == row && currentCheat.getColCheating() == col)
            currentCheat = null;
    }

    private void toastOnUi(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GameTurnsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cheating is available after 10 seconds and only while the game is not finished.
     * If cheating was disabled during game setup, its always disabled.
     */
    private boolean cheatingIsAvailable(){
        if (!GlobalGameSettings.getCurrent().isSchummelnEnabled())
            return false;

        if (GlobalGameSettings.getCurrent().isGameFinished())
            return false;

        long timeDiff = System.currentTimeMillis() - initTimeStamp;
        if ( timeDiff > 15000){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (controller != null)
            controller.cleanup();
        super.onBackPressed();
    }
}
