package me.carc.btown.db.tours;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.LatLon;
import me.carc.btown.db.tours.model.POIs;
import me.carc.btown.db.tours.model.StopInfo;

/**
 * Created by bamptonm on 12/05/2018.
 */

public class Converters {

    @TypeConverter
    public static List<Attraction> attractionsToList(String string) {
        if (string == null) {
            return Collections.emptyList();
        }
        return new Gson().fromJson(string, new TypeToken<List<Attraction>>() {}.getType());
    }

    @TypeConverter
    public static String attractionToString(List<Attraction> list) {
        return new Gson().toJson(list);
    }

    /*******************************************/
    @TypeConverter
    public static List<String> stringToList(String string) {
        if (string == null) {
            return Collections.emptyList();
        }
        return new Gson().fromJson(string, new TypeToken<List<String>>() {}.getType());
    }

    @TypeConverter
    public static String stringListToString(List<String> list) {
        return new Gson().toJson(list);
    }

    /*******************************************/

    @TypeConverter
    public static LatLon stringToLatLon(String str) {
        if (str == null) {
            return new LatLon();
        }
        return new Gson().fromJson(str, new TypeToken<LatLon>() {}.getType());
    }

    @TypeConverter
    public static String latLonToString(LatLon latLon) {
        return new Gson().toJson(latLon);
    }

    /*******************************************/


    @TypeConverter
    public static List<POIs> stringToPoisList(String string) {
        if (string == null) {
            return Collections.emptyList();
        }
        return new Gson().fromJson(string, new TypeToken<List<POIs>>() {}.getType());
    }

    @TypeConverter
    public static String poisListToString(List<POIs> pois) {
        return new Gson().toJson(pois);
    }

    @TypeConverter
    public static POIs stringToPoi(String str) {
        if (str == null) {
            return new POIs();
        }
        return new Gson().fromJson(str, new TypeToken<POIs>() {}.getType());
    }

    @TypeConverter
    public static String poiToString(POIs poi) {
        return new Gson().toJson(poi);
    }

    /*******************************************/

    @TypeConverter
    public static StopInfo stringToStopInfo(String str) {
        if (str == null) {
            return new StopInfo();
        }
        return new Gson().fromJson(str, new TypeToken<StopInfo>() {}.getType());
    }

    @TypeConverter
    public static String latLonToString(StopInfo info) {
        return new Gson().toJson(info);
    }

}