package at.aau.gloryweapons.siegeanddestroy3d;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import at.aau.gloryweapons.siegeanddestroy3d.game.activities.PlacementActivity;
import at.aau.gloryweapons.siegeanddestroy3d.validation.ValidationHelperClass;

public class NewGameActivity extends AppCompatActivity {

    private Button _buttonHostGame;
    private EditText _editTextPlayerName;
    private EditText _editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        loadUiElements();

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        TextView t = new TextView(this);

        t = (TextView) findViewById(R.id.textViewIpHost);
        t.setText(ip);


        _buttonHostGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Editable name = _editTextPlayerName.getText();
                if (!ValidationHelperClass.isUserNameValid(name != null ? name.toString() : null)) {
                    showLongToast("Bitte geben Sie einen Usernamen ein");
                    return;
                }

                Editable shot = _editText.getText();
                if (!ValidationHelperClass.validShots(shot != null ? shot.toString(): null )) {
                    showLongToast("Bitte geben Sie eine Zahl ein");
                    return;

                } else {
                    Intent intent = new Intent(NewGameActivity.this, PlacementActivity.class);
                    startActivity(intent);
                }

            }

        });

    }
    private void loadUiElements() {
        _buttonHostGame = findViewById(R.id.buttonHostGame);
        _editTextPlayerName = findViewById(R.id.editTextPlayerName);
        _editText = findViewById(R.id.editText);
    }

    private void showLongToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}