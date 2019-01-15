package org.abondar.experimental.messagingclient.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.abondar.experimental.messagingclient.R;
import org.abondar.experimental.messagingclient.util.PermissionCodes;

import ua.naiksoftware.stomp.client.StompClient;


public class MainActivity extends AppCompatActivity  {


    private TextView idView;
    private String deviceId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestPermissions();


        idView = this.findViewById(R.id.id_text);
        Button locationButton = this.findViewById(R.id.location_button);
        Button motionButton = this.findViewById(R.id.motion_button);

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
          fillDeviceId();
        }


        locationButton.setOnClickListener(v -> {
            Intent locationIntent = new Intent(MainActivity.this,LocationActivity.class);
            locationIntent.putExtra("deviceId",deviceId);
            startActivity(locationIntent);
        });

        motionButton.setOnClickListener(v -> {
            Intent motionIntent = new Intent(MainActivity.this,MotionActivity.class);
            motionIntent.putExtra("deviceId",deviceId);
            startActivity(motionIntent);
        });
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




    private void fillDeviceId(){
        deviceId = getDeviceId();
        idView.append(deviceId);

    }

}
