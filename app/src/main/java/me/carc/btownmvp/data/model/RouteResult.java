package me.carc.btownmvp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bamptonm on 30/09/2017.
 */

public class RouteResult implements Parcelable, Serializable {
    @SerializedName("paths")
    public List<Paths> paths = new ArrayList<>();

    public static class Paths implements Serializable {

        @SerializedName("distance")
        public double distance;

        @SerializedName("time")
        public long time;

        @SerializedName("descend")
        public double descend;

        @SerializedName("ascend")
        public double ascend;

        @SerializedName("bbox")
        public double[] bbox;

        @SerializedName("weight")
        public double weight;

        @SerializedName("transfers")
        public int transfers;

        @SerializedName("legs")
        public int[] legs;

        @SerializedName("instructions")
        public ArrayList<Instructions> instructions = new ArrayList<>();

        public static class Instructions {

            @SerializedName("text")
            public String text;

            @SerializedName("distance")
            public double distance;

            @SerializedName("time")
            public long time;

            @SerializedName("interval")
            public int[] interval;

            @SerializedName("sign")
            public int sign;

            @SerializedName("exit_number")
            public int exit_number;

            @SerializedName("street_name")
            public String street_name;
        }
/*
        @SerializedName("points")
        public List<Points> points = new ArrayList<>();


        public static class Points {

            @SerializedName("coordinates")
            public double[][] coordinates;


            public double getLon(){return coordinates[0];}
            public double getLat(){return coordinates[1];}
            public double getElevation(){return coordinates.length > 2 ? coordinates[3] : null;}



        }

        public GeoPoint getPointsGeoPoint(int index) {
            return new GeoPoint(points.get(index).getLat(),points.get(index).getLon());
        }
*/


    }

    public Paths getPath() {
        return paths != null ? paths.get(0) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.paths);
    }

    public RouteResult() {
    }

    protected RouteResult(Parcel in) {
        this.paths = new ArrayList<Paths>();
        in.readList(this.paths, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<RouteResult> CREATOR = new Parcelable.Creator<RouteResult>() {
        public RouteResult createFromParcel(Parcel source) {
            return new RouteResult(source);
        }

        public RouteResult[] newArray(int size) {
            return new RouteResult[size];
        }
    };
}
