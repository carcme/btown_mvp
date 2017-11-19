package me.carc.btown.tours.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Classes to hold the tour data
 * Created by bamptonm on 16/10/2017.
 */
@Keep
public class TourHolderResult implements Parcelable {

    @SuppressWarnings("unused")
    @SerializedName("filename")
    public String filename;
    @SuppressWarnings("unused")
    @SerializedName("version")
    public int version;

    @SuppressWarnings("unused")
    @SerializedName("tours")
    public ArrayList<TourCatalogue> tours = new ArrayList<>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filename);
        dest.writeInt(this.version);
        dest.writeTypedList(tours);
    }

    public TourHolderResult() {
    }

    protected TourHolderResult(Parcel in) {
        this.filename = in.readString();
        this.version = in.readInt();
        this.tours = in.createTypedArrayList(TourCatalogue.CREATOR);
    }

    public static final Parcelable.Creator<TourHolderResult> CREATOR = new Parcelable.Creator<TourHolderResult>() {
        public TourHolderResult createFromParcel(Parcel source) {
            return new TourHolderResult(source);
        }

        public TourHolderResult[] newArray(int size) {
            return new TourHolderResult[size];
        }
    };
}
