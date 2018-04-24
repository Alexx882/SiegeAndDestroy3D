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
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameSettings;

public class GameTurnsActivity extends AppCompatActivity {
    ImageView iv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enemy_turn);
        //initializing gameSettings class. normally not in this activity...only for testing
        GameSettings gameSettings = new GameSettings();

        int nRows = 9, nCols = 9;
        // set size for grid
        GridLayout grid = findViewById(R.id.gridEnemyTurnBoard);
        grid.setRowCount(nRows);
        grid.setColumnCount(nCols);



        for(int i = 0; i < nRows; ++i)
            for(int j = 0; j < nCols; ++j)
                grid.addView(iv = createImageViewForGrid(R.drawable.water, i, j));


        Button button = findViewById(R.id.buttonUpadteWater);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv.setImageResource(R.drawable.ship_end);
            }
        });

        //adding playerLabels to ConstraintLayout.
        ConstraintLayout userLayout = findViewById(R.id.UserLayout);

        for(int i = 0; i< gameSettings.getUserList().size(); i++)
        {
            //create textview + setText with Username + ontouchlistener
            TextView v = new TextView (this);
            v.setText(gameSettings.getUserByIndex(i).getName());
            v.setTextSize(25);
            v.setId(i);
            // set the ontouchlistener; methode loads the field of the user
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.i("fish",""+view.getId());
                    // methodcall in controller like: changeGridToBattleAreaUser(int id) ...id of the view because the id of the view is the same as the position in the userList (ArrayList in GameSettings)
                    return false;
                }
            });
            //set params for view
            ConstraintLayout.LayoutParams viewParam = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

            userLayout.addView(v,viewParam);

           ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(userLayout);

            int marginTop = 8+i*95;
            constraintSet.connect(v.getId(), ConstraintSet.RIGHT, userLayout.getId(), ConstraintSet.RIGHT,250);
            constraintSet.connect(v.getId(), ConstraintSet.TOP, userLayout.getId(), ConstraintSet.TOP,  marginTop);

            constraintSet.applyTo(userLayout);
        }

    }

    private ImageView createImageViewForGrid(int imageResource, int row, int col){
        // create imageview
        ImageView view =  new ImageView(this);
        view.setImageResource(imageResource);

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

        return view;
    }


}
