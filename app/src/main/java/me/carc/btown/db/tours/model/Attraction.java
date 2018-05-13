package me.carc.btown.db.tours.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.carc.btown.db.tours.Converters;

/**
 * Tour attractions holder
 * Created by bamptonm on 16/10/2017.
 */
@Keep
@Entity
public class Attraction implements Serializable, Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "atractionId")
    @SerializedName("id")
    private int attractionId;

    @SerializedName("stopName")
    private String stopName;

    @SerializedName("busStop")
    private String busStop;
    @SerializedName("de_busStop")
    private String de_busStop;

    @SerializedName("facebookPageId")
    private long facebookPageId;

    @SerializedName("wikiLink")
    private String wikiLink;
    @SerializedName("de_wikiLink")
    private String de_wikiLink;

    @SerializedName("web")
    private String web;

    @SerializedName("email")
    private String email;

    @SerializedName("tel")
    private String tel;

    @SerializedName("address")
    private String address;

    @SerializedName("hours")
    private String hours;

    @SerializedName("stopImageFile")
    private String stopImageFile;

    @SerializedName("stopImagesource")
    private String stopImagesource;

    @SerializedName("stopCost")
    private String stopCost;

    @SerializedName("stopType")
    private String stopType;

    @SerializedName("stopRating")
    private int stopRating;

    @TypeConverters(Converters.class)
    @SerializedName("location")
    private LatLon location = new LatLon();

    @TypeConverters(Converters.class)
    @SerializedName("pois")
    private List<POIs> pois = new ArrayList<>();

    @TypeConverters(Converters.class)
    @SerializedName("stopInfo")
    private StopInfo stopInfo = new StopInfo();

    @TypeConverters(Converters.class)
    @SerializedName("de_stopInfo")
    private StopInfo de_stopInfo = new StopInfo();


    public Attraction(int attractionId, String stopName, String busStop, String de_busStop, long facebookPageId,
                      String wikiLink, String de_wikiLink, String web, String email, String tel, String address,
                      String hours, String stopImageFile, String stopImagesource, String stopCost, String stopType,
                      int stopRating, LatLon location, List<POIs> pois, StopInfo stopInfo, StopInfo de_stopInfo) {
        this.attractionId = attractionId;
        this.stopName = stopName;
        this.busStop = busStop;
        this.de_busStop = de_busStop;
        this.facebookPageId = facebookPageId;
        this.wikiLink = wikiLink;
        this.de_wikiLink = de_wikiLink;
        this.web = web;
        this.email = email;
        this.tel = tel;
        this.address = address;
        this.hours = hours;
        this.stopImageFile = stopImageFile;
        this.stopImagesource = stopImagesource;
        this.stopCost = stopCost;
        this.stopType = stopType;
        this.stopRating = stopRating;
        this.location = location;
        this.pois = pois;
        this.stopInfo = stopInfo;
        this.de_stopInfo = de_stopInfo;
    }

    public int getAttractionIndex() { return attractionId; }
    public String getStopName() { return stopName; }
    public String getBusStop(boolean isGerman) {
        return isGerman ? de_busStop : busStop;
    }
    public long getFacebookPageId() { return facebookPageId; }
    public String getFacebookPageIdString() { return String.valueOf(facebookPageId); }
    public String getWiki(boolean isGerman) {
        return isGerman ? de_wikiLink : wikiLink;
    }
    public String getWebLink() { return web; }
    public String getEmail() { return email; }
    public String getTel() { return tel; }
    public String getAddress() { return address; }
    public String getHours() { return hours; }
    public String getImage() { return stopImageFile; }
    public String getImageSource() { return stopImagesource;}
    public String getCost() { return stopCost;}
    public String getType() { return stopType; }
    public int getRating() { return stopRating; }
    public LatLon getLocation() { return location; }
    public List<POIs> getPOIs() { return pois; }
    public StopInfo getAttractionStopInfo(boolean isGerman) {
        return isGerman ? de_stopInfo : stopInfo;
    }

    public int getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(int attractionId) {
        this.attractionId = attractionId;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getBusStop() {
        return busStop;
    }

    public void setBusStop(String busStop) {
        this.busStop = busStop;
    }

    public String getDe_busStop() {
        return de_busStop;
    }

    public void setDe_busStop(String de_busStop) {
        this.de_busStop = de_busStop;
    }

    public void setFacebookPageId(long facebookPageId) {
        this.facebookPageId = facebookPageId;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public String getDe_wikiLink() {
        return de_wikiLink;
    }

    public void setDe_wikiLink(String de_wikiLink) {
        this.de_wikiLink = de_wikiLink;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getStopImageFile() {
        return stopImageFile;
    }

    public void setStopImageFile(String stopImageFile) {
        this.stopImageFile = stopImageFile;
    }

    public String getStopImagesource() {
        return stopImagesource;
    }

    public void setStopImagesource(String stopImagesource) {
        this.stopImagesource = stopImagesource;
    }

    public String getStopCost() {
        return stopCost;
    }

    public void setStopCost(String stopCost) {
        this.stopCost = stopCost;
    }

    public String getStopType() {
        return stopType;
    }

    public void setStopType(String stopType) {
        this.stopType = stopType;
    }

    public int getStopRating() {
        return stopRating;
    }

    public void setStopRating(int stopRating) {
        this.stopRating = stopRating;
    }

    public void setLocation(LatLon location) {
        this.location = location;
    }

    public List<POIs> getPois() {
        return pois;
    }

    public void setPois(List<POIs> pois) {
        this.pois = pois;
    }

    public StopInfo getStopInfo() {
        return stopInfo;
    }

    public void setStopInfo(StopInfo stopInfo) {
        this.stopInfo = stopInfo;
    }

    public StopInfo getDe_stopInfo() {
        return de_stopInfo;
    }

    public void setDe_stopInfo(StopInfo de_stopInfo) {
        this.de_stopInfo = de_stopInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.attractionId);
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

    protected Attraction(Parcel in) {
        this.attractionId = in.readInt();
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