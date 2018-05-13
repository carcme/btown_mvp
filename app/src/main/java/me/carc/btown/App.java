package me.carc.btown;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
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

    // Dont like this!!
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
        getFirebaseTours();
    }


    public void setUpdatingFirebase(boolean updating){
        isUpdatingFirebase = updating;
    }
    public boolean isUpdatingFirebase(){
        return isUpdatingFirebase;
    }

    public void getFirebaseTours() {
        if(isNetworkAvailable() && !isUpdatingFirebase()) {
            imagesServiceIntent = new Intent(getApplicationContext(), FirebaseImageDownloader.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(imagesServiceIntent);
            } else {
                startService(imagesServiceIntent);
            }
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
        super.onTerminate();
        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (IllegalArgumentException e) { /*EMPTY*/ }
    }
}