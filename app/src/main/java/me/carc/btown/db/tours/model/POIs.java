package me.carc.btown.db.tours.model;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Tour stop extras to look out for
 * Created by bamptonm on 16/10/2017.
 */
@Keep
public class POIs implements Serializable, Parcelable {
    @SerializedName("title")
    public String title;
    @SerializedName("lat")
    public double lat;
    @SerializedName("lng")
    public double lng;
    @SerializedName("tag")
    public String tag;


    @Ignore
    public POIs() {
    }


    public POIs(String title, double lat, double lng, String tag) {
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.tag = tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.tag);
    }

    protected POIs(Parcel in) {
        this.title = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.tag = in.readString();
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