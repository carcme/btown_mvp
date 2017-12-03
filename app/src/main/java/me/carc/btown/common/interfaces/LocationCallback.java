package me.carc.btown.common.interfaces;

import android.location.Location;

/**
 * Created by bamptonm on 21/11/2017.
 */

public interface LocationCallback {
    void onConnected(boolean connected);

    void onLastKnownLocation(Location location);

    void onLocationChanged(Location location);
}
