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
        //final GameSettings gameSettings = (GameConfiguration) getIntent().getSerializableExtra(GameConfiguration.INTENT_KEYWORD);
        gameSettings = new GameConfiguration(true);
        board = new BoardRenderer(this);
        controller = new GameController();

        int nRows = 9, nCols = 9;
        //todo is needed but not working at the moment....no serverShit available
  /*      for(User u :gameSettings.getUserList())
        {
            if(u.getId()==gameSettings.getMyId())
            {
                actualUser=u;
            }
        }
*/
        //  visualBoard=initBoard(nRows, nCols);
        BattleArea area = new BattleArea(1, 9, 9);
        visualBoard = loadBattleArea(area, nRows, nCols);

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
            // set the ontouchlistener; methode loads the field of the user
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.i("fish", "" + view.getId());
                    actualUser = gameSettings.getUserByIndex(view.getId());
                    // methodcall in controller like: changeGridToBattleAreaUser(int id) ...id of the view because the id of the view is the same as the position in the userList (ArrayList in GameSettings)
                    loadArea(view.getId());
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

    private void loadArea(int id) {
        User u = gameSettings.getUserByIndex(id);
                   /* BattleArea a;
                    for (BattleArea area : serverListOfAreas) {
                        if(area.getUserId() == u.getId()) {

                            printArea(area);
                            break;
                        }
                    }*/
    }

    /*   private GameBoardImageView[][] initBoard(int nRows, int nCols)
       {
           GridLayout grid = findViewById(R.id.gridEnemyTurnBoard);
           grid.setRowCount(nRows);
           grid.setColumnCount(nCols);

           GameBoardImageView[][] gView = null;

           for (int i = 0; i < nRows; ++i) {
               for (int j = 0; j < nCols; ++j) {
                   gView[i][j] = gridRenderer.addImageToGrid(grid, R.drawable.water, i, j, 0);
                   gView[i][j].setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           GameBoardImageView v = (GameBoardImageView) view;
                           Log.i("fish", "" + v.getBoardCol() + v.getBoardRow());
                           //shotOnEnemy
                           controller.shotOnEnemy(gameSettings, actualUser, v.getBoardCol(), v.getBoardRow());
                       }
                   });
               }
           }
           return gView;
       }*/
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
                }

                gView[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GameBoardImageView v = (GameBoardImageView) view;
                        Log.i("fish", "" + v.getBoardCol() + v.getBoardRow());
                        //shotOnEnemy
                        // todo actualUser = gameSettings.getUserByIndex(v.getId());
                        int drawable = getTheRightTile(controller.shotOnEnemy(gameSettings, actualUser, v.getBoardCol(), v.getBoardRow()));

                        gView[v.getBoardRow()][v.getBoardCol()].setImageResource(drawable);
                    }
                });
            }
        }
        return gView;
    }

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
                drawable = R.drawable.ship_destroyed;
                break;
        }
        return drawable;
    }
}
