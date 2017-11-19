
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
@Deprecated
public class Venue implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("contact")
    @Expose
    private ContactVenue contact;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("categories")
    @Expose
    private ArrayList<Category> categories = new ArrayList<>();
    @SerializedName("verified")
    @Expose
    private boolean verified;
    @SerializedName("stats")
    @Expose
    private Stats stats;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("rating")
    @Expose
    private double rating;
    @SerializedName("ratingColor")
    @Expose
    private String ratingColor;
    @SerializedName("ratingSignals")
    @Expose
    private int ratingSignals;
    @SerializedName("venueRatingBlacklisted")
    @Expose
    private boolean venueRatingBlacklisted;
    @SerializedName("allowMenuUrlEdit")
    @Expose
    private boolean allowMenuUrlEdit;

    @SerializedName("beenHere")
    @Expose
    private BeenHere beenHere;
    @SerializedName("venuePage")
    @Expose
    private VenuePage venuePage;

    @SerializedName("storeId")
    @Expose
    private String storeId;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ContactVenue getContact() {
        return contact;
    }

    public Location getLocation() {
        return location;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public boolean isVerified() {
        return verified;
    }

    public Stats getStats() {
        return stats;
    }

    public String getUrl() {
        return url;
    }

    public double getRating() {
        return rating;
    }

    public String getRatingColor() {
        return ratingColor;
    }

    public int getRatingSignals() {
        return ratingSignals;
    }

    public boolean isVenueRatingBlacklisted() {
        return venueRatingBlacklisted;
    }

    public boolean isAllowMenuUrlEdit() {
        return allowMenuUrlEdit;
    }

    public BeenHere getBeenHere() {
        return beenHere;
    }

    public VenuePage getVenuePage() {
        return venuePage;
    }

    public String getStoreId() {
        return storeId;
    }


    public final static Creator<Venue> CREATOR = new Creator<Venue>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        public Venue[] newArray(int size) {
            return (new Venue[size]);
        }

    };
    private final static long serialVersionUID = -5771976668691252205L;

    protected Venue(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.contact = ((ContactVenue) in.readValue((ContactVenue.class.getClassLoader())));
        this.location = ((Location) in.readValue((Location.class.getClassLoader())));
        in.readList(this.categories, (Category.class.getClassLoader()));
        this.verified = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.stats = ((Stats) in.readValue((Stats.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.rating = ((double) in.readValue((double.class.getClassLoader())));
        this.ratingColor = ((String) in.readValue((String.class.getClassLoader())));
        this.ratingSignals = ((int) in.readValue((int.class.getClassLoader())));
        this.venueRatingBlacklisted = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.allowMenuUrlEdit = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.beenHere = ((BeenHere) in.readValue((BeenHere.class.getClassLoader())));
        this.venuePage = ((VenuePage) in.readValue((VenuePage.class.getClassLoader())));
        this.storeId = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Venue() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(contact);
        dest.writeValue(location);
        dest.writeList(categories);
        dest.writeValue(verified);
        dest.writeValue(stats);
        dest.writeValue(url);
        dest.writeValue(rating);
        dest.writeValue(ratingColor);
        dest.writeValue(ratingSignals);
        dest.writeValue(venueRatingBlacklisted);
        dest.writeValue(allowMenuUrlEdit);
        dest.writeValue(beenHere);
        dest.writeValue(venuePage);
        dest.writeValue(storeId);
    }

    public int describeContents() {
        return 0;
    }

}
