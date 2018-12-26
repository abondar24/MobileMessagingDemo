package org.abondar.experimental.locationtracker.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.abondar.experimental.locationtracker.R;
import org.abondar.experimental.locationtracker.data.LocationData;
import org.abondar.experimental.locationtracker.util.ConnectionUtil;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

import static android.content.Context.ACTIVITY_SERVICE;
import static org.abondar.experimental.locationtracker.util.ConnectionUtil.*;

public class LocationSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int SYNC_INTERVAL = 1;
    private static final int SYNC_FLEXTIME = 1;

    private StompClient stompClient;
    private static LocationData locationData;
    private ObjectMapper mapper;


    public LocationSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        initCommon();
    }

    public LocationSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        initCommon();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        if (locationData==null){
            Log.e(ACTIVITY_SERVICE,"Nothing to send");
            return;
        }

        String json="";

        try {
            json = mapper.writeValueAsString(locationData);
        } catch (JsonProcessingException ex){
            Log.e(ACTIVITY_SERVICE,"Empty location data");
        }

        stompClient.send(ConnectionUtil.STOMP_QUEUE,json).subscribe();
    }

    @Override
    public void onSyncCanceled(){
        stompClient.disconnect();
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;

            }
            ContentResolver.setIsSyncable(newAccount, context.getString(R.string.content_authority), 1);
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        LocationSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        SyncRequest request = new SyncRequest.Builder().
                syncPeriodic(syncInterval, flexTime).
                setSyncAdapter(account, authority).
                setExtras(new Bundle()).build();
        ContentResolver.requestSync(request);
    }

    private void initCommon(){
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, STOMP_HOST+STOMP_PORT +STOMP_ENDPOINT);
        mapper = new ObjectMapper();
    }




    public static void setLocationData(LocationData locationData) {
        LocationSyncAdapter.locationData = locationData;
    }
}
