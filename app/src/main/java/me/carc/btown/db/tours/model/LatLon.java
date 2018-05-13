package me.carc.btown.db.tours.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bamptonm on 16/10/2017.
 */
@Keep
@Entity(tableName = "ToursLatLon")
public class LatLon implements Serializable, Parcelable {

    private static final int EMPTY = -1;

    @PrimaryKey
    @ColumnInfo(name = "latLonId")
    private long keyId = EMPTY;

    @ColumnInfo(name = "lat")
    @SerializedName("lat")
    public double lat;

    @ColumnInfo(name = "lon")
    @SerializedName("long")
    public double lon;

    @Ignore
    public LatLon() {
    }
    public LatLon(long keyId, double lat, double lon) {
        this.keyId = keyId;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
    }


    protected LatLon(Parcel in) {
        this.lat = in.readDouble();
        this.lon = in.readDouble();
    }

    public static final Creator<LatLon> CREATOR = new Creator<LatLon>() {
        public LatLon createFromParcel(Parcel source) {
            return new LatLon(source);
        }

        public LatLon[] newArray(int size) {
            return new LatLon[size];
        }
    };
}