package me.carc.btownmvp.db.favorite;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Comparator;
import java.util.Date;

import me.carc.btownmvp.data.model.OverpassQueryResult;

/**
 * Favorite entry core data structure
 *
 * Created by bamptonm on 2/9/17.
 */
@Entity(tableName = "FavoriteEntry")
public class FavoriteEntry {

    private static final int EMPTY = -1;

    @PrimaryKey
    @ColumnInfo(name = "osmId")
    private long osmId = EMPTY;

    @ColumnInfo(name = "timestamp")
    private long timestamp = EMPTY;

    @ColumnInfo(name = "latitude")
    private double lat = EMPTY;

    @ColumnInfo(name = "longtitude")
    private double lon = EMPTY;

    @ColumnInfo(name = "displayName")
    private String name;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "userComment")
    private String comment;

    @ColumnInfo(name = "iconStr")
    private String iconStr = "";

    @ColumnInfo(name = "iconInt")
    private int iconInt = EMPTY;


    @Embedded
    private OverpassQueryResult.Element osmPojo;


    @Ignore
    public FavoriteEntry(){
        super();
    }

    @Ignore
    public FavoriteEntry(OverpassQueryResult.Element element) {
        super();

        setTimestamp(new Date().getTime());

        osmPojo = element;

        osmId = element.id;
        lat = element.lat;
        lon = element.lon;
        name = element.tags.name;
        address = element.tags.getAddress();
        comment = element.userDescription;
        iconInt = element.iconId;
        iconStr = element.tags.getPrimaryType();
    }

    public FavoriteEntry(long osmId, double lat, double lon, String name, String address,
                         String comment, String iconStr, int iconInt,
                         OverpassQueryResult.Element osmPojo) {
        this.osmId = osmId;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.address = address;
        this.comment = comment;
        this.iconStr = iconStr;
        this.iconInt = iconInt;
        this.osmPojo = osmPojo;
    }

    /**
     * Sort most recent to top
     */
    public static class TimeStampComparator implements Comparator<FavoriteEntry> {
        @Override
        public int compare(FavoriteEntry lhs, FavoriteEntry rhs) {
            Long ts1 = lhs.getTimestamp();
            Long ts2 = rhs.getTimestamp();
            if (ts1.compareTo(ts2) > 0) {
                return -1;
            } else if (ts1.compareTo(ts2) < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }


    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long ts) { this.timestamp = ts; }

    public long getOsmId() { return osmId; }
    public void setOsmId(long osmId) { this.osmId = osmId; }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIconStr() {
        return iconStr;
    }

    public void setIconStr(String iconStr) {
        this.iconStr = iconStr;
    }

    public int getIconInt() {
        return iconInt;
    }

    public void setIconInt(int iconInt) {
        this.iconInt = iconInt;
    }

    public OverpassQueryResult.Element getOsmPojo() { return osmPojo; }
    public void setOsmPojo(OverpassQueryResult.Element osmPojo) { this.osmPojo = osmPojo;}
}
