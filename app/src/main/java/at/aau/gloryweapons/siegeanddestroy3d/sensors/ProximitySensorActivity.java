package at.aau.gloryweapons.siegeanddestroy3d.sensors;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import at.aau.gloryweapons.siegeanddestroy3d.R;

public class ProximitySensorActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager cheatSensorManager;
    private Sensor cheatSensor;
    private static final int SENSOR_SENSITIVITY = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cheatSensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        cheatSensor = cheatSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


        if (cheatSensor == null){
            showLongToast("Not available - please try again");
            return;
        }
    }
    @Override
    protected void onResume (){
        super.onResume();
        cheatSensorManager.registerListener((SensorEventListener) this, cheatSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause(){
        super.onPause();
        cheatSensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged (SensorEvent event) {

    }

    private void showLongToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
