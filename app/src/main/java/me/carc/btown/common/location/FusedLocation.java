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
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.common.interfaces.LocationCallback;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Interface for using Google's FusedLocationAPI.
 */
@Deprecated
public class FusedLocation implements ConnectionCallbacks, OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = FusedLocation.class.getName();

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
    private boolean inProgress = false;


    public FusedLocation(Context c, LocationCallback callback) {
        this.mContext = c;
        this.mCallback = callback;

        buildGoogleApiClient();
        if (canGetLocation()) {
            mGoogleApiClient.connect();

            //force an updated check for internet connectivity here before destroying A-GPS-data
            if (System.currentTimeMillis() - TinyDB.getTinyDB().getLong(AGPS_LAST_DOWNLOAD_DATE, 0L) > AGPS_REDOWNLOAD_DELAY) {
                redownloadAGPS();
            }
        }
    }

    /**
     * Sets up FusedLocation Updates and chooses the best reading among `maxTries` samples.
     *
     * @param maxTries Maximum number of times to sample GPS readings
     */
    public void getCurrentLocation(int maxTries) {
        chooseNetworkGps();
        buildGoogleApiClient();
        inProgress = true;
        if (canGetLocation())
            mGoogleApiClient.connect();
    }

    public void getLastKnownLocation() {
        chooseNetworkGps();
        buildGoogleApiClient();
        inProgress = true;
        if (canGetLocation())
            mGoogleApiClient.connect();
    }

    public void setCallback(LocationCallback callback) {
        mCallback = callback;
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

    public void updateLocationRequest(boolean tracking) {
        stopLocationUpdates(false);

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

    public void stopLocationUpdates() {
        stopLocationUpdates(true);
    }

    private void stopLocationUpdates(boolean resetGoogleApi) {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        reset(resetGoogleApi);
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

    public Location getLocation() {
        return mCurrentLocation;
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                mCallback.onLastKnownLocation(mCurrentLocation);
//                startLocationUpdates();
//            } else {
//                startLocationUpdates();
            }
        }
        mCallback.onConnected(true);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mCallback.onConnected(false);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        mCallback.onConnected(false);
    }

    private void reset(boolean resetGoogleApi) {
        mCurrentLocation = null;
        inProgress = false;
        if (resetGoogleApi) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
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

    public boolean isInProgress() {
        return inProgress;
    }

}