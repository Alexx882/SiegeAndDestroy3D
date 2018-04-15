package at.aau.gloryweapons.siegeanddestroy3d;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import at.aau.gloryweapons.siegeanddestroy3d.gameActivities.PlacementActivity;

public class NewGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        TextView t = new TextView(this);

        t = (TextView) findViewById(R.id.textViewIpHost);
        t.setText(ip);

        }

    public void goToPlacementActivity (View view){
        Intent intent = new Intent (NewGameActivity.this, PlacementActivity.class);
        startActivity(intent);
    }
}