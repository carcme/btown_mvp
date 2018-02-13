package me.carc.btown.common.location;

import android.Manifest;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import me.carc.btown.common.C;
import me.carc.btown.common.TinyDB;
import me.carc.btown.settings.Preferences;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Update location (GPS or Network)
 * Created by bamptonm on 21/11/2017.
 */

public class BTownFusedLocation {

    private final String TAG = BTownFusedLocation.class.getName();

    private Context mContext;
    private BTownLocationCallback mCallback;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final long STANDARD_UPDATE_INTERVAL = C.TIME_ONE_SECOND * 30;
    private static final long TRACKING_UPDATE_INTERVAL = C.TIME_ONE_SECOND * 2;
    private static final long FASTEST_UPDATE          = TRACKING_UPDATE_INTERVAL / 2;
    private static final long MAX_WAIT_TIME           = STANDARD_UPDATE_INTERVAL;

    private static final String AGPS_LAST_DOWNLOAD_DATE = "AGPS_LAST_DOWNLOAD_DATE";
    private static final long AGPS_REDOWNLOAD_DELAY = 20 * 60 * 60 * 1000; // 20 hours
    private static final long BASE_CORRECTION_VALUE = 360;

    private float previousCorrectionValue = BASE_CORRECTION_VALUE;
    private int PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;


    public BTownFusedLocation(Context c) {
        this.mContext = c;
    }

    public void setCallback(BTownLocationCallback callback) {
        mCallback = callback;
        createLocationRequest();
        if (mFusedLocationClient == null) {
            startLocationUpdates();
        }
    }

    public void getLastLocation() {
        if (mCallback != null && mCurrentLocation != null)
            mCallback.onLastKnownLocation(mCurrentLocation);
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(chooseNetworkGps())
                .setInterval(STANDARD_UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE);
    }

    public void updateLocationRequest(boolean tracking) {
        stopLocationUpdates();
        mLocationRequest = LocationRequest.create()
                .setPriority(chooseNetworkGps())
//                .setPriority(tracking ? LocationRequest.PRIORITY_HIGH_ACCURACY : LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(tracking ? TRACKING_UPDATE_INTERVAL : STANDARD_UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE)
                .setMaxWaitTime(tracking ? FASTEST_UPDATE : STANDARD_UPDATE_INTERVAL);
        startLocationUpdates();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    super.onLocationResult(result);
                    mCurrentLocation = result.getLocations().get(0);
                    mCallback.onLocationResult(result);
                }

                @Override
                public void onLocationAvailability(LocationAvailability availability) {
                    mCallback.onLocationAvailability(availability);
                }
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates() {
        if(mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mFusedLocationClient.flushLocations();
            mFusedLocationClient = null;
        }
    }

    public boolean canGetLocation() {
        return isNetworkEnabled() || isGPSEnabled();
    }

    private boolean isNetworkEnabled() {
        return ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGPSEnabled() {
        return ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private int chooseNetworkGps() {
        if(Preferences.alwaysUseGps(mContext)) {
            PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
        }else {
            if (isGPSEnabled()) {
                PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
            } else if (isNetworkEnabled()) {
                PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
            } else {
                PRIORITY = LocationRequest.PRIORITY_NO_POWER;
            }
        }
        return PRIORITY;
    }

    /**
     * force an updated check for internet connectivity here before destroying A-GPS-data
     */
    public void redownloadAGPS() {
        if (System.currentTimeMillis() - TinyDB.getTinyDB().getLong(AGPS_LAST_DOWNLOAD_DATE, 0L) > AGPS_REDOWNLOAD_DELAY) {
            try {
                final LocationManager service = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                service.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", null);
                Bundle bundle = new Bundle();
                service.sendExtraCommand("gps", "force_xtra_injection", bundle);
                service.sendExtraCommand("gps", "force_time_injection", bundle);
                TinyDB.getTinyDB().putLong(AGPS_LAST_DOWNLOAD_DATE, System.currentTimeMillis());
            } catch (Exception e) {
                TinyDB.getTinyDB().putLong(AGPS_LAST_DOWNLOAD_DATE, 0L);
                e.printStackTrace();
            }
        }
    }

    public float calcGeoMagneticCorrection(float val) {
        if (previousCorrectionValue == BASE_CORRECTION_VALUE && mCurrentLocation != null) {
            GeomagneticField gf = new GeomagneticField(
                    (float) mCurrentLocation.getLatitude(),
                    (float) mCurrentLocation.getLongitude(),
                    (float) mCurrentLocation.getAltitude(),
                    System.currentTimeMillis());

            previousCorrectionValue = gf.getDeclination();
        }
        if (previousCorrectionValue != BASE_CORRECTION_VALUE) {
            val += previousCorrectionValue;
        }
        return val;
    }
}