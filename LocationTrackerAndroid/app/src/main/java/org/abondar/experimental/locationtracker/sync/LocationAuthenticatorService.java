package org.abondar.experimental.locationtracker.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LocationAuthenticatorService extends Service {

    private AbstractAccountAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new LocationAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
