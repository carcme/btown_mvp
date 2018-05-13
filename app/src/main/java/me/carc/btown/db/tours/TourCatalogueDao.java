package me.carc.btown.db.tours;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import me.carc.btown.db.tours.model.TourCatalogueItem;

/**
 * Created by bamptonm on 12/05/2018.
 */

@Dao
public interface TourCatalogueDao {

    @Query("SELECT * FROM catalogue_table")
    LiveData<List<TourCatalogueItem>> getAllTours();

    @Query("SELECT * FROM catalogue_table WHERE tourId LIKE :tourId LIMIT 1")
    TourCatalogueItem getTour(int tourId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TourCatalogueItem catalogueItem);

    @Update
    void update(TourCatalogueItem entry);

    @Delete
    void delete(TourCatalogueItem entry);

    @Query("DELETE FROM catalogue_table")
    void nukeTable();

}