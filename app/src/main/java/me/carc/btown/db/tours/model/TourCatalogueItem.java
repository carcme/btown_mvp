package me.carc.btown.db.tours.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import me.carc.btown.common.Commons;
import me.carc.btown.db.tours.Converters;

/**
 * Created by bamptonm on 16/10/2017.
 */
@Keep
@Entity(tableName = "catalogue_table")
public class TourCatalogueItem implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "tourId")
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

    @TypeConverters(Converters.class)
    @SerializedName("tourBrief")
    private List<String> tourBrief;

    @TypeConverters(Converters.class)
    @SerializedName("de_tourBrief")
    private List<String> de_tourBrief;

    @SerializedName("tourCoverImageFile")
    private String tourCoverImageFile;

    @TypeConverters(Converters.class)
    @SerializedName("attractions")
    private List<Attraction> attractions;


    public TourCatalogueItem(int tourId, String tourName, String de_tourName, double tourDistance,
                             int tourTime, double tourRating, String tourDesc, String de_tourDesc,
                             List<String> tourBrief, List<String> de_tourBrief, String tourCoverImageFile, List<Attraction> attractions) {
        this.tourId = tourId;
        this.tourName = tourName;
        this.de_tourName = de_tourName;
        this.tourDistance = tourDistance;
        this.tourTime = tourTime;
        this.tourRating = tourRating;
        this.tourDesc = tourDesc;
        this.de_tourDesc = de_tourDesc;
        this.tourBrief = tourBrief;
        this.de_tourBrief = de_tourBrief;
        this.tourCoverImageFile = tourCoverImageFile;
        this.attractions = attractions;
    }

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
        double duration = tourTime / 60.0;
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

    public List<Attraction> getAttractions() {
        return attractions;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getDe_tourName() {
        return de_tourName;
    }

    public void setDe_tourName(String de_tourName) {
        this.de_tourName = de_tourName;
    }

    public double getTourDistance() {
        return tourDistance;
    }

    public void setTourDistance(double tourDistance) {
        this.tourDistance = tourDistance;
    }

    public int getTourTime() {
        return tourTime;
    }

    public void setTourTime(int tourTime) {
        this.tourTime = tourTime;
    }

    public double getTourRating() {
        return tourRating;
    }

    public void setTourRating(double tourRating) {
        this.tourRating = tourRating;
    }

    public String getTourDesc() {
        return tourDesc;
    }

    public void setTourDesc(String tourDesc) {
        this.tourDesc = tourDesc;
    }

    public String getDe_tourDesc() {
        return de_tourDesc;
    }

    public void setDe_tourDesc(String de_tourDesc) {
        this.de_tourDesc = de_tourDesc;
    }

    public List<String> getTourBrief() {
        return tourBrief;
    }

    public void setTourBrief(List<String> tourBrief) {
        this.tourBrief = tourBrief;
    }

    public List<String> getDe_tourBrief() {
        return de_tourBrief;
    }

    public void setDe_tourBrief(List<String> de_tourBrief) {
        this.de_tourBrief = de_tourBrief;
    }

    public String getTourCoverImageFile() {
        return tourCoverImageFile;
    }

    public void setTourCoverImageFile(String tourCoverImageFile) {
        this.tourCoverImageFile = tourCoverImageFile;
    }

    public void setAttractions(ArrayList<Attraction> attractions) {
        this.attractions = attractions;
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
        dest.writeStringList(this.tourBrief);
        dest.writeStringList(this.de_tourBrief);
        dest.writeString(this.tourCoverImageFile);
        dest.writeTypedList(attractions);
    }

    protected TourCatalogueItem(Parcel in) {
        this.tourId = in.readInt();
        this.tourName = in.readString();
        this.de_tourName = in.readString();
        this.tourDistance = in.readDouble();
        this.tourTime = in.readInt();
        this.tourRating = in.readDouble();
        this.tourDesc = in.readString();
        this.de_tourDesc = in.readString();
        this.tourBrief = in.createStringArrayList();
        this.de_tourBrief = in.createStringArrayList();
        this.tourCoverImageFile = in.readString();
        this.attractions = in.createTypedArrayList(Attraction.CREATOR);
    }

    public static final Parcelable.Creator<TourCatalogueItem> CREATOR = new Parcelable.Creator<TourCatalogueItem>() {
        public TourCatalogueItem createFromParcel(Parcel source) {
            return new TourCatalogueItem(source);
        }

        public TourCatalogueItem[] newArray(int size) {
            return new TourCatalogueItem[size];
        }
    };
}
