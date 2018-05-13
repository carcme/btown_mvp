package me.carc.btown.db.tours;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import me.carc.btown.db.tours.model.Attraction;

/**
 * Created by bamptonm on 12/05/2018.
 */

@Dao
public interface AttractionDao {

    @Query("SELECT * FROM attraction")
    List<Attraction> getAllAttractions();

    @Query("SELECT * FROM attraction WHERE atractionId LIKE :atractionId LIMIT 1")
    Attraction getAttraction(int atractionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Attraction catalogueItem);

    @Update
    void update(Attraction entry);

    @Delete
    void delete(Attraction entry);

    @Query("DELETE FROM attraction")
    void nukeTable();

}