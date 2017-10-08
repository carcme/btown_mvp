package me.carc.btownmvp;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.squareup.leakcanary.LeakCanary;

import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.common.NetworkChangeReceiver;
import me.carc.btownmvp.common.TinyDB;
import me.carc.btownmvp.db.AppDatabase;

/** Application class for BTown
 * Created by bamptonm on 19/09/2017.
 */

public class App extends Application {

    private static final String BTOWN_DATABASE_NAME = "btown.db";
    private static App mInstance;

    private AppDatabase database;
    private NetworkChangeReceiver networkChangeReceiver;

    /**
     * Get the application context
     *
     * @return the application context
     */
    public static synchronized App get() {
        return mInstance;
    }

    public synchronized AppDatabase getDB() {
        if (Commons.isNull(database))
            initDB();
        return App.get().database;
    }

    /**
     * Init global values
     */
    public void onCreate() {
        super.onCreate();

        try {
            LeakCanary.install(this);
        }catch (Throwable ex){ // catch on androidx86 getExternalStorageDir is not writable
            ex.printStackTrace();
        }

        TinyDB db = new TinyDB(this);

        mInstance = this;
        initDB();

        registerConnectionRecver();
    }

    /**
     * Init database
     */
    private void initDB() {
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, BTOWN_DATABASE_NAME)
                .build();
    }

    private void registerConnectionRecver() {
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


