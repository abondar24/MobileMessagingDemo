package org.abondar.experimental.messagingclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.abondar.experimental.messagingclient.data.LocationData;
import org.abondar.experimental.messagingclient.util.ConnectionUtil;
import org.abondar.experimental.messagingclient.util.PermissionCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

import static org.abondar.experimental.messagingclient.util.ConnectionUtil.STOMP_ENDPOINT;
import static org.abondar.experimental.messagingclient.util.ConnectionUtil.STOMP_PORT;
import static org.abondar.experimental.messagingclient.util.ConnectionUtil.STOMP_URI;

public class LocationTrackerActivity extends AppCompatActivity implements LocationListener {
    private TextView latView;
    private TextView lonView;
    private TextView altView;
    private TextView idView;
    private String deviceId;
    private LocationManager lm;
    private StompClient stompClient;
    private ObjectMapper mapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestPermissions();
        idView = this.findViewById(R.id.id_text);

        latView = this.findViewById(R.id.location_lat_val);
        lonView = this.findViewById(R.id.location_lon_val);
        altView = this.findViewById(R.id.location_alt_val);

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            fillDeviceId();
        }

        enableLocation();

        mapper = new ObjectMapper();
        stompClient = Stomp.over(Stomp.ConnectionProvider.JWS,
                STOMP_URI + STOMP_PORT + STOMP_ENDPOINT);
        stompClient.connect();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        PermissionCodes code = PermissionCodes.byCode(requestCode);
        switch (code) {
            case LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fillDeviceId();
                    enableLocation();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void requestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION};

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = checkSelfPermission(p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PermissionCodes.LOCATION.getCode());
        }


    }

    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        try {
            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getImei();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            return deviceUuid.toString();
        } catch (SecurityException ex) {
            Log.e(ACTIVITY_SERVICE, "getDeviceId: ", ex);
        }
        return "id is not available";
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
    public void onDestroy() {
        stompClient.disconnect();
        super.onDestroy();
    }

    private void fillDeviceId(){
        deviceId = getDeviceId();
        idView.append(deviceId);
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
