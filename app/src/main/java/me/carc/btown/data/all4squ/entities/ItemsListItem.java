
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemsListItem implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private int createdAt;
    @SerializedName("photo")
    @Expose
    private Photo photo;
    @SerializedName("venue")
    @Expose
    private VenueResult venue;
    @SerializedName("index")
    @Expose
    private int index;
    @SerializedName("isNew")
    @Expose
    private boolean isNew;



    public final static Creator<ItemsListItem> CREATOR = new Creator<ItemsListItem>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ItemsListItem createFromParcel(Parcel in) {
            return new ItemsListItem(in);
        }

        public ItemsListItem[] newArray(int size) {
            return (new ItemsListItem[size]);
        }

    }
    ;
    private final static long serialVersionUID = -5006165913250672637L;

    public ItemsListItem() {
    }


    public String getId() {
        return id;
    }
    public int getCreatedAt() {
        return createdAt;
    }
    public Photo getPhoto() { return photo; }
    public VenueResult getVenue() { return venue; }
    public int getIndex() {
        return index;
    }
    public boolean isIsNew() {
        return isNew;
    }


    protected ItemsListItem(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((int) in.readValue((int.class.getClassLoader())));
        this.photo = ((Photo) in.readValue((me.carc.btown.data.all4squ.entities.Photo.class.getClassLoader())));
        this.venue = ((VenueResult) in.readValue((me.carc.btown.data.all4squ.entities.VenueResult.class.getClassLoader())));
        this.index = ((int) in.readValue((int.class.getClassLoader())));
        this.isNew = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(createdAt);
        dest.writeValue(photo);
        dest.writeValue(venue);
        dest.writeValue(index);
        dest.writeValue(isNew);
    }

    public int describeContents() {
        return  0;
    }

}
