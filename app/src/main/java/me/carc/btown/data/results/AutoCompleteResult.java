package me.carc.btown.data.results;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * http://photon.komoot.de/api/?q=berlin&lat=52.3879&lon=13.0582
 * Created by bamptonm on 23/09/2017.
 */
public class AutoCompleteResult implements Parcelable {
    @SerializedName("features")
    public List<Features> features = new ArrayList<>();

    public static class Features {

        @SerializedName("type")
        public String type;

        @SerializedName("geometry")
        public Geometry geometry = new Geometry();

        public static class Geometry {

            @SerializedName("coordinates")
            public double[] coordinates;

            @SerializedName("type")
            public String type;
        }

        @SerializedName("properties")
        public Properties properties = new Properties();

        public static class Properties {

            @SerializedName("osm_id")
            public long osm_id;

            @SerializedName("osm_type")
            public String osm_type;

            @SerializedName("country")
            public String country;

            @SerializedName("osm_key")
            public String osm_key;

            @SerializedName("housenumber")
            public String housenumber;

            @SerializedName("street")
            public String street;

            @SerializedName("postcode")
            public String postcode;

            @SerializedName("city")
            public String city;

            @SerializedName("osm_value")
            public String osm_value;

            @SerializedName("name")
            public String name;

            @SerializedName("state")
            public String state;

            @SerializedName("extent")
            public double[] extent;

        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.features);
    }

    public AutoCompleteResult() {
    }

    protected AutoCompleteResult(Parcel in) {
        this.features = new ArrayList<Features>();
        in.readList(this.features, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<AutoCompleteResult> CREATOR = new Parcelable.Creator<AutoCompleteResult>() {
        public AutoCompleteResult createFromParcel(Parcel source) {
            return new AutoCompleteResult(source);
        }

        public AutoCompleteResult[] newArray(int size) {
            return new AutoCompleteResult[size];
        }
    };
}
