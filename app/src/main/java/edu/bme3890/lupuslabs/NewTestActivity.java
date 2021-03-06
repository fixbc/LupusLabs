package edu.bme3890.lupuslabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewTestActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor linearAccelerationSensor;
    TextView sensorTextView;

    public static final String EXTRA_SENSOR_DATA = "edu.bme3890.lupuslabs.EXTRA_SENSOR_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_test);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorTextView = findViewById(R.id.sensorTextView);
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
        sensorTextView.setText(acceleration + " m/s\u00B2");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    public void openEmailActivity(View v) {
        String sensor_data = sensorTextView.getText().toString();
        Intent intent = new Intent(this, EmailActivity.class);
        intent.putExtra(EXTRA_SENSOR_DATA, sensor_data);
        intent.putExtra(Activity.ACTIVITY_SERVICE, this.getClass().getSimpleName());
        startActivity(intent);
    }
}

