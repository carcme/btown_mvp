package me.carc.btownmvp.db.bookmark;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Favorites Data Access Object (DAO)
 * Created by bamptonm on 03/10/2017.
 */

@Dao
public interface BookmarkDao {

    @Query("SELECT * FROM BookmarkEntry")
    List<BookmarkEntry> getAllBookmarks();

    @Query("SELECT * FROM BookmarkEntry WHERE pageId LIKE :osmId LIMIT 1")
    BookmarkEntry findByPageId(long osmId);

    @Insert
    void insertAll(List<BookmarkEntry> entries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BookmarkEntry entry);

    @Update
    void update(BookmarkEntry entry);

    @Delete
    void delete(BookmarkEntry entry);

    @Query("DELETE FROM BookmarkEntry")
    void nukeTable();
}
