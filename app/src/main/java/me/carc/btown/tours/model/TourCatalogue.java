package me.carc.btown.tours.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.carc.btown.common.Commons;

/**
 * Created by bamptonm on 16/10/2017.
 */
@Keep
public class TourCatalogue implements Parcelable {

    @SerializedName("id")
    private int tourId;

    @SerializedName("tourName")
    private String tourName;
    @SerializedName("de_tourName")
    private String de_tourName;

    @SerializedName("tourDistance")
    private double tourDistance;

    @SerializedName("tourTime")
    private int tourTime;

    @SerializedName("tourRating")
    private double tourRating;

    @SerializedName("tourDesc")
    private String tourDesc;
    @SerializedName("de_tourDesc")
    private String de_tourDesc;

    @SerializedName("tourBrief")
    private String[] tourBrief;
    @SerializedName("de_tourBrief")
    private String[] de_tourBrief;

    @SerializedName("tourCoverImageFile")
    private String tourCoverImageFile;

    @SerializedName("attractions")
    private ArrayList<Attraction> attractions = new ArrayList<>();

    public int getCatalogueNumberOfStops() {
        return attractions.size();
    }

    public int getCatalogueIndex() {
        return tourId;
    }

    public String getCatalogueName(boolean isGermanLanguage) {
        return isGermanLanguage ? de_tourName : tourName;
    }

    public double getCatalogueDistance() {
        return tourDistance;
    }

    public String getCatalogueTourTime() {
        double duration = tourTime / 60;
        return Double.toString(duration).concat("h");
    }

    public double getCatalogueRating() { return tourRating; }

    public String getCatalogueDesc(boolean isGermanLanguage) {
        return isGermanLanguage ? de_tourDesc : tourDesc;
    }

    public String getCatalogueBrief(boolean isLangGerman) {
        if(isLangGerman)
            return Commons.buildStringFromArray(de_tourBrief);
         else
            return Commons.buildStringFromArray(tourBrief);
    }

    public String getCatalogueImage() {
        return tourCoverImageFile;
    }

    public ArrayList<Attraction> getAttractions() {
        return attractions;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tourId);
        dest.writeString(this.tourName);
        dest.writeString(this.de_tourName);
        dest.writeDouble(this.tourDistance);
        dest.writeInt(this.tourTime);
        dest.writeDouble(this.tourRating);
        dest.writeString(this.tourDesc);
        dest.writeString(this.de_tourDesc);
        dest.writeStringArray(this.tourBrief);
        dest.writeStringArray(this.de_tourBrief);
        dest.writeString(this.tourCoverImageFile);
        dest.writeTypedList(attractions);
    }

    public TourCatalogue() {
    }

    protected TourCatalogue(Parcel in) {
        this.tourId = in.readInt();
        this.tourName = in.readString();
        this.de_tourName = in.readString();
        this.tourDistance = in.readDouble();
        this.tourTime = in.readInt();
        this.tourRating = in.readDouble();
        this.tourDesc = in.readString();
        this.de_tourDesc = in.readString();
        this.tourBrief = in.createStringArray();
        this.de_tourBrief = in.createStringArray();
        this.tourCoverImageFile = in.readString();
        this.attractions = in.createTypedArrayList(Attraction.CREATOR);
    }

    public static final Creator<TourCatalogue> CREATOR = new Creator<TourCatalogue>() {
        public TourCatalogue createFromParcel(Parcel source) {
            return new TourCatalogue(source);
        }

        public TourCatalogue[] newArray(int size) {
            return new TourCatalogue[size];
        }
    };
}
