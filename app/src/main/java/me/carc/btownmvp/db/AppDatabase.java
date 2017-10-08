package me.carc.btownmvp.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import me.carc.btownmvp.db.favorite.FavoriteDao;
import me.carc.btownmvp.db.favorite.FavoriteEntry;
import me.carc.btownmvp.db.history.HistoryDao;
import me.carc.btownmvp.db.history.HistoryEntry;

/**
 * Applicaiton database (Room database)
 *
 * Created by bamptonm on 04/10/2017.
 */

@Database(entities = {FavoriteEntry.class, HistoryEntry.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoriteDao favoriteDao();
    public abstract HistoryDao historyDao();
}