package at.aau.gloryweapons.siegeanddestroy3d;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.Inet4Address;

public class JoinGameActivity extends AppCompatActivity {

    private Button _btnJoinGame;
    private EditText _txtUserName;
    private EditText _txtServerIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        loadUiElements();

        _btnJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Editable eUsername = _txtUserName.getText();
                if (!ValidationHelperClass.isUserNameValid(eUsername != null ? eUsername.toString() : null)) {
                    showLongToast("Username ist ungültig");
                    return;
                }

                Editable eServerIp = _txtServerIp.getText();
                if (!ValidationHelperClass.isServerIpValid(eServerIp != null ? eServerIp.toString() : null)) {
                    showLongToast("Server IP ist ungültig");
                    return;
                }

                // everything is ok.

                // TODO: connect to IP
            }
        });
    }

    /**
     * Loads all UI Elements.
     */
    private void loadUiElements() {
        _btnJoinGame = findViewById(R.id.buttonJoinGame);
        _txtUserName = findViewById(R.id.editTextUserName);
        _txtServerIp = findViewById(R.id.editTextServerIp);
    }


    private void showLongToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
