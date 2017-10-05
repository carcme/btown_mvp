package me.carc.btownmvp;

import android.app.Application;
import android.arch.persistence.room.Room;

import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.common.TinyDB;
import me.carc.btownmvp.db.AppDatabase;

/**
 * Created by bamptonm on 19/09/2017.
 */

public class App extends Application {

    private static final String BTOWN_DATABASE_NAME = "btown.db";
    private static App mInstance;

    private AppDatabase database;

    /**
     * Get the application context
     *
     * @return the application context
     */
    public static synchronized App get() {
        return mInstance;
    }

    public synchronized AppDatabase getDB() {
        if(Commons.isNull(database))
            initDB();
        return App.get().database; }

    /**
     * Init global values
     */
    public void onCreate() {
        super.onCreate();

        TinyDB db = new TinyDB(this);

        mInstance = this;
        initDB();
    }

    /**
     * Init database
     */
    private void initDB(){
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, BTOWN_DATABASE_NAME)
                .build();
    }
}


