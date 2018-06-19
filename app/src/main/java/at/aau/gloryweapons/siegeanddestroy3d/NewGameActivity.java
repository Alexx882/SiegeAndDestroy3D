package at.aau.gloryweapons.siegeanddestroy3d;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.activities.PlacementActivity;
import at.aau.gloryweapons.siegeanddestroy3d.game.controllers.GlobalPlayer;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;
import at.aau.gloryweapons.siegeanddestroy3d.network.kryonet.ServerGameHandlerKryoNet;
import at.aau.gloryweapons.siegeanddestroy3d.validation.ValidationHelperClass;

public class NewGameActivity extends AppCompatActivity {

    private boolean appliedSettings = false;

    private Button buttonHostGame;
    private EditText editTextPlayerName;
    private EditText editTextNumberShots;
    private ListView userListView;
    private Switch switchSchummeln;

    private List<String> usersList;
    private ArrayAdapter<String> adapter;

    private ServerGameHandlerKryoNet serverGameHandlerAsyncComm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // start server
        serverGameHandlerAsyncComm = ServerGameHandlerKryoNet.getInstance();
        serverGameHandlerAsyncComm.initServerGameHandler(this, new CallbackObject<List<String>>() {
            @Override
            public void callback(List<String> param) {
                listViewUpdater(param);
            }
        });

        loadUiElements();

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        // todo deprecated
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        TextView t = findViewById(R.id.textViewIpHost);
        t.setText(ip);

        buttonHostGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!appliedSettings)
                    applySettings();

                else {
                    // start placement for server

                    GlobalGameSettings.getCurrent()
                            // connected clients + this host
                            .setNumberPlayers(serverGameHandlerAsyncComm.getNumberOfConnectedPlayers() + 1);
                    // todo disable new joins.

                    Intent intent = new Intent(NewGameActivity.this, PlacementActivity.class);
                    startActivity(intent);

                    // close this activity
                    NewGameActivity.this.finish();
                }
            }
        });
    }

    /**
     * Applies the input from the user. Including the Username check.
     */
    private void applySettings() {
        // check user input
        Editable name = editTextPlayerName.getText();
        if (!ValidationHelperClass.isUserNameValid(name != null ? name.toString() : null)) {
            showLongToast("Bitte geben Sie einen Usernamen ein.");
            return;
        }

        final Editable shot = editTextNumberShots.getText();
        if (!ValidationHelperClass.validShots(shot != null ? shot.toString() : null)) {
            showLongToast("Bitte geben Sie eine Zahl ein.");
            return;
        }

        // name check for server
        serverGameHandlerAsyncComm.sendNameToServer(name.toString(), new CallbackObject<User>() {
            @Override
            public void callback(User param) {
                if (param == null) {
                    // name is not valid
                    Toast.makeText(NewGameActivity.this, "Username nicht verfügbar!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // apply input
                GlobalGameSettings.getCurrent().setLocalUser(param);
                GlobalGameSettings.getCurrent().setSchummelnEnabled(switchSchummeln.isChecked());
                GlobalGameSettings.getCurrent().setNumberShots(Integer.parseInt(shot.toString()));

                // update the button
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonHostGame.setText("Spiel starten");
                    }
                });

                // disable ui because its activated now.
                editTextPlayerName.setEnabled(false);
                editTextNumberShots.setEnabled(false);
                switchSchummeln.setEnabled(false);

                appliedSettings = true;
            }
        });
    }

    private void loadUiElements() {
        buttonHostGame = findViewById(R.id.buttonHostGame);
        editTextPlayerName = findViewById(R.id.editTextPlayerName);
        editTextNumberShots = findViewById(R.id.editTextNumberShots);
        userListView = findViewById(R.id.listViewUser);
        switchSchummeln = findViewById(R.id.switchSchummeln);

        initAdapter();
    }

    private void showLongToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void initAdapter() {
        usersList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, usersList);

        userListView.setAdapter(adapter);
    }

    private void listViewUpdater(List<String> usersList) {
        this.usersList.clear();
        this.usersList.addAll(usersList);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
              //  StopPlayer();
                Toast.makeText(NewGameActivity.this, "YOU LEFT YOUR APP. MUSIC STOP", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
      //  GlobalPlayer.StartPlayer();
    }

    @Override
    public void onBackPressed() {
      /*  if (serverGameHandlerWifi != null){
           serverGameHandlerWifi.resetNetwork();
        }*/
        super.onBackPressed();
    }
}