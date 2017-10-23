package me.carc.btown.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by Carc.me on 18.04.16.
 * <p/>
 * Get the status of the network connectivity
 */
public class Network {

    private static int status = C.TYPE_NC;

    public static boolean connected() {
        return status == ConnectivityManager.TYPE_WIFI || status == ConnectivityManager.TYPE_MOBILE;
    }

    private static int getStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return C.TYPE_WIFI;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return C.TYPE_MOBILE;
            }
        }

        return C.TYPE_NC;
    }

    static String getConnectivityStatusString(Context context) {
        status = Network.getStatus(context);
        String str = null;
        if (status == C.TYPE_WIFI) {
            str = "Wifi enabled";
        } else if (status == C.TYPE_MOBILE) {
            str = "Mobile data enabled";
        } else if (status == C.TYPE_NC) {
            str = "Not connected to Internet";
        }
        return str;
    }
}