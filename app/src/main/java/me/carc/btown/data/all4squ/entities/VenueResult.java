
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class VenueResult implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("contact")
    @Expose
    private Contact contact;

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("canonicalUrl")
    @Expose
    private String canonicalUrl;

    @SerializedName("categories")
    @Expose
    private ArrayList<Category> categories = new ArrayList<>();

    @SerializedName("verified")
    @Expose
    private boolean verified;

    @SerializedName("stats")
    @Expose
    private Stats stats;

    @SerializedName("price")
    @Expose
    private Price price;

    @SerializedName("likes")
    @Expose
    private Likes likes;

    @SerializedName("dislike")
    @Expose
    private boolean dislike;

    @SerializedName("ok")
    @Expose
    private boolean ok;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("ratingColor")
    @Expose
    private String ratingColor;

    @SerializedName("ratingSignals")
    @Expose
    private int ratingSignals;

    @SerializedName("allowMenuUrlEdit")
    @Expose
    private boolean allowMenuUrlEdit;

    @SerializedName("beenHere")
    @Expose
    private BeenHere beenHere;

    @SerializedName("specials")
    @Expose
    private Specials specials;

    @SerializedName("photos")
    @Expose
    private Photos photosVenuePhotos;

    @SerializedName("reasons")
    @Expose
    private Reasons reasons;

    @SerializedName("hereNow")
    @Expose
    private HereNow hereNow;

    @SerializedName("createdAt")
    @Expose
    private int createdAt;

    @SerializedName("tips")
    @Expose
    private Tips tips;

    @SerializedName("tags")
    @Expose
    private ArrayList<Object> tags = new ArrayList<>();

    @SerializedName("shortUrl")
    @Expose
    private String shortUrl;

    @SerializedName("timeZone")
    @Expose
    private String timeZone;

    @SerializedName("listed")
    @Expose
    private Listed listed;

    @SerializedName("popular")
    @Expose
    private Popular popular;

    @SerializedName("pageUpdates")
    @Expose
    private PageUpdates pageUpdates;

    @SerializedName("inbox")
    @Expose
    private Inbox inbox;

    @SerializedName("venueChains")
    @Expose
    private ArrayList<Object> venueChains = new ArrayList<>();

    @SerializedName("attributes")
    @Expose
    private Attributes attributes;

    @SerializedName("bestPhoto")
    @Expose
    private Photo bestPhoto;

    @SerializedName("colors")
    @Expose
    private Colors colors;

    @SerializedName("hasMenu")
    @Expose
    private boolean hasMenu;

    @SerializedName("url")
    @Expose
    private String venueUrl;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("page")
    @Expose
    private Page page;




    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Contact getContact() {
        return contact;
    }

    public Location getLocation() {
        return location;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
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

    public Price getPrice() {
        return price;
    }

    public Likes getLikes() {
        return likes;
    }

    public boolean isDislike() {
        return dislike;
    }

    public boolean isOk() {
        return ok;
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

    public boolean isAllowMenuUrlEdit() {
        return allowMenuUrlEdit;
    }

    public BeenHere getBeenHere() {
        return beenHere;
    }

    public Specials getSpecials() {
        return specials;
    }

    public Photos getPhotosVenuePhotos() {
        return photosVenuePhotos;
    }

    public Reasons getReasons() {
        return reasons;
    }

    public HereNow getHereNow() {
        return hereNow;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public Tips getTips() {
        return tips;
    }

    public ArrayList<Object> getTags() {
        return tags;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public Listed getListed() {
        return listed;
    }

    public Popular getPopular() {
        return popular;
    }

    public PageUpdates getPageUpdates() {
        return pageUpdates;
    }

    public Inbox getInbox() {
        return inbox;
    }

    public ArrayList<Object> getVenueChains() {
        return venueChains;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public Photo getBestPhoto() {
        return bestPhoto;
    }

    public Colors getColors() {
        return colors;
    }

    public boolean isHasMenu() {
        return hasMenu;
    }

    public String getVenueUrl() {
        return venueUrl;
    }
    public String getDescription() {
        return description;
    }
    public Page getPage() {
        return page;
    }




    public final static Creator<VenueResult> CREATOR = new Creator<VenueResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public VenueResult createFromParcel(Parcel in) {
            return new VenueResult(in);
        }

        public VenueResult[] newArray(int size) {
            return (new VenueResult[size]);
        }

    };
    private final static long serialVersionUID = 6569062960322294248L;

    protected VenueResult(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.contact = ((Contact) in.readValue((Contact.class.getClassLoader())));
        this.location = (Location) in.readValue(Location.class.getClassLoader());
        this.canonicalUrl = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.categories, (Category.class.getClassLoader()));
        this.verified = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.stats = ((Stats) in.readValue((Stats.class.getClassLoader())));
        this.price = ((Price) in.readValue((Price.class.getClassLoader())));
        this.likes = ((Likes) in.readValue((Likes.class.getClassLoader())));
        this.dislike = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.ok = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.rating = ((double) in.readValue((double.class.getClassLoader())));
        this.ratingColor = ((String) in.readValue((String.class.getClassLoader())));
        this.ratingSignals = ((int) in.readValue((int.class.getClassLoader())));
        this.allowMenuUrlEdit = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.beenHere = ((BeenHere) in.readValue((BeenHere.class.getClassLoader())));
        this.specials = ((Specials) in.readValue((Specials.class.getClassLoader())));
        this.photosVenuePhotos = ((Photos) in.readValue((Photos.class.getClassLoader())));
        this.reasons = ((Reasons) in.readValue((Reasons.class.getClassLoader())));
        this.hereNow = ((HereNow) in.readValue((HereNow.class.getClassLoader())));
        this.createdAt = ((int) in.readValue((int.class.getClassLoader())));
        this.tips = ((Tips) in.readValue((Tips.class.getClassLoader())));
        in.readList(this.tags, (Object.class.getClassLoader()));
        this.shortUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.timeZone = ((String) in.readValue((String.class.getClassLoader())));
        this.listed = ((Listed) in.readValue((Listed.class.getClassLoader())));
        this.popular = ((Popular) in.readValue((Popular.class.getClassLoader())));
        this.pageUpdates = ((PageUpdates) in.readValue((PageUpdates.class.getClassLoader())));
        this.inbox = ((Inbox) in.readValue((Inbox.class.getClassLoader())));
        in.readList(this.venueChains, (Object.class.getClassLoader()));
        this.attributes = ((Attributes) in.readValue((Attributes.class.getClassLoader())));
        this.bestPhoto = ((Photo) in.readValue((Photo.class.getClassLoader())));
        this.colors = ((Colors) in.readValue((Colors.class.getClassLoader())));
        this.hasMenu = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.venueUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.page = ((Page) in.readValue((Page.class.getClassLoader())));
    }

    public VenueResult() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(contact);
        dest.writeValue(location);
        dest.writeValue(canonicalUrl);
        dest.writeList(categories);
        dest.writeValue(verified);
        dest.writeValue(stats);
        dest.writeValue(price);
        dest.writeValue(likes);
        dest.writeValue(dislike);
        dest.writeValue(ok);
        dest.writeValue(rating);
        dest.writeValue(ratingColor);
        dest.writeValue(ratingSignals);
        dest.writeValue(allowMenuUrlEdit);
        dest.writeValue(beenHere);
        dest.writeValue(specials);
        dest.writeValue(photosVenuePhotos);
        dest.writeValue(reasons);
        dest.writeValue(hereNow);
        dest.writeValue(createdAt);
        dest.writeValue(tips);
        dest.writeList(tags);
        dest.writeValue(shortUrl);
        dest.writeValue(timeZone);
        dest.writeValue(listed);
        dest.writeValue(popular);
        dest.writeValue(pageUpdates);
        dest.writeValue(inbox);
        dest.writeList(venueChains);
        dest.writeValue(attributes);
        dest.writeValue(bestPhoto);
        dest.writeValue(colors);
        dest.writeValue(hasMenu);
        dest.writeValue(venueUrl);
        dest.writeValue(description);
        dest.writeValue(page);
    }

    public int describeContents() {
        return 0;
    }

}
