package at.aau.gloryweapons.siegeanddestroy3d.gameActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import at.aau.gloryweapons.siegeanddestroy3d.R;

public class GameTurnsActivity extends AppCompatActivity {
    ImageView iv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enemy_turn);

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
