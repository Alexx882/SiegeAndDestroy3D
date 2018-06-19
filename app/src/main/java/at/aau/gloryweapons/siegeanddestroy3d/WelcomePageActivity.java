package at.aau.gloryweapons.siegeanddestroy3d;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;


import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.activities.PlacementActivity;
import at.aau.gloryweapons.siegeanddestroy3d.game.controllers.GlobalPlayer;
import at.aau.gloryweapons.siegeanddestroy3d.sensors.AccelerometerSensorActivity;
import at.aau.gloryweapons.siegeanddestroy3d.sensors.ProximitySensorActivity;

public class WelcomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        Button spielStarten = (Button) findViewById(R.id.Starten);

        spielStarten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePageActivity.this, NewGameActivity.class));
            }
        });

        Button spielBeitreten = (Button) findViewById(R.id.Beitreten);

        spielBeitreten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePageActivity.this, JoinGameActivity.class));
            }
        });

        Button proximityTest = (Button) findViewById(R.id.proximityTest);

        proximityTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePageActivity.this, ProximitySensorActivity.class));
            }
        });

        Button accelerometerTest = (Button) findViewById(R.id.accelerometerTest);

        accelerometerTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePageActivity.this, AccelerometerSensorActivity.class));
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
               // GlobalPlayer.StopPlayer();
                Toast.makeText(WelcomePageActivity.this, "YOU LEFT YOUR APP. MUSIC STOP", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
       // GlobalPlayer.StartPlayer();
    }
}
