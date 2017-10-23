package me.carc.btown.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import me.carc.btown.db.bookmark.BookmarkDao;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.db.favorite.FavoriteDao;
import me.carc.btown.db.favorite.FavoriteEntry;
import me.carc.btown.db.history.HistoryDao;
import me.carc.btown.db.history.HistoryEntry;

/**
 * Applicaiton database (Room database)
 *
 * Created by bamptonm on 04/10/2017.
 */

@Database(entities = {FavoriteEntry.class, HistoryEntry.class, BookmarkEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoriteDao favoriteDao();
    public abstract HistoryDao historyDao();
    public abstract BookmarkDao bookmarkDao();
}