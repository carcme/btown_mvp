package me.carc.btown.db.history;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * History Data Access Object (DAO)
 * Created by bamptonm on 03/10/2017.
 */

@Dao
public interface HistoryDao {

    @Query("SELECT * FROM HistoryEntry")
    List<HistoryEntry> getAllHistories();

    @Query("SELECT * FROM HistoryEntry WHERE osmId LIKE :osmId LIMIT 1")
    HistoryEntry findByOsmId(long osmId);

    @Insert
    void insertAll(List<HistoryEntry> entries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HistoryEntry entry);

    @Update
    void update(HistoryEntry entry);

    @Delete
    void delete(HistoryEntry entry);

    @Query("DELETE FROM HistoryEntry")
    void nukeTable();
}
