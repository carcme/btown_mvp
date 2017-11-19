
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LabeledLatLng implements Serializable, Parcelable
{

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;



    public final static Creator<LabeledLatLng> CREATOR = new Creator<LabeledLatLng>() {


        @SuppressWarnings({
            "unchecked"
        })
        public LabeledLatLng createFromParcel(Parcel in) {
            return new LabeledLatLng(in);
        }

        public LabeledLatLng[] newArray(int size) {
            return (new LabeledLatLng[size]);
        }

    }
    ;
    private final static long serialVersionUID = -3356251695892871229L;

    protected LabeledLatLng(Parcel in) {
        this.label = ((String) in.readValue((String.class.getClassLoader())));
        this.lat = ((double) in.readValue((double.class.getClassLoader())));
        this.lng = ((double) in.readValue((double.class.getClassLoader())));
    }

    public LabeledLatLng() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(label);
        dest.writeValue(lat);
        dest.writeValue(lng);
    }

    public int describeContents() {
        return  0;
    }

}
