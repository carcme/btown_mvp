package me.carc.btown;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.android.vending.billing.IInAppBillingService;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import me.carc.btown.common.Commons;
import me.carc.btown.common.NetworkChangeReceiver;
import me.carc.btown.common.injection.component.ApplicationComponent;
import me.carc.btown.common.injection.component.DaggerApplicationComponent;
import me.carc.btown.common.injection.module.ApplicationModule;
import me.carc.btown.db.AppDatabase;
import me.carc.btown.tours.data.services.FirebaseImageDownloader;

/**
 * Application class for BTown
 * Created by bamptonm on 19/09/2017.
 */

public class App extends Application {

    public static final String BTOWN_DATABASE_NAME = "btown.db";
    private static Context applicationContext;
    private static IInAppBillingService mBillingService;

    private AppDatabase database;
    private NetworkChangeReceiver networkChangeReceiver;
    private AppCompatActivity mCurrentActivity = null;
    private Location mLatestLocation;
    private boolean isUpdatingFirebase;


    public Location getLatestLocation() {
        return mLatestLocation;
    }
    public void setLatestLocation(Location location) {
        mLatestLocation = location;
    }

    ApplicationComponent mApplicationComponent;

    private Intent imagesServiceIntent;

    // Dont like this!! Used in CacheDir and TinyDB, both of which should be removed/reworked
    public static Context getAC() {
        return applicationContext;
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public AppCompatActivity getCurrentActivity() {
        return mCurrentActivity;
    }
    public void setCurrentActivity(AppCompatActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public synchronized AppDatabase getDB() {
        if (Commons.isNull(database))
            database = initDB();
        return database;
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }


    private static ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBillingService = IInAppBillingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBillingService = null;
        }
    };

    /**
     * Init global values
     */
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.USE_CRASHLYTICS)
            Fabric.with(this, new Crashlytics());

        try {
            LeakCanary.install(this);
        } catch (Throwable ex) { // catch on androidx86 getExternalStorageDir is not writable
            ex.printStackTrace();
        }

        applicationContext = getApplicationContext();
        registerConnectivityRecver();
/*
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            getFirebaseTours();
*/

        if (mBillingService == null && GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE);
            bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public IInAppBillingService getmBillingService() {
        return mBillingService;
    }

    public void setUpdatingFirebase(boolean updating){
        isUpdatingFirebase = updating;
    }
    public boolean isUpdatingFirebase(){
        return isUpdatingFirebase;
    }

    /**
     * Get the tours and images
     */
    public void getFirebaseTours() {
        if(isNetworkAvailable() && !isUpdatingFirebase()) {
            imagesServiceIntent = new Intent(getApplicationContext(), FirebaseImageDownloader.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(imagesServiceIntent);
            else
                startService(imagesServiceIntent);
        }
    }

    /**
     * Init database
     */
    private AppDatabase initDB() {
        return Room.databaseBuilder(getApplicationContext(), AppDatabase.class, BTOWN_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    private void registerConnectivityRecver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(new NetworkChangeReceiver(), intentFilter);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && (networkInfo.isConnected());
    }


    @Override
    public void onTerminate() {
        stopService(imagesServiceIntent);
        try {
            if (mBillingService != null) unbindService(mServiceConnection);
        } catch (Exception e) { /* EMPTY CATCH */ }

        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (IllegalArgumentException e) { /* EMPTY CATCH */ }

        super.onTerminate();
    }
}