package me.carc.btown.tours.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Tour attractions holder
 * Created by bamptonm on 16/10/2017.
 */

public class Attraction implements Serializable, Parcelable {

    @SerializedName("id")
    private int tourId;
    public int getAttractionIndex() { return tourId; }

    @SerializedName("stopName")
    private String stopName;
    public String getStopName() { return stopName; }

    @SerializedName("busStop")
    private String busStop;
    @SerializedName("de_busStop")
    private String de_busStop;
    public String getBusStop() {
        Locale locale = Locale.getDefault();
        if(locale.getLanguage().equals("en"))
            return busStop;
        else
            return de_busStop;
    }

    @SerializedName("facebookPageId")
    private long facebookPageId;
    public long getFacebookPageId() { return facebookPageId; }
    public String getFacebookPageIdString() { return String.valueOf(facebookPageId); }

    @SerializedName("wikiLink")
    private String wikiLink;
    @SerializedName("de_wikiLink")
    private String de_wikiLink;
    public String getWiki() {
        Locale locale = Locale.getDefault();
        if(locale.getLanguage().equals("en"))
            return wikiLink;
        else
            return de_wikiLink;
    }

    @SerializedName("web")
    private String web;
    public String getWebLink() { return web; }

    @SerializedName("email")
    private String email;
    public String getEmail() { return email; }

    @SerializedName("tel")
    private String tel;
    public String getTel() { return tel; }

    @SerializedName("address")
    private String address;
    public String getAddress() { return address; }

    @SerializedName("hours")
    private String hours;
    public String getHours() { return hours; }

    @SerializedName("stopImageFile")
    private String stopImageFile;
    public String getImage() { return stopImageFile; }

    @SerializedName("stopImagesource")
    private String stopImagesource;
    public String getImageSource() { return stopImagesource;}

    @SerializedName("stopCost")
    private String stopCost;
    public String getCost() { return stopCost;}

    @SerializedName("stopType")
    private String stopType;
    public String getType() { return stopType; }

    @SerializedName("stopRating")
    private int stopRating;
    public int getRating() { return stopRating; }

    @SerializedName("location")
    private LatLon location = new LatLon();
    public LatLon getLocation() { return location; }

    @SerializedName("pois")
    private ArrayList<POIs> pois = new ArrayList<>();
    public ArrayList<POIs> getPOIs() { return pois; }

    @SerializedName("stopInfo")
    private StopInfo stopInfo = new StopInfo();
    @SerializedName("de_stopInfo")
    private StopInfo de_stopInfo = new StopInfo();

    public StopInfo getAttractionStopInfo() {
        Locale locale = Locale.getDefault();
        if(locale.getLanguage().equals("en"))
            return stopInfo;
        else
            return de_stopInfo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tourId);
        dest.writeString(this.stopName);
        dest.writeString(this.busStop);
        dest.writeString(this.de_busStop);
        dest.writeLong(this.facebookPageId);
        dest.writeString(this.wikiLink);
        dest.writeString(this.de_wikiLink);
        dest.writeString(this.web);
        dest.writeString(this.email);
        dest.writeString(this.tel);
        dest.writeString(this.address);
        dest.writeString(this.hours);
        dest.writeString(this.stopImageFile);
        dest.writeString(this.stopImagesource);
        dest.writeString(this.stopType);
        dest.writeInt(this.stopRating);
        dest.writeParcelable(this.location, 0);
        dest.writeTypedList(pois);
        dest.writeParcelable(this.stopInfo, 0);
        dest.writeParcelable(this.de_stopInfo, 0);
    }

    public Attraction() {
    }

    protected Attraction(Parcel in) {
        this.tourId = in.readInt();
        this.stopName = in.readString();
        this.busStop = in.readString();
        this.de_busStop = in.readString();
        this.facebookPageId = in.readLong();
        this.wikiLink = in.readString();
        this.de_wikiLink = in.readString();
        this.web = in.readString();
        this.email = in.readString();
        this.tel = in.readString();
        this.address = in.readString();
        this.hours = in.readString();
        this.stopImageFile = in.readString();
        this.stopImagesource = in.readString();
        this.stopType = in.readString();
        this.stopRating = in.readInt();
        this.location = in.readParcelable(LatLon.class.getClassLoader());
        this.pois = in.createTypedArrayList(POIs.CREATOR);
        this.stopInfo = in.readParcelable(StopInfo.class.getClassLoader());
        this.de_stopInfo = in.readParcelable(StopInfo.class.getClassLoader());
    }

    public static final Creator<Attraction> CREATOR = new Creator<Attraction>() {
        public Attraction createFromParcel(Parcel source) {
            return new Attraction(source);
        }

        public Attraction[] newArray(int size) {
            return new Attraction[size];
        }
    };
}