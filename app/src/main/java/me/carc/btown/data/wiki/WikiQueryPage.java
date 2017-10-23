package me.carc.btown.data.wiki;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A class representing a standard page object as returned by the MediaWiki API.
 */
public class WikiQueryPage implements Serializable {
    @SuppressWarnings("unused")
    private int pageid;
    @SuppressWarnings("unused")
    private int ns;
    @SuppressWarnings("unused")
    private int index;
    @SuppressWarnings("unused")
    private String wikitext;
    @SuppressWarnings("unused")
    private String pagelanguage;
    @SuppressWarnings("unused")
    private String pagelanguagehtmlcode;
    @SuppressWarnings("unused")
    private String pagelanguagedir;
    @SuppressWarnings("unused")
    private String touched;
    @SuppressWarnings("unused")
    private long lastrevid;
    @SuppressWarnings("unused")
    private long length;
    @SuppressWarnings("unused")
    private String fullurl;
    @SuppressWarnings("unused")
    private String editurl;
    @SuppressWarnings("unused")
    private String canonicalurl;
    @SuppressWarnings("unused")
    private String extract;
    @SuppressWarnings("unused,NullableProblems")
    @NonNull
    private String title;
    @SuppressWarnings("unused")
    @Nullable
    @Embedded
    private List<Coordinates> coordinates;
    @SuppressWarnings("unused")
    @Nullable
    @Embedded
    private Thumbnail thumbnail;
    @SuppressWarnings("unused")
    @Nullable
    @SerializedName("terms")
    @Ignore
    private Terms terms;
    @Nullable
    private String distance;

    public double dDist;

    @SuppressWarnings("unused")
    private String userComment;

    @NonNull
    public String title() { return title; }
    public void setTitle(String title) { this.title = title; }

    @NonNull
    public String fullurl() {
        return fullurl;
    }
    public void setFullurl(String url) {
        fullurl = url;
    }

    public int index() {
        return index;
    }

    public String userComment() { return userComment; }
    public void userComment(String comment) { userComment = comment; }


    @Nullable
    public void setCoordinates(Coordinates coordinates)  {
        this.coordinates = new ArrayList<>();
        this.coordinates().add(coordinates);
    }
    public List<Coordinates> coordinates()  {
        // TODO: Handle null values in lists during deserialization, perhaps with a new
        // @RequiredElements annotation and corresponding TypeAdapter
        if (coordinates != null) {
            coordinates.removeAll(Collections.singleton(null));
        }
        return coordinates;
    }

    public int pageId() { return pageid; }
    public void setPageId(int id) { pageid = id; }

    public double getLat() {
        return coordinates().get(0).lat();
    }

    public double getLon() {
        return coordinates().get(0).lon();
    }

    @Nullable
    public String thumbUrl() {
        return thumbnail != null ? thumbnail.source() : null;
    }
    public void setThumbUrl(String thumb) {
        if(thumbnail == null)
            thumbnail = new Thumbnail();
        thumbnail.source = thumb;
    }

    @Nullable
    public String extract() { return extract != null ? extract : null; }
    public void  setExtract(String ex) { extract = ex; }

    public void setDistance(String dist) { distance = dist; }

    public String distance() { return distance != null ? distance : "--"; }

    @Nullable
    public String description() {
        return terms != null && terms.description() != null ? terms.description().get(0) : null;
    }
    @Nullable
    public void setDescription(String desc) {
        if(terms == null)
            terms = new Terms();
        terms.description = new ArrayList<>();
        terms.description.add(desc);
    }


    public static class Coordinates implements Serializable {
        // Use Double object type rather than primitive type so that the presence of the fields can
        // be checked correctly by the RequiredFieldsCheckOnReadTypeAdapter.
        @SuppressWarnings("unused")
        @NonNull
        private Double lat;
        @SuppressWarnings("unused")
        @NonNull
        private Double lon;
        @SuppressWarnings("unused")
        @NonNull
        private Double dist;

        public Coordinates(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double lat() {
            return lat;
        }

        public double lon() {
            return lon;
        }

        public double dist() {
            return dist;
        }
    }

    static class Terms implements Serializable {
        @SuppressWarnings("unused")
        @SerializedName("description")
        private List<String> description;

        List<String> description() {
            return description;
        }
    }

    static class Thumbnail implements Serializable {
        @SuppressWarnings("unused")
        private String source;
        @SuppressWarnings("unused")
        private int width;
        @SuppressWarnings("unused")
        private int height;

        String source() {
            return source;
        }
    }

    public static class DistanceComparator implements Comparator<WikiQueryPage> {
        @Override
        public int compare(WikiQueryPage lhs, WikiQueryPage rhs) {

            Double d1 = lhs.dDist;
            Double d2 = rhs.dDist;
            if (d1.compareTo(d2) < 0) {
                return -1;
            } else if (d1.compareTo(d2) > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }


}
