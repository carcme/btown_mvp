package me.carc.btown.tours.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bamptonm on 16/10/2017.
 */
@Keep
public class LatLon implements Serializable, Parcelable {
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
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
    }

    public LatLon() {
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