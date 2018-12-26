package org.abondar.experimental.locationtracker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.Nullable;

public class LocationSyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static LocationSyncAdapter locationSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.i("LocationSyncService", "onCreate");
        synchronized (syncAdapterLock) {
            if (locationSyncAdapter== null) {
                locationSyncAdapter = new LocationSyncAdapter(getApplicationContext(), true);
            }

        }
    }


    public static LocationSyncAdapter getAdapter(){
        return locationSyncAdapter;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return locationSyncAdapter.getSyncAdapterBinder();
    }
}
