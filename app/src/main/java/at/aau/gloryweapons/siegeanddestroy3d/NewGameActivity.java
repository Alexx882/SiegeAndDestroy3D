package at.aau.gloryweapons.siegeanddestroy3d;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.activities.PlacementActivity;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicator;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.NetworkCommunicatorServer;
import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.UserCallBack;
import at.aau.gloryweapons.siegeanddestroy3d.network.wifiDirect.ServerGameHandlerWifi;
import at.aau.gloryweapons.siegeanddestroy3d.validation.ValidationHelperClass;

public class NewGameActivity extends AppCompatActivity {

    private Button _buttonHostGame;
    private EditText _editTextPlayerName;
    private EditText _editText;
    private ListView userListView;
    private List<String> usersList;
    private ArrayAdapter<String> adapter;

    private NetworkCommunicatorServer serverGameHandlerWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        //init Server
        //ServerGameHandler.getInstance().initSimpleSocketServer();
        this.serverGameHandlerWifi = ServerGameHandlerWifi.getInstance();
        serverGameHandlerWifi.initServerGameHandler(this, new UserCallBack() {
            @Override
            public void callback(List<String> users) {
                listViewUpdater(users);
            }
        });


        loadUiElements();

        final WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        final String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

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
                if (!ValidationHelperClass.validShots(shot != null ? shot.toString() : null)) {
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
        userListView = findViewById(R.id.listViewUser);

        initAdapter();
    }

    private void showLongToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void initAdapter(){
        usersList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,usersList);

        userListView.setAdapter(adapter);
    }

    private void listViewUpdater(List<String> usersList){
        this.usersList.clear();
        this.usersList.addAll(usersList);
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (serverGameHandlerWifi != null){
           serverGameHandlerWifi.resetNetwork();
        }
        super.onBackPressed();
    }
}