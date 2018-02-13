package me.carc.btown.data.results;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.carc.btown.BuildConfig;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;

/**
 * http://wiki.openstreetmap.org/wiki/Map_Features
 * <p>
 * Map tags to class
 */
public class OverpassQueryResult {

    private static final String TAG = C.DEBUG + Commons.getTag();

    @SerializedName("elements")
    public List<Element> elements = new ArrayList<>();

    public static class Element implements Serializable, Parcelable {

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

        public static class Tags implements Serializable, Parcelable {

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

            @SerializedName("facebook")
            public String facebook;

            @SerializedName("instagram")
            public String instagram;

            @SerializedName("twitter")
            public String twitter;

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
            public String thumbnail;
            public boolean isIcon;

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
                list.add(memorial);

                list.removeAll(Collections.singleton(null));

                if (BuildConfig.DEBUG) {
                    if (list.size() > 1) {
                        for (String str : list) {
                            Log.d(TAG, "getPrimaryType: " + str);
                        }
//                        throw new RuntimeException(list.toString());
                    }
                }

                return list.size() > 0 ? list.get(0) : null;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.aerialway);
                dest.writeString(this.aeroway);
                dest.writeString(this.amenity);
                dest.writeString(this.barrier);
                dest.writeString(this.boundary);
                dest.writeString(this.building);
                dest.writeString(this.craft);
                dest.writeString(this.emergency);
                dest.writeString(this.geological);
                dest.writeString(this.highway);
                dest.writeString(this.historic);
                dest.writeString(this.landuse);
                dest.writeString(this.leisure);
                dest.writeString(this.man_made);
                dest.writeString(this.military);
                dest.writeString(this.natural);
                dest.writeString(this.office);
                dest.writeString(this.place);
                dest.writeString(this.power);
                dest.writeString(this.public_transport);
                dest.writeString(this.railway);
                dest.writeString(this.route);
                dest.writeString(this.shop);
                dest.writeString(this.sport);
                dest.writeString(this.tourism);
                dest.writeString(this.waterway);
                dest.writeString(this.healthcare);
                dest.writeString(this.entrance);
                dest.writeString(this.memorial);
                dest.writeString(this.name);
                dest.writeString(this.phone);
                dest.writeString(this.contactPhone);
                dest.writeString(this.contactEmail);
                dest.writeString(this.website);
                dest.writeString(this.fax);
                dest.writeString(this.contactWebsite);
                dest.writeString(this.source);
                dest.writeString(this.sourceName);
                dest.writeString(this.sourceRef);
                dest.writeString(this.url);
                dest.writeString(this.addressCity);
                dest.writeString(this.addressPostCode);
                dest.writeString(this.addressSuburb);
                dest.writeString(this.addressStreet);
                dest.writeString(this.addressHouseNumber);
                dest.writeString(this.wheelchair);
                dest.writeString(this.wheelchairToilets);
                dest.writeString(this.wheelchairDescription);
                dest.writeString(this.openingHours);
                dest.writeString(this.internetAccess);
                dest.writeString(this.fee);
                dest.writeString(this.operator);
                dest.writeString(this.collectionTimes);
                dest.writeString(this.takeaway);
                dest.writeString(this.delivery);
                dest.writeString(this.outdoor_seating);
                dest.writeString(this.religion);
                dest.writeString(this.denomination);
                dest.writeString(this.image);
                dest.writeString(this.smoking);
                dest.writeString(this.note);
                dest.writeString(this.cuisine);
                dest.writeString(this.wikipedia);
                dest.writeString(this.wikidata);
                dest.writeString(this.nudism);
                dest.writeString(this.cycleway);
                dest.writeString(this.comment);
                dest.writeString(this.height);
            }

            public Tags() {
            }

            protected Tags(Parcel in) {
                this.aerialway = in.readString();
                this.aeroway = in.readString();
                this.amenity = in.readString();
                this.barrier = in.readString();
                this.boundary = in.readString();
                this.building = in.readString();
                this.craft = in.readString();
                this.emergency = in.readString();
                this.geological = in.readString();
                this.highway = in.readString();
                this.historic = in.readString();
                this.landuse = in.readString();
                this.leisure = in.readString();
                this.man_made = in.readString();
                this.military = in.readString();
                this.natural = in.readString();
                this.office = in.readString();
                this.place = in.readString();
                this.power = in.readString();
                this.public_transport = in.readString();
                this.railway = in.readString();
                this.route = in.readString();
                this.shop = in.readString();
                this.sport = in.readString();
                this.tourism = in.readString();
                this.waterway = in.readString();
                this.healthcare = in.readString();
                this.entrance = in.readString();
                this.memorial = in.readString();
                this.name = in.readString();
                this.phone = in.readString();
                this.contactPhone = in.readString();
                this.contactEmail = in.readString();
                this.website = in.readString();
                this.fax = in.readString();
                this.contactWebsite = in.readString();
                this.source = in.readString();
                this.sourceName = in.readString();
                this.sourceRef = in.readString();
                this.url = in.readString();
                this.addressCity = in.readString();
                this.addressPostCode = in.readString();
                this.addressSuburb = in.readString();
                this.addressStreet = in.readString();
                this.addressHouseNumber = in.readString();
                this.wheelchair = in.readString();
                this.wheelchairToilets = in.readString();
                this.wheelchairDescription = in.readString();
                this.openingHours = in.readString();
                this.internetAccess = in.readString();
                this.fee = in.readString();
                this.operator = in.readString();
                this.collectionTimes = in.readString();
                this.takeaway = in.readString();
                this.delivery = in.readString();
                this.outdoor_seating = in.readString();
                this.religion = in.readString();
                this.denomination = in.readString();
                this.image = in.readString();
                this.smoking = in.readString();
                this.note = in.readString();
                this.cuisine = in.readString();
                this.wikipedia = in.readString();
                this.wikidata = in.readString();
                this.nudism = in.readString();
                this.cycleway = in.readString();
                this.comment = in.readString();
                this.height = in.readString();
            }

            public static final Creator<Tags> CREATOR = new Creator<Tags>() {
                public Tags createFromParcel(Parcel source) {
                    return new Tags(source);
                }

                public Tags[] newArray(int size) {
                    return new Tags[size];
                }
            };
        }

        public GeoPoint getGeoPoint() {
            return new GeoPoint(lat, lon);
        }

        public static class DistanceComparator implements Comparator<OverpassQueryResult.Element> {
            @Override
            public int compare(Element lhs, Element rhs) {
                Double d1 = lhs.distance;
                Double d2 = rhs.distance;
                if (d1.compareTo(d2) < 0) {
                    return -1;
                } else if (d1.compareTo(d2) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.type);
            dest.writeLong(this.id);
            dest.writeDouble(this.lat);
            dest.writeDouble(this.lon);
            dest.writeDouble(this.distance);
            dest.writeDoubleArray(this.extent);
            dest.writeString(this.userDescription);
            dest.writeInt(this.iconId);
            dest.writeSerializable(this.tags);
        }

        public Element() {
        }

        protected Element(Parcel in) {
            this.type = in.readString();
            this.id = in.readLong();
            this.lat = in.readDouble();
            this.lon = in.readDouble();
            this.distance = in.readDouble();
            this.extent = in.createDoubleArray();
            this.userDescription = in.readString();
            this.iconId = in.readInt();
            this.tags = (Tags) in.readSerializable();
        }

        public static final Parcelable.Creator<Element> CREATOR = new Parcelable.Creator<Element>() {
            public Element createFromParcel(Parcel source) {
                return new Element(source);
            }

            public Element[] newArray(int size) {
                return new Element[size];
            }
        };
    }
}