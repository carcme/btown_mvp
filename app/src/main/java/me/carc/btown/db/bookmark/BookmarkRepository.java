package me.carc.btown.db.bookmark;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import me.carc.btown.db.AppDatabase;


/**
 * Created by bamptonm on 12/05/2018.
 */

public class BookmarkRepository {

    private BookmarkDao mDao;
    private LiveData<List<BookmarkEntry>> mAllBookmarks;

    BookmarkRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDao = db.bookmarkDao();
        mAllBookmarks = mDao.getAllBookmarks();
    }

    LiveData<List<BookmarkEntry>> getAllBookmarks() {
        return mAllBookmarks;
    }

    void insert (BookmarkEntry entry) {
        new insertBookmark(mDao).execute(entry);
    }

    void delete (long id) {
        new deleteBookmark(mDao).execute(id);
    }


    private static class insertBookmark extends AsyncTask<BookmarkEntry, Void, Void> {
        private BookmarkDao mAsyncTaskDao;

        insertBookmark(BookmarkDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BookmarkEntry... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteBookmark extends AsyncTask<Long, Void, Void> {
        private BookmarkDao mAsyncTaskDao;

        deleteBookmark(BookmarkDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}