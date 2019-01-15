package org.abondar.experimental.messagingclient.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.abondar.experimental.messagingclient.R;
import org.abondar.experimental.messagingclient.data.LocationData;
import org.abondar.experimental.messagingclient.util.ConnectionUtil;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

import static org.abondar.experimental.messagingclient.util.ConnectionUtil.ENDPOINT;
import static org.abondar.experimental.messagingclient.util.ConnectionUtil.STOMP_PORT;
import static org.abondar.experimental.messagingclient.util.ConnectionUtil.URI;

public class LocationActivity extends AppCompatActivity implements LocationListener {
    private TextView latView;
    private TextView lonView;
    private TextView altView;
    private LocationManager lm;
    private String deviceId;
    private StompClient stompClient;
    private ObjectMapper mapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        latView = this.findViewById(R.id.location_lat_val);
        lonView = this.findViewById(R.id.location_lon_val);
        altView = this.findViewById(R.id.location_alt_val);

        deviceId = getIntent().getStringExtra("deviceId");

        enableLocation();

        mapper = new ObjectMapper();
        stompClient = Stomp.over(Stomp.ConnectionProvider.JWS,
                URI + STOMP_PORT + ENDPOINT);
        stompClient.connect();


    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(ACTIVITY_SERVICE, location.toString());
        String lat = convertToString(location.getLatitude());
        String lon = convertToString(location.getLongitude());
        String alt = convertToString(location.getAltitude());

        LocationData locationData = new LocationData(lat, lon, alt);
        locationData.setDeviceId(deviceId);

        sendToQueue(locationData);

        latView.setText(locationData.getLatitude());
        lonView.setText(locationData.getLongitude());
        altView.setText(locationData.getAltitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }



    private String convertToString(double val) {
        return String.valueOf(Math.round(val * 1000000.0) / 1000000.0);
    }

    private void sendToQueue(LocationData locationData) {
        if (locationData == null) {
            Log.e(ACTIVITY_SERVICE, "Nothing to send");
            return;

        }

        String json = "";

        try {
            json = mapper.writeValueAsString(locationData);
        } catch (JsonProcessingException ex) {
            Log.e(ACTIVITY_SERVICE, "Empty location data");
        }

        stompClient.send(ConnectionUtil.STOMP_TOPIC, json).subscribe(() ->
                Log.i(ACTIVITY_SERVICE, "Sent to broker "+locationData));


    }

    @Override
    public void onPause(){
        stompClient.disconnect();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        stompClient.disconnect();
        super.onDestroy();
    }


    private void enableLocation(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled =lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }

            if (isGPSEnabled){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }


        }

    }
}
