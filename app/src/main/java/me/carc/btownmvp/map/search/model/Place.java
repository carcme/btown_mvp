package me.carc.btownmvp.map.search.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

import java.util.Arrays;

/**
 * Created by Carc.me on 13.10.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class Place implements Parcelable {

    /**
     * coordinates of place
     */
    private Location location;

    private double distance;
    private String name;
    private int placeId;
    private String[] types;
    private String[] tags;
    private String address;

    private String osmClass;
    private double lat;
    private double lng;
    private long osmId;
    private String osmKey;
    private String osmType;
    private String displayType;
    private int iconRes;
    private String iconName;
    private int iconColor;
    private long timestamp;

    private String userComment;

    public Place() { }

    private Place(Builder builder) {
        setLocation(builder.location);
        setDistance(builder.distance);
        setName(builder.name);
        setTypes(builder.types);
        setTags(builder.tags);
        setAddress(builder.address);
        setLat(builder.lat);
        setLng(builder.lng);
        setOsmClass(builder.osmClass);
        setOsmId(builder.osmId);
        setOsmKey(builder.osmKey);
        setOsmType(builder.osmType);
        setIconRes(builder.iconRes);
        setIconName(builder.iconName);
        setIconColor(builder.iconColor);
        setPlaceId(builder.placeId);
        setDisplayType(builder.displayType);
        setUserComment(builder.userComment);
        setTimestamp(builder.timestamp);
    }

    @Override
    public String toString() {
        return "Place{" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", types=" + Arrays.toString(types) +
                ", ad`ess='" + address + '\'' +
                '}';
    }

    /**
     * Get lat,lat - location of the place
     *
     * @return Location
     */
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }


    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * returns name of location
     *
     * @return name
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get placeid of location.
     * placeid is string id and can be used to get details of a place
     *
     * @return place id
     */
    public int getPlaceId() {
        return placeId;
    }
    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getDisplayType() {
        return displayType;
    }
    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getUserComment() {
        return userComment;
    }
    public void setUserComment(String comment) {
        this.userComment = comment;
    }

    /**
     * returns types of location e.g. types ["cafe","food","point_of_interest","establishment"]
     *
     * @return String array types
     */
    public String[] getTypes() {
        return types;
    }
    public String[] getTags() {
        return tags;
    }

    /**
     * set location types
     *
     * @param types array of tags
     */
    public void setTypes(String[] types) {
        this.types = types;
    }
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getOsmClass() {
        return osmClass;
    }
    public void setOsmClass(String osmClass) {
        this.osmClass = osmClass;
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

    public long getOsmId() {
        return osmId;
    }
    public void setOsmId(long osmId) {
        this.osmId = osmId;
    }

    public String getOsmKey() {
        return osmKey;
    }
    public void setOsmKey(String osmKey) { this.osmKey= osmKey; }

    public String getOsmType() {
        return osmType;
    }
    public void setOsmType(String osmType) { this.osmType = osmType; }

    public void setIconRes(int icon) { this.iconRes = icon; }
    public int getIconRes() { return iconRes; }

    public void setIconName(String name) { this.iconName = name; }
    public String getIconName() { return iconName; }

    public void setIconColor(int color) { this.iconColor = color; }
    public int getIconColor() { return iconColor; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public long getTimestamp() { return timestamp; }


    public GeoPoint getGeoPoint() {
        return new GeoPoint(getLat(), getLng());
    }


//    public double getLatitude(){
//        return location.getLatitude();
//    }
//
//    public double getLongitude(){
//        return location.getLongitude();
//    }

    public static final class Builder {
        private Location location;
        private double distance;
        private String name;
        private int placeId;
        private String[] types;
        private String[] tags;
        private String address;
        private int iconRes;
        private String iconName;
        private int iconColor;
        private String osmClass;
        private double lat;
        private double lng;
        private long osmId;
        private String osmKey;
        private String osmType;
        private String displayType;
        private String userComment;
        private long timestamp;

        public Builder() {
        }

        public Builder osmClass(String val) {
            osmClass = val;
            return this;
        }

        public Builder osmId(long val) {
            osmId = val;
            return this;
        }

        public Builder osmKey(String val) {
            osmKey = val;
            return this;
        }

        public Builder osmType(String val) {
            osmType = val;
            return this;
        }

        public Builder lat(double val) {
            lat = val;
            return this;
        }

        public Builder lng(double val) {
            lng = val;
            return this;
        }

        public Builder location(Location val) {
            location = val;
            return this;
        }

        public Builder distance(double val) {
            distance = val;
            return this;
        }
        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder placeId(int val) {
            placeId = val;
            return this;
        }

        public Builder types(String[] val) {
            types = val;
            return this;
        }
        public Builder tags(String[] val) {
            tags = val;
            return this;
        }
        public Builder displayType(String val) {
            displayType = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder iconRes(int val) {
            iconRes = val;
            return this;
        }

        public Builder iconName(String val) {
            iconName = val;
            return this;
        }

        public Builder iconColor(int val) {
            iconColor = val;
            return this;
        }

        public Builder userComment(String val) {
            userComment = val;
            return this;
        }

        public Builder timestamp(long val) {
            timestamp = val;
            return this;
        }

        public Place build() {
            return new Place(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.location, flags);
        dest.writeDouble(this.distance);
        dest.writeString(this.name);
        dest.writeInt(this.placeId);
        dest.writeStringArray(this.types);
        dest.writeStringArray(this.tags);
        dest.writeString(this.address);
        dest.writeString(this.osmClass);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeLong(this.osmId);
        dest.writeString(this.osmKey);
        dest.writeString(this.osmType);
        dest.writeString(this.displayType);
        dest.writeInt(this.iconRes);
        dest.writeString(this.iconName);
        dest.writeInt(this.iconColor);
        dest.writeString(this.userComment);
        dest.writeLong(this.timestamp);
    }

    protected Place(Parcel in) {
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.distance = in.readDouble();
        this.name = in.readString();
        this.placeId = in.readInt();
        this.types = in.createStringArray();
        this.tags = in.createStringArray();
        this.address = in.readString();
        this.osmClass = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.osmId = in.readLong();
        this.osmKey = in.readString();
        this.osmType = in.readString();
        this.displayType = in.readString();
        this.iconRes = in.readInt();
        this.iconName = in.readString();
        this.iconColor = in.readInt();
        this.userComment = in.readString();
        this.timestamp = in.readLong();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
