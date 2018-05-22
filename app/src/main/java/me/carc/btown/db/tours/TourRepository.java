package me.carc.btown.db.tours;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import me.carc.btown.db.AppDatabase;
import me.carc.btown.db.tours.model.TourCatalogueItem;


/**
 * Created by bamptonm on 12/05/2018.
 */

public class TourRepository {

    private TourCatalogueDao mTourDao;
    private LiveData<List<TourCatalogueItem>> mAllTours;
    private LiveData<TourCatalogueItem> mTour;

    TourRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTourDao = db.catalogueDao();
        mAllTours = mTourDao.getAllTours();
    }

    LiveData<List<TourCatalogueItem>> getAllTours() {
        return mAllTours;
    }

    LiveData<TourCatalogueItem> getTour(int id) {
        mTour  = mTourDao.getTour(id);
        return mTour;
    }

    void insert (TourCatalogueItem tour) {
        new insertAsyncTask(mTourDao).execute(tour);
    }

    private static class insertAsyncTask extends AsyncTask<TourCatalogueItem, Void, Void> {

        private TourCatalogueDao mAsyncTaskDao;

        insertAsyncTask(TourCatalogueDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TourCatalogueItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}