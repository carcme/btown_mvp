package me.carc.btown.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import me.carc.btown.App;
import me.carc.btown.db.bookmark.BookmarkDao;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.db.favorite.FavoriteDao;
import me.carc.btown.db.favorite.FavoriteEntry;
import me.carc.btown.db.history.HistoryDao;
import me.carc.btown.db.history.HistoryEntry;
import me.carc.btown.db.tours.AttractionDao;
import me.carc.btown.db.tours.TourCatalogueDao;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.TourCatalogueItem;

/**
 * Applicaiton database (Room database)
 *
 * Created by bamptonm on 04/10/2017.
 */

@Database(entities = {FavoriteEntry.class, HistoryEntry.class, BookmarkEntry.class, TourCatalogueItem.class, Attraction.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoriteDao favoriteDao();
    public abstract HistoryDao historyDao();
    public abstract BookmarkDao bookmarkDao();
    public abstract TourCatalogueDao catalogueDao();
    public abstract AttractionDao attractionDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, App.BTOWN_DATABASE_NAME)
                            .addMigrations(Migrations.MIGRATION_1_2)
//                            .fallbackToDestructiveMigration()  // TODO: REMOVE FOR RELEASE
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

//        private final TourCatalogueDao catalogueDao;
//        private final AttractionDao attractionDao;

        PopulateDbAsync(AppDatabase db) {
//            catalogueDao = db.catalogueDao();
//            attractionDao = db.attractionDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            // PRE-POPULATE THE DB's HERE

            return null;
        }
    }

}