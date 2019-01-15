package org.abondar.experimental.messagingclient.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.abondar.experimental.messagingclient.R;
import org.abondar.experimental.messagingclient.data.MotionData;

public class MotionActivity extends AppCompatActivity implements SensorEventListener {


    private String deviceId;

    private TextView pitchView;
    private TextView rollView;
    private TextView yawView;

    private SensorManager sm;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motion_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceId = getIntent().getStringExtra("deviceId");

        pitchView = this.findViewById(R.id.motion_pitch_val);
        rollView = this.findViewById(R.id.motion_roll_val);
        yawView = this.findViewById(R.id.motion_yaw_val);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);


    }

    @Override
    public void onResume() {
        super.onResume();
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {

            float[] vals = event.values.clone();

            float pitch = vals[0];
            float roll = vals[1];
            float yaw = vals[2];

            MotionData motionData = new MotionData(pitch, roll, yaw);
            motionData.setDeviceId(deviceId);

            pitchView.setText(String.format("%.2f",pitch));
            rollView.setText(String.format("%.2f",roll));
            yawView.setText(String.format("%.2f",yaw));
            sendToTopic(motionData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void sendToTopic(MotionData motionData) {

    }

    ;
}
