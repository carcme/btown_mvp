package me.carc.btown.db.tours;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import me.carc.btown.data.wiki.WikiQueryPage;

/**
 * Tour entry core data structure
 *
 * Created by bamptonm on 2/9/17.
 */
@Entity(tableName = "TourEntry")
public class TourEntry  {

    private static final int EMPTY = -1;

    @PrimaryKey
    @ColumnInfo(name = "pageId")
    private long pageId = EMPTY;

    @ColumnInfo(name = "timestamp")
    private long timestamp = EMPTY;

    @ColumnInfo(name = "latitude")
    private double lat = EMPTY;

    @ColumnInfo(name = "longtitude")
    private double lon = EMPTY;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "link")
    private String linkUrl;

    @ColumnInfo(name = "extract")
    private String extract;

    @ColumnInfo(name = "thumbnail")
    private String thumbnail;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "userComment")
    private String userComment;



    @Ignore
    public TourEntry(){
        super();
    }

    @Ignore
    public TourEntry(WikiQueryPage page) {
        super();

        setTimestamp(new Date().getTime());

        pageId  = page.pageId();
        lat     = page.getLat();
        lon     = page.getLon();
        title   = page.title();
        linkUrl = page.fullurl();
        extract = page.extract();
        thumbnail = page.thumbUrl();
        description = page.description();
        userComment = page.userComment();
    }

    public TourEntry(long pageId, long timestamp, double lat, double lon, String title,
                     String linkUrl, String extract, String thumbnail, String description,
                     String userComment) {
        this.pageId = pageId;
        this.timestamp = timestamp;
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.linkUrl = linkUrl;
        this.extract = extract;
        this.thumbnail = thumbnail;
        this.description = description;
        this.userComment = userComment;
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long ts) { this.timestamp = ts; }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long id) {
        this.pageId = id;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String address) {
        this.linkUrl = address;
    }

    public String getExtract() { return extract; }

    public void setExtract(String extract) {
        this.extract = extract;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserComment() {
        return userComment;
    }
    public void setUserComment(String comment) {
        this.userComment = comment;
    }


}
