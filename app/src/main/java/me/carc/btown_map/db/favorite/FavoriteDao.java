package me.carc.btown_map.db.favorite;

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
public interface FavoriteDao {

    @Query("SELECT * FROM FavoriteEntry")
    List<FavoriteEntry> getAllFavorites();

    @Query("SELECT * FROM FavoriteEntry WHERE osmId LIKE :osmId LIMIT 1")
    FavoriteEntry findByOsmId(long osmId);

    @Insert
    void insertAll(List<FavoriteEntry> entries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteEntry entry);

    @Update
    void update(FavoriteEntry entry);

    @Delete
    void delete(FavoriteEntry entry);

    @Query("DELETE FROM FavoriteEntry")
    void nukeTable();
}
