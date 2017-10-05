package me.carc.btownmvp.data.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * http://wiki.openstreetmap.org/wiki/Map_Features
 * <p>
 * Map tags to class
 */
public class OverpassQueryResult implements Parcelable {
    @SerializedName("elements")
    public List<Element> elements = new ArrayList<>();

    public static class Element implements Serializable {

        @SerializedName("type")
        public String type;


        @SerializedName("id")
        public long id;


        @SerializedName("lat")
        public double lat;


        @SerializedName("lon")
        public double lon;

        /**
         * Store calculated distance from my locaiton
         */

        @SerializedName("distance")
        public double distance;


        @Ignore
        @SerializedName("extent")
        public double[] extent;

        /**
         * Store the user description (used in favorites)
         */

        @SerializedName("userDescription")
        public String userDescription;

        /**
         * Store the resource ID
         */

        @SerializedName("iconId")
        public int iconId;


        @Embedded
        @SerializedName("tags")
        public Tags tags = new Tags();

        public static class Tags implements Serializable {

/*
            @SerializedName("type")
            public String type;
*/

            // 1.1	Aerialway

            @SerializedName("aerialway")
            public String aerialway;
            //1.2	Aeroway

            @SerializedName("aeroway")
            public String aeroway;
            //1.3	Amenity

            @SerializedName("amenity")
            public String amenity;
            //1.4	Barrier

            @SerializedName("barrier")
            public String barrier;
            //1.5	Boundary

            @SerializedName("boundary")
            public String boundary;
            //1.6	Building

            @SerializedName("building")
            public String building;
            //1.7	Craft

            @SerializedName("craft")
            public String craft;
            //1.8	Emergency

            @SerializedName("emergency")
            public String emergency;
            //1.9	Geological

            @SerializedName("geological")
            public String geological;
            //1.10	Highway

            @SerializedName("highway")
            public String highway;
            //1.11	Historic

            @SerializedName("historic")
            public String historic;
            //1.12	Landuse

            @SerializedName("landuse")
            public String landuse;
            //1.13	Leisure

            @SerializedName("leisure")
            public String leisure;
            //1.14	Man_made

            @SerializedName("man_made")
            public String man_made;
            //1.14	Military

            @SerializedName("military")
            public String military;
            //1.16	Natural

            @SerializedName("natural")
            public String natural;
            //1.17	Office

            @SerializedName("office")
            public String office;
            //1.18	Place

            @SerializedName("place")
            public String place;
            //1.19	Power

            @SerializedName("power")
            public String power;
            //1.20	Public Transport

            @SerializedName("public_transport")
            public String public_transport;
            //1.21	Railway

            @SerializedName("railway")
            public String railway;
            //1.22	Route

            @SerializedName("route")
            public String route;
            //1.23	Shop

            @SerializedName("shop")
            public String shop;
            //1.24	Sport
            @SerializedName("sport")
            public String sport;
            //1.25	Tourism
            @SerializedName("tourism")
            public String tourism;
            //1.26	Waterway
            @SerializedName("waterway")
            public String waterway;
            //1.3.5	Healthcare
            @SerializedName("healthcare")
            public String healthcare;


            @SerializedName("entrance")
            public String entrance;

            @SerializedName("memorial")
            public String memorial;

            @SerializedName("name")
            public String name;

            @SerializedName("phone")
            public String phone;

            @SerializedName("contact:phone")
            public String contactPhone;

            @SerializedName("contact:email")
            public String contactEmail;

            @SerializedName("website")
            public String website;

            @SerializedName("fax")
            public String fax;

            @SerializedName("contact:website")
            public String contactWebsite;

            @SerializedName("source")
            public String source;

            @SerializedName("source:name")
            public String sourceName;

            @SerializedName("source:ref")
            public String sourceRef;

            @SerializedName("url")
            public String url;

            @SerializedName("addr:city")
            public String addressCity;

            @SerializedName("addr:postcode")
            public String addressPostCode;

            @SerializedName("addr:suburb")
            public String addressSuburb;

            @SerializedName("addr:street")
            public String addressStreet;

            @SerializedName("addr:housenumber")
            public String addressHouseNumber;

            @SerializedName("wheelchair")
            public String wheelchair;

            @SerializedName("toilets:wheelchair")
            public String wheelchairToilets;

            @SerializedName("wheelchair:description")
            public String wheelchairDescription;

            @SerializedName("opening_hours")
            public String openingHours;

            @SerializedName("internet_access")
            public String internetAccess;

            @SerializedName("fee")
            public String fee;

            @SerializedName("operator")
            public String operator;

            @SerializedName("collection_times")
            public String collectionTimes;

            @SerializedName("takeaway")
            public String takeaway;

            @SerializedName("delivery")
            public String delivery;

            @SerializedName("outdoor_seating")
            public String outdoor_seating;

            @SerializedName("religion")
            public String religion;

            @SerializedName("denomination")
            public String denomination;

            @SerializedName("image")
            public String image;

            @SerializedName("smoking")
            public String smoking;

            @SerializedName("note")
            public String note;

            @SerializedName("cuisine")
            public String cuisine;

            @SerializedName("wikipedia")
            public String wikipedia;

            @SerializedName("wikidata")
            public String wikidata;

            @SerializedName("nudism")
            public String nudism;

            @SerializedName("cycleway")
            public String cycleway;

            @SerializedName("comment")
            public String comment;

            @SerializedName("height")
            public String height;

            public String getAddress() {
                StringBuilder str = new StringBuilder();

                if (addressStreet != null)
                    str.append(addressStreet).append(", ");

                if (addressHouseNumber != null)
                    str.append(addressHouseNumber).append(", ");

                if (addressSuburb != null)
                    str.append(addressSuburb).append(", ");

                if (addressPostCode != null)
                    str.append(addressPostCode).append(", ");

                if (addressCity != null)
                    str.append(addressCity);

                return str.toString();
            }

            public String getPrimaryType() {
                ArrayList<String> list = new ArrayList<>(0);

                list.add(aerialway);
                list.add(aeroway);
                list.add(amenity);
                list.add(barrier);
                list.add(boundary);
                list.add(building);
                list.add(craft);
                list.add(emergency);
                list.add(geological);
                list.add(highway);
                list.add(historic);
                list.add(landuse);
                list.add(leisure);
                list.add(man_made);
                list.add(military);
                list.add(natural);
                list.add(office);
                list.add(place);
                list.add(power);
                list.add(public_transport);
                list.add(railway);
                list.add(route);
                list.add(shop);
                list.add(sport);
                list.add(tourism);
                list.add(waterway);
                list.add(healthcare);

                list.removeAll(Collections.singleton(null));

                return list.size() > 0 ? list.get(0) : null;
            }
        }

        public GeoPoint getGeoPoint() {
            return new GeoPoint(lat, lon);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.elements);
    }

    public OverpassQueryResult() {
    }

    protected OverpassQueryResult(Parcel in) {
        this.elements = new ArrayList<Element>();
        in.readList(this.elements, List.class.getClassLoader());
    }

    public static final Creator<OverpassQueryResult> CREATOR = new Creator<OverpassQueryResult>() {
        public OverpassQueryResult createFromParcel(Parcel source) {
            return new OverpassQueryResult(source);
        }

        public OverpassQueryResult[] newArray(int size) {
            return new OverpassQueryResult[size];
        }
    };
}

