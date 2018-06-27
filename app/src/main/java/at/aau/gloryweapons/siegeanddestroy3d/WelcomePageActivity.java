package at.aau.gloryweapons.siegeanddestroy3d;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

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

    }
}
