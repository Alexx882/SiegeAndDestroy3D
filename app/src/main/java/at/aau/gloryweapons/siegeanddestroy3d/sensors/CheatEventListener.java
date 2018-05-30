package at.aau.gloryweapons.siegeanddestroy3d.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;

public class CheatEventListener implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor proximitySensor;

    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    CallbackObject<Boolean> callback;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (proximityActivated(sensorEvent, 4) || accelerometerActivated(sensorEvent, 3)) {
            callback.callback(true);
        }
        callback.callback(false);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public CheatEventListener(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager == null) {
            throw new IllegalStateException("Sensor Manager is null");
        }

        //Sensoren werden initialisiert
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (accelerometerSensor == null) {
            throw new IllegalStateException("Accelerometer Sensor is null");
        }

        if (proximitySensor == null) {
            throw new IllegalStateException("Proximity Sensor is null");
        }
        registerSensors();
    }

    public void registerSensors() {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensors() {
        sensorManager.unregisterListener(this);
        sensorManager.unregisterListener(this);
    }

    private boolean proximityActivated(SensorEvent event, int sensitivity) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -sensitivity && event.values[0] <= sensitivity) {
                return true;
            }
        }
        return false;
    }

    private boolean accelerometerActivated(SensorEvent event, int sensitivity) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // reagiert bei schütteln
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > sensitivity) {
                return true;
            }
        }
        return false;
    }

    public void registerForChanges(CallbackObject<Boolean> callback) {
        this.callback = callback;
    }

}
