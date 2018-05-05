package at.aau.gloryweapons.siegeanddestroy3d.game.activities;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import at.aau.gloryweapons.siegeanddestroy3d.R;

import at.aau.gloryweapons.siegeanddestroy3d.game.activities.renderer.BoardRenderer;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.game.views.GameBoardImageView;

public class GameTurnsActivity extends AppCompatActivity {
    ImageView iv = null;
    GameSettings gameSettings = null;
    BoardRenderer board = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enemy_turn);

       // receiving and saving the game configuration
        GameSettings gameSettings = (GameConfiguration)getIntent().getSerializableExtra(GameConfiguration.INTENT_KEYWORD);
        board = new BoardRenderer(this);
      
        int nRows = 9, nCols = 9;
        // set size for grid
        GridLayout grid = findViewById(R.id.gridEnemyTurnBoard);
        grid.setRowCount(nRows);
        grid.setColumnCount(nCols);

        GameBoardImageView gView = null;


        for (int i = 0; i < nRows; ++i)
            for (int j = 0; j < nCols; ++j) {
                gView = createImageViewForGrid(grid, R.drawable.water, i, j);
                gView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GameBoardImageView v = (GameBoardImageView) view;
                        Log.i("fish", "" + v.getBoardCol() + v.getBoardRow());
                    }
                });

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
            // set the ontouchlistener; methode loads the field of the user
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.i("fish", "" + view.getId());
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


}
