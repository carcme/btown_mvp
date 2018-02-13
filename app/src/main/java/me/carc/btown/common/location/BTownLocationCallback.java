package me.carc.btown.common.location;

import android.location.Location;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationResult;

/**
 * Created by bamptonm on 21/11/2017.
 */

public interface BTownLocationCallback {

/*
    @Deprecated
    void onConnected(boolean connected);
*/
    void onLastKnownLocation(Location location);
/*
    @Deprecated
    void onLocationChanged(Location location);
*/


    void onLocationResult(LocationResult result);

    void onLocationAvailability(LocationAvailability availability);
}
