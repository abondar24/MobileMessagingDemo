package org.abondar.experimental.messagingclient.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.abondar.experimental.messagingclient.R;
import org.abondar.experimental.messagingclient.data.MotionData;
import org.abondar.experimental.messagingclient.util.ConnectionUtil;
import org.apache.commons.io.IOUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class MotionActivity extends AppCompatActivity implements SensorEventListener {


    private String deviceId;

    private TextView pitchView;
    private TextView rollView;
    private TextView yawView;

    private SensorManager sm;
    private Sensor sensor;

    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motion_activity);
        Toolbar toolbar = findViewById(R.id.motion_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(view -> {
            Intent backIntent = new Intent(MotionActivity.this, MainActivity.class);
            startActivity(backIntent);

        });

        deviceId = getIntent().getStringExtra("deviceId");

        pitchView = this.findViewById(R.id.motion_pitch_val);
        rollView = this.findViewById(R.id.motion_roll_val);
        yawView = this.findViewById(R.id.motion_yaw_val);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        try {
            mqttClient = new MqttClient(ConnectionUtil.MQTT_URI +ConnectionUtil.MQTT_PORT,
                    ConnectionUtil.MQTT_CLIENT_ID,new MemoryPersistence());
        } catch (MqttException ex){
            Log.e(ACTIVITY_SERVICE,"Exception while creating mqtt client: "+ex.getMessage());
        }

        mqttClient.setCallback(initCallback());
        connectToBroker();


    }

    @Override
    public void onResume() {
        super.onResume();
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        connectToBroker();
    }

    @Override
    public void onPause() {
        sm.unregisterListener(this);
        disconnectFromBroker();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        sm.unregisterListener(this);
        disconnectFromBroker();
        super.onDestroy();
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
          byte[] content = convertDataToBytes(motionData);
          MqttMessage msg = new MqttMessage(content);
          msg.setQos(ConnectionUtil.MQTT_QOS);
          try{
              mqttClient.publish(ConnectionUtil.MQTT_TOPIC,msg);
              Log.i(ACTIVITY_SERVICE, "Sent to broker "+motionData);

          } catch (MqttException ex){
              Log.e(ACTIVITY_SERVICE,"Exception while publishing to broker: "+ex.getMessage());
          }

    }

    private byte[] convertDataToBytes(MotionData motionData) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json=mapper.writeValueAsString(motionData);
        } catch (JsonProcessingException ex) {
            Log.e(ACTIVITY_SERVICE, "JSON exception: "+ex.getMessage());
        }

        return json.getBytes();
    }

    private MqttCallback initCallback(){
        return new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
               Log.i(ACTIVITY_SERVICE,"Got message: "+message.toString()+ "from topic: "+topic);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };
    }

    private void connectToBroker(){
        if (mqttClient==null){
            return;
        }

        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName(ConnectionUtil.BROKER_USER);
        opts.setPassword(ConnectionUtil.BROKER_PASSWORD.toCharArray());
        opts.setCleanSession(false);
        opts.setKeepAliveInterval(5);
        opts.setAutomaticReconnect(false);

        try {
            mqttClient.connect(opts);
            mqttClient.subscribe(ConnectionUtil.MQTT_ALERT);
        } catch (MqttException ex){
            Log.e(ACTIVITY_SERVICE,"Exception while connecting to broker: "+ex.getMessage());
        }
    }

    private void disconnectFromBroker(){
        try {
            mqttClient.disconnect();
        } catch (MqttException ex){
            Log.e(ACTIVITY_SERVICE,"Exception while disconnecting from broker: "+ex.getMessage());
        }
    }

}
