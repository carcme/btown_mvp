package me.carc.btown_map.tours.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bamptonm on 16/10/2017.
 */

public class POIs implements Serializable, Parcelable {
    @SerializedName("title")
    public String title;
    @SerializedName("lat")
    public double lat;
    @SerializedName("long")
    public double lon;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
    }

    public POIs() {
    }

    protected POIs(Parcel in) {
        this.title = in.readString();
        this.lat = in.readDouble();
        this.lon = in.readDouble();
    }

    public static final Creator<POIs> CREATOR = new Creator<POIs>() {
        public POIs createFromParcel(Parcel source) {
            return new POIs(source);
        }

        public POIs[] newArray(int size) {
            return new POIs[size];
        }
    };
}