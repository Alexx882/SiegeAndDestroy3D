package at.aau.gloryweapons.siegeanddestroy3d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.peak.salut.SalutDevice;

import at.aau.gloryweapons.siegeanddestroy3d.game.activities.PlacementActivity;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect.ClientGameHandlerWifi;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.validation.ValidationHelperClass;

public class JoinGameActivity extends AppCompatActivity {

    private Button btnJoinGame;
    private EditText txtUserName;
    private EditText txtServerIp;
    private TextView txtServer;
    private TextView txtServerIPHeader;
    private SalutDevice salutDevice;
    private ClientGameHandlerWifi clientGameHandlerWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        loadUiElements();

        //init wifi direct
        clientGameHandlerWifi = ClientGameHandlerWifi.getInstance();
        clientGameHandlerWifi.initClientGameHandler(this, new CallbackObject<SalutDevice>() {
            @Override
            public void callback(SalutDevice param) {
                salutDevice = param;
                txtServer.setText("Server: " + param.deviceName + " " + param.readableName);
                btnJoinGame.setText("Spiel " + param.deviceName + " beitreten");
            }
        });


        btnJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Editable eUsername = txtUserName.getText();
                String username = eUsername != null ? eUsername.toString() : null;
                if (!ValidationHelperClass.isUserNameValid(username)) {
                    showLongToast("Username ist ungültig");
                    return;
                }

                /*no use with wifi direct
                Editable eServerIp = txtServerIp.getText();
                String hostIp = eServerIp != null ? eServerIp.toString() : null;
                if (!ValidationHelperClass.isServerIpValid(hostIp)) {
                    showLongToast("Server IP ist ungültig");
                    return;
                } */

                // disable button now
                Button btn = (Button) view;
                // TODO show user that the app is still working ("connecting to server" or something)
                btn.setClickable(false);

                // connect to server and check name
                //connectToServer(hostIp, username);
                connectToServer(null, username);


            }

        });

    }

    private void connectToServer(String hostIp, String username) {
        // send name to server and hope that it isn't taken
        //NetworkCommunicator comm = new DummyNetworkCommunicator();

        //register to server
        clientGameHandlerWifi.registerToHost(salutDevice, username);
        clientGameHandlerWifi.sendNameToServer(new User(0, null, username), new CallbackObject<User>() {
            @Override
            public void callback(User param) {
                if (param == null) {
                    showLongToast("Name ist bereits vergeben.");
                    btnJoinGame.setClickable(true);
                    return;
                }
            }
        });

        // TODO save user object and host IP
        Intent intent = new Intent(JoinGameActivity.this, PlacementActivity.class);
        startActivity(intent);

    }

    /**
     * Loads all UI Elements.
     */
    private void loadUiElements() {
        btnJoinGame = findViewById(R.id.buttonJoinGame);
        txtUserName = findViewById(R.id.editTextUserName);
        txtServerIp = findViewById(R.id.editTextServerIp);
        txtServer = findViewById(R.id.textView_show_server);
        txtServerIPHeader = findViewById(R.id.textView_server_ip_header);

        //no use with wifi direct
        txtServerIp.setVisibility(View.INVISIBLE);
        txtServerIPHeader.setVisibility(View.INVISIBLE);
    }

    private void showLongToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
