
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Location implements Serializable, Parcelable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;

    @SerializedName("distance")
    @Expose
    private double distance;

    private String distanceFormatted;

    @SerializedName("labeledLatLngs")
    @Expose
    private ArrayList<LabeledLatLng> labeledLatLngs = new ArrayList<>();
    @SerializedName("postalCode")
    @Expose
    private String postalCode;
    @SerializedName("cc")
    @Expose
    private String cc;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("formattedAddress")
    @Expose
    private ArrayList<String> formattedAddress = new ArrayList<>();


    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getDistance() {return distance;}
    public void setDistance(double dist) {distance = dist;}

    public String getDistanceFormatted() {return distanceFormatted;}
    public void setDistanceFormatted(String dist) {distanceFormatted = dist;}

    public GeoPoint getGeoPoint() {
        return new GeoPoint(lat, lng);
    }

    public List<LabeledLatLng> getLabeledLatLngs() {
        return labeledLatLngs;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCc() {
        return cc;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public List<String> getFormattedAddress() {
        return formattedAddress;
    }




    public final static Creator<Location> CREATOR = new Creator<Location>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return (new Location[size]);
        }

    };
    private final static long serialVersionUID = 8787950514120783200L;

    protected Location(Parcel in) {
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.lat = ((Double) in.readValue((double.class.getClassLoader())));
        this.lng = ((Double) in.readValue((double.class.getClassLoader())));
        this.distance = ((Double) in.readValue((double.class.getClassLoader())));
        this.distanceFormatted = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.labeledLatLngs, (me.carc.btown.data.all4squ.entities.LabeledLatLng.class.getClassLoader()));
//        this.postalCode = ((Long) in.readValue((Long.class.getClassLoader())));
        this.postalCode = ((String) in.readValue((String.class.getClassLoader())));
        this.cc = ((String) in.readValue((String.class.getClassLoader())));
        this.city = ((String) in.readValue((String.class.getClassLoader())));
        this.state = ((String) in.readValue((String.class.getClassLoader())));
        this.country = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.formattedAddress, (java.lang.String.class.getClassLoader()));
    }

    public Location() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(address);
        dest.writeValue(lat);
        dest.writeValue(lng);
        dest.writeValue(distance);
        dest.writeValue(distanceFormatted);
        dest.writeList(labeledLatLngs);
        dest.writeValue(postalCode);
        dest.writeValue(cc);
        dest.writeValue(city);
        dest.writeValue(state);
        dest.writeValue(country);
        dest.writeList(formattedAddress);
    }

    public int describeContents() {
        return 0;
    }

}
