package me.carc.btown_map.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import me.carc.btown_map.BuildConfig;

/**
 * Detect network state changes
 * Created by bamptonm on 08/10/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String status = Network.getConnectivityStatusString(context);
        if(BuildConfig.DEBUG)
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }
}