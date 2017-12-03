package me.carc.btown.common.location;

import android.Manifest;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import me.carc.btown.App;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.common.interfaces.LocationCallback;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by bamptonm on 21/11/2017.
 */

public class BTownLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private final String TAG = BTownLocation.class.getName();


    private LocationCallback mCallback = null;

    public static final long STANDARD_UPDATE_INTERVAL = C.TIME_ONE_SECOND * 30;
    public static final long TRACKING_UPDATE_INTERVAL = C.TIME_ONE_SECOND * 5;
    private static final long FASTEST_UPDATE = C.TIME_ONE_SECOND * 5;
    private static final long MAX_WAIT_TIME = STANDARD_UPDATE_INTERVAL;

    private static final String AGPS_LAST_DOWNLOAD_DATE = "AGPS_LAST_DOWNLOAD_DATE";
    private static final long AGPS_REDOWNLOAD_DELAY = 20 * 60 * 60 * 1000; // 20 hours
    private static final long BASE_CORRECTION_VALUE = 360;

    /**
     * Provides the entry point to Google Play services.
     */
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation = null;
    private LocationRequest mLocationRequest;
    private float previousCorrectionValue = BASE_CORRECTION_VALUE;
    private long mLastLocationMillis;
    private int PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;


    public BTownLocation(App c) {
        this.mContext = c;

        buildGoogleApiClient();
        if (canGetLocation()) {
            mGoogleApiClient.connect();

            //force an updated check for internet connectivity here before destroying A-GPS-data
/*
            if (System.currentTimeMillis() - TinyDB.getTinyDB().getLong(AGPS_LAST_DOWNLOAD_DATE, 0L) > AGPS_REDOWNLOAD_DELAY) {
                redownloadAGPS();
            }
*/
        }
    }

    private synchronized void buildGoogleApiClient() {
        if (Commons.isNull(mGoogleApiClient)) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(PRIORITY)
                .setInterval(STANDARD_UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE)
                .setMaxWaitTime(MAX_WAIT_TIME);
    }


    public void getCurrentLocation() {
        if(mCallback != null && mCurrentLocation != null)
            mCallback.onLastKnownLocation(mCurrentLocation);
    }

    public void getLastKnownLocation() {
        if (null != mCurrentLocation) {
            mCallback.onLastKnownLocation(mCurrentLocation);
        } else {
            chooseNetworkGps();
            if (null == mGoogleApiClient) {
                throw new RuntimeException("Google API is null");
            }
            if (canGetLocation())
                mGoogleApiClient.connect();
        }
    }

    public void setCallback(LocationCallback callback) {
        mCallback = callback;
    }


    public void updateLocationRequest(boolean tracking) {
        pauseLocationUpdates();

        mLocationRequest = LocationRequest.create()
                .setPriority(tracking ? LocationRequest.PRIORITY_HIGH_ACCURACY : LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(tracking ? TRACKING_UPDATE_INTERVAL : STANDARD_UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE)
                .setMaxWaitTime(MAX_WAIT_TIME);

        startLocationUpdates();
    }

    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */

    public void pauseLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void closeLocationProvider() {
        mCurrentLocation = null;
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
    }


    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mCallback.onLocationChanged(location);
        chooseNetworkGps();
        mLastLocationMillis = SystemClock.elapsedRealtime();
    }

    public Location getLatestLocation() {
        return mCurrentLocation;
    }


    /**
     * GoogleApiClient object successfully connects.
     */
    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null && mCallback != null) {
                mCallback.onLastKnownLocation(mCurrentLocation);
//                startLocationUpdates();
//            } else {
//                startLocationUpdates();
            }
        }
        if (mCallback != null)
            mCallback.onConnected(true);
    }

    /**
     * GoogleApiClient object suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mCallback.onConnected(false);
        mGoogleApiClient.connect();
    }

    /**
     * GoogleApiClient object failed to connect
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        mCallback.onConnected(false);
    }


    public void redownloadAGPS() {
        TinyDB db = TinyDB.getTinyDB();
        try {
            final LocationManager service = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            service.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", null);
            Bundle bundle = new Bundle();
            service.sendExtraCommand("gps", "force_xtra_injection", bundle);
            service.sendExtraCommand("gps", "force_time_injection", bundle);
            db.putLong(AGPS_LAST_DOWNLOAD_DATE, System.currentTimeMillis());
        } catch (Exception e) {
            db.putLong(AGPS_LAST_DOWNLOAD_DATE, 0L);
            e.printStackTrace();
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
        if (isGPSEnabled()) {
            PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
        } else if (isNetworkEnabled()) {
            PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        } else {
            PRIORITY = LocationRequest.PRIORITY_NO_POWER;
        }
        return PRIORITY;
    }

}