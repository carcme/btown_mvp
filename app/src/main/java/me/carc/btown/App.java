package me.carc.btown;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import me.carc.btown.common.Commons;
import me.carc.btown.common.NetworkChangeReceiver;
import me.carc.btown.db.AppDatabase;

/** Application class for BTown
 * Created by bamptonm on 19/09/2017.
 */

public class App extends Application {

    private static final String BTOWN_DATABASE_NAME = "btown.db";

    private AppDatabase database;
    private NetworkChangeReceiver networkChangeReceiver;
    private AppCompatActivity mCurrentActivity = null;


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

    /**
     * Init global values
     */
    public void onCreate() {
        super.onCreate();
//        if (BuildConfig.USE_CRASHLYTICS)
            Fabric.with(this, new Crashlytics());

        try {
            LeakCanary.install(this);
        }catch (Throwable ex){ // catch on androidx86 getExternalStorageDir is not writable
            ex.printStackTrace();
        }

        database = initDB();

        registerConnectivityRecver();
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


    @Override
    public void onTerminate() {
        super.onTerminate();
        try { unregisterReceiver(networkChangeReceiver);
        }catch (IllegalArgumentException e) { /*EMPTY*/ }

    }
}


