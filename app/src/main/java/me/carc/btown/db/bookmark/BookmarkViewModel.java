package me.carc.btown.db.bookmark;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by bamptonm on 12/05/2018.
 */

public class BookmarkViewModel extends AndroidViewModel {

    private BookmarkRepository mRepository;
    private LiveData<List<BookmarkEntry>> mAllBookmarks;

    public BookmarkViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BookmarkRepository(application);
        mAllBookmarks = mRepository.getAllBookmarks();
    }

    public LiveData<List<BookmarkEntry>> getmAllBookmarks() {
        return mAllBookmarks;
    }

    public void insert(BookmarkEntry entry) { mRepository.insert(entry); }

    public void delete(long id) { mRepository.delete(id); }
}
