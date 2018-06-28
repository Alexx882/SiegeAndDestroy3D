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

import at.aau.gloryweapons.siegeanddestroy3d.game.activities.PlacementActivity;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ClientGameHandlerKryoNet;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorClient;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.validation.ValidationHelperClass;

public class JoinGameActivity extends AppCompatActivity {

    private Button btnJoinGame;
    private EditText txtUserName;
    private TextView txtServer;
    private TextView txtError;
    private EditText txtIp;

    private NetworkCommunicatorClient clientCommunicator;
    private boolean connectedToServer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        loadUiElements();

        GlobalGameSettings.getCurrent().setGameFinished(false);

        btnJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // new click, disable error information
                txtError.setVisibility(View.GONE);
                // disable button
                btnJoinGame.setEnabled(false);

                if (!connectedToServer) {
                    btnJoinGame.setText("Verbinden...");
                    // init server connection
                    connectToServer();

                } else {
                    // basic check for username on client side
                    Editable eUsername = txtUserName.getText();
                    String username = eUsername != null ? eUsername.toString() : null;
                    if (!ValidationHelperClass.isUserNameValid(username)) {
                        showError("Username ist ungültig.");
                        btnJoinGame.setEnabled(true);
                        return;
                    }

                    // check name on server too
                    clientCommunicator.sendNameToServer(username, new CallbackObject<User>() {
                        @Override
                        public void callback(User param) {
                            // server sent back response for name
                            if (param != null) {
                                // name ok
                                // save name and things
                                GlobalGameSettings.getCurrent().setLocalUser(param);

                                // move on to placement
                                startPlacementActivity();

                            } else {
                                // name invalid
                                showError("Username nicht verfügbar!");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnJoinGame.setEnabled(true);
                                    }
                                });

                            }
                        }
                    });
                }
            }

        });
    }

    /**
     * Connects to a server and re-enables the button for the name check.
     */
    private void connectToServer() {
        if (connectedToServer)
            throw new IllegalStateException("Please dont try to connect twice");

        // init with singleton
        this.clientCommunicator = ClientGameHandlerKryoNet.getInstance();

        String ip = txtIp.getText().toString();

        try {
            // init connection
            this.clientCommunicator.initClientGameHandler(ip, this, new CallbackObject<HandshakeDTO>() {
                @Override
                public void callback(final HandshakeDTO param) {
                    // connecting finished
                    JoinGameActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // re-enable button
                            btnJoinGame.setEnabled(true);
                            btnJoinGame.setText("Name überprüfen");

                            // show server name
                            txtServer.setVisibility(View.VISIBLE);
                            txtServer.setText("Verbindung zum Server hergestellt!");

                            connectedToServer = true;
                        }
                    });
                }
            });
        } catch (Exception e) {
            // TODO @johannes return error if something bad happened (ie. some additional callback etc)

            // show and log error
            Log.e("Error", "connection could not be established." + e.getMessage());
            showError("Verbindung fehlgeschlagen");

            // enable button again
            btnJoinGame.setEnabled(true);
            btnJoinGame.setText("Erneut versuchen");
        }
    }

    private void showError(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtError.setText(message);
                txtError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startPlacementActivity() {
        // start placement activity and provide the game settings
        Intent switchActivityIntent = new Intent(this, PlacementActivity.class);
        startActivity(switchActivityIntent);

        // remove this activity from the stack (back means WelcomePage)
        this.finish();
    }

    /**
     * Loads all UI Elements.
     */
    private void loadUiElements() {
        btnJoinGame = findViewById(R.id.buttonJoinGame);
        txtUserName = findViewById(R.id.editTextUserName);
        txtServer = findViewById(R.id.textViewServer);
        txtError = findViewById(R.id.textViewError);

        txtIp = findViewById(R.id.editTextHostIpAddress);

        // disable info on start
        txtServer.setVisibility(View.GONE);
        txtError.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (clientCommunicator != null)
            clientCommunicator.resetNetwork();
        super.onBackPressed();
    }
}
