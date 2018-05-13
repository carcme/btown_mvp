package me.carc.btown.db.tours.model;

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
public class ToursResponse implements Parcelable {

    @SuppressWarnings("unused")
    @SerializedName("filename")
    public String filename;

    @SuppressWarnings("unused")
    @SerializedName("version")
    public int version;

    @SuppressWarnings("unused")
    @SerializedName("tours")
    public ArrayList<TourCatalogueItem> tours = new ArrayList<>();


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

    public ToursResponse() {
    }

    protected ToursResponse(Parcel in) {
        this.filename = in.readString();
        this.version = in.readInt();
        this.tours = in.createTypedArrayList(TourCatalogueItem.CREATOR);
    }

    public static final Parcelable.Creator<ToursResponse> CREATOR = new Parcelable.Creator<ToursResponse>() {
        public ToursResponse createFromParcel(Parcel source) {
            return new ToursResponse(source);
        }

        public ToursResponse[] newArray(int size) {
            return new ToursResponse[size];
        }
    };
}
