
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class Response implements Serializable, Parcelable {

    private final static long serialVersionUID = 5894934532213494116L;

    @SerializedName("venue")
    @Expose
    private VenueResult venueResult;

    @SerializedName("list")
    @Expose
    private ListResult listResult;

    @SerializedName("lists")
    @Expose
    private BtownListsResult btownListsResult;

    @SerializedName("photos")
    @Expose
    private Photos photos;

    @SerializedName("menu")
    @Expose
    private Menu menu;

    @SerializedName("tips")
    @Expose
    private GroupsTip tips;

    @SerializedName("venues")
    @Expose
    public ArrayList<VenueResult> venuesSearch = null;


    // FSQ EXPLORE
    @SerializedName("warning")
    @Expose
    public Warning warning = new Warning();

    public static class Warning {

        @SerializedName("text")
        @Expose
        public String text;

    }

    @SerializedName("suggestedRadius")
    @Expose
    public Long suggestedRadius;
    @SerializedName("headerLocation")
    @Expose
    public String headerLocation;
    @SerializedName("headerFullLocation")
    @Expose
    public String headerFullLocation;
    @SerializedName("headerLocationGranularity")
    @Expose
    public String headerLocationGranularity;
    @SerializedName("totalResults")
    @Expose
    public Long totalResults;
    @SerializedName("groups")
    @Expose
    public ArrayList<GroupExplore> explore = new ArrayList<>();


    public Response() {
    }

    public VenueResult getVenueResult() { return venueResult;    }
    public ListResult getListResult() {return listResult;  }
    public BtownListsResult getBtownListsResult() { return btownListsResult; }
    public Photos getPhotos() { return photos; }
    public Menu getMenu() { return menu; }
    public GroupsTip getTips() { return tips; }
    public ArrayList<VenueResult> getSearchResult() { return venuesSearch; }
    public ArrayList<GroupExplore> getExplore() { return explore; }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(venueResult);
        dest.writeValue(listResult);
        dest.writeValue(btownListsResult);
        dest.writeValue(photos);
        dest.writeValue(menu);
        dest.writeValue(tips);
        dest.writeValue(explore);
    }

    protected Response(Parcel in) {
        this.venueResult = ((VenueResult) in.readValue((VenueResult.class.getClassLoader())));
        this.listResult = ((ListResult) in.readValue((ListResult.class.getClassLoader())));
        this.btownListsResult = ((BtownListsResult) in.readValue((BtownListsResult.class.getClassLoader())));
        this.photos = ((Photos) in.readValue((Photos.class.getClassLoader())));
        this.menu = ((Menu) in.readValue((Menu.class.getClassLoader())));
        this.tips = ((GroupsTip) in.readValue((GroupsTip.class.getClassLoader())));
        in.readList(this.explore, (GroupExplore.class.getClassLoader()));
    }

    public int describeContents() {
        return 0;
    }

    @SuppressWarnings({"unchecked"})
    public final static Creator<Response> CREATOR = new Creator<Response>() {
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        public Response[] newArray(int size) {
            return (new Response[size]);
        }

    };

    private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
        throw new java.io.NotSerializableException( getClass().getName() );
    }

    private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        throw new java.io.NotSerializableException( getClass().getName() );
    }
}
