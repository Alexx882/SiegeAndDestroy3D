package at.aau.gloryweapons.siegeanddestroy3d;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect.WifiDirectHelper;


public class WelcomePageActivity extends AppCompatActivity {

    Button SpielStarten;
    Button SpielBeitreten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        Button SpielStarten = (Button) findViewById(R.id.Starten);

        //reset wifi direct
        WifiDirectHelper.getInstance().resetWifiDirect();

        SpielStarten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePageActivity.this, NewGameActivity.class));
            }
        });

        Button SpielBeitreten = (Button) findViewById(R.id.Beitreten);

        SpielBeitreten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomePageActivity.this, JoinGameActivity.class));
            }
        });
    }
}
