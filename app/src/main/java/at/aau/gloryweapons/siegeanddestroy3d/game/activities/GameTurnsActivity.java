package at.aau.gloryweapons.siegeanddestroy3d.game.activities;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.R;

import at.aau.gloryweapons.siegeanddestroy3d.game.GameController;
import at.aau.gloryweapons.siegeanddestroy3d.game.activities.renderer.BoardRenderer;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.game.views.GameBoardImageView;

public class GameTurnsActivity extends AppCompatActivity {
    ImageView iv = null;
    GameConfiguration gameSettings = null;
    GameController controller = null;
    private BoardRenderer board = null;
    User actualUser = null;
    private GameBoardImageView[][] visualBoard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enemy_turn);


        // receiving and saving the game configuration
        gameSettings = new GameConfiguration(true);
        board = new BoardRenderer(this);
        controller = new GameController();

        final int nRows = 9, nCols = 9;
        //todo is needed but not working at the moment....no serverShit available
        for (User u : gameSettings.getUserList()) {
            if (u.getId() == GlobalGameSettings.getCurrent().getPlayerId()) {
                actualUser = u;
            }
        }

        //gets the BattleArea of the Client.
        for (BattleArea area : gameSettings.getBattleAreaList()) {
            if (area.getUserId() == actualUser.getId()) {
                //  visualBoard=initBoard(nRows, nCols);
                visualBoard = loadBattleArea(area, nRows, nCols);
            }
        }


        Button button = findViewById(R.id.buttonUpadteWater);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv.setImageResource(R.drawable.ship_end);
            }
        });

        //adding playerLabels to ConstraintLayout.
        ConstraintLayout userLayout = findViewById(R.id.UserLayout);

        for (int i = 0; i < gameSettings.getUserList().size(); i++) {
            //create textview + setText with Username + ontouchlistener
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
                        }
                    }

                    return false;
                }
            });
            //set params for view
            ConstraintLayout.LayoutParams viewParam = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

            userLayout.addView(v, viewParam);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(userLayout);

            int marginTop = 8 + i * 95;
            constraintSet.connect(v.getId(), ConstraintSet.RIGHT, userLayout.getId(), ConstraintSet.RIGHT, 250);
            constraintSet.connect(v.getId(), ConstraintSet.TOP, userLayout.getId(), ConstraintSet.TOP, marginTop);

            constraintSet.applyTo(userLayout);
        }

    }

    private GameBoardImageView createImageViewForGrid(GridLayout grid, int imageResource, int row, int col) {
        GameBoardImageView view = null;

        view = board.addImageToGrid(grid, imageResource, row, col, 0);

        return view;
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
        BattleAreaTile tiles[][] = area.getBattleAreaTiles();

        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nCols; ++j) {
                //gView[i][j].setOnClickListener(null);
                switch (tiles[i][j].getType()) {
                    case WATER:
                        gView[i][j] = board.addImageToGrid(grid, R.drawable.water, i, j, 0);
                        break;
                    case NO_HIT:
                        gView[i][j] = board.addImageToGrid(grid, R.drawable.no_hit, i, j, 0);
                        break;
                    case SHIP_START:
                        gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_start, i, j, 0);
                        break;
                    case SHIP_MIDDLE:
                        gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_middle, i, j, 0);
                        break;
                    case SHIP_END:
                        gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_end, i, j, 0);
                        break;
                    case SHIP_DESTROYED:
                        gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_destroyed, i, j, 0);
                        break;
                    default:
                        gView[i][j] = board.addImageToGrid(grid, R.drawable.ship_start, i, j, 90);
                        break;
                }

                //OnClickListener for the GridLayout. Calls the method shotOnEnemy in the GameController
                gView[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GameBoardImageView v = (GameBoardImageView) view;
                        Log.i("fish", "" + v.getBoardCol() + v.getBoardRow());
                        //controlls the returnvalue of controller.shotOnEnemy
                        if (controller.shotOnEnemy(gameSettings, actualUser, v.getBoardCol(), v.getBoardRow()) == null) {
                            Toast.makeText(GameTurnsActivity.this, "Nice try", Toast.LENGTH_SHORT).show();
                        } else {
                            int drawable = getTheRightTile(controller.shotOnEnemy(gameSettings, actualUser, v.getBoardCol(), v.getBoardRow()));
                            gView[v.getBoardRow()][v.getBoardCol()].setImageResource(drawable);
                        }
                    }
                });
            }
        }
        return gView;
    }

    /**
     * returns the int Value of the selected Tile
     *
     * @param tile
     * @return
     */
    private int getTheRightTile(BattleAreaTile.TileType tile) {
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
                drawable = R.drawable.ship_start;
                break;
        }
        return drawable;
    }
}
