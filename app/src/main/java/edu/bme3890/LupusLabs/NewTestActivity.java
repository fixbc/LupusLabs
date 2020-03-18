package edu.bme3890.LupusLabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class NewTestActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor linearAccelerationSensor;
    TextView sensorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_test);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorTextView = (TextView) findViewById(R.id.sensorTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float acceleration = event.values[0];
        sensorTextView.setText(Float.toString(acceleration));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }
}

