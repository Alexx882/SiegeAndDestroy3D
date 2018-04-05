package at.aau.gloryweapons.siegeanddestroy3d.gameActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import at.aau.gloryweapons.siegeanddestroy3d.R;

public class GameTurnsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enemy_turn);

        ImageView v =  new ImageView(this);

        GridLayout layout = findViewById(R.id.gridLayoutBoard);
        layout.setRowCount(8);
        layout.setColumnCount(8);
        // layout.addView();
    }
}
