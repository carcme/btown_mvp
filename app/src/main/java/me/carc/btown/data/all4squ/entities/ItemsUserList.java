
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemsUserList implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("placesSummary")
    @Expose
    private String placesSummary;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("editable")
    @Expose
    private boolean editable;
    @SerializedName("public")
    @Expose
    private boolean publicRename;
    @SerializedName("collaborative")
    @Expose
    private boolean collaborative;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("canonicalUrl")
    @Expose
    private String canonicalUrl;
    @SerializedName("createdAt")
    @Expose
    private int createdAt;
    @SerializedName("updatedAt")
    @Expose
    private int updatedAt;
    @SerializedName("photo")
    @Expose
    private Photo photo;
    @SerializedName("followers")
    @Expose
    private Followers followers;
    @SerializedName("listItems")
    @Expose
    private ListItemsCount listItems;


    public final static Creator<ItemsUserList> CREATOR = new Creator<ItemsUserList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ItemsUserList createFromParcel(Parcel in) {
            return new ItemsUserList(in);
        }

        public ItemsUserList[] newArray(int size) {
            return (new ItemsUserList[size]);
        }

    };
    private final static long serialVersionUID = 1971064858425245302L;

    protected ItemsUserList(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.placesSummary = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.editable = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.publicRename = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.collaborative = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.canonicalUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((int) in.readValue((int.class.getClassLoader())));
        this.updatedAt = ((int) in.readValue((int.class.getClassLoader())));
        this.photo = ((Photo) in.readValue((Photo.class.getClassLoader())));
        this.followers = ((Followers) in.readValue((Followers.class.getClassLoader())));
        this.listItems = ((ListItemsCount) in.readValue((ListItemsCount.class.getClassLoader())));
    }

    public ItemsUserList() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPlacesSummary() {
        return placesSummary;
    }

    public User getUser() {
        return user;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isPublicRename() {
        return publicRename;
    }

    public boolean isCollaborative() {
        return collaborative;
    }

    public String getUrl() {
        return url;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Followers getFollowers() {
        return followers;
    }

    public ListItemsCount getListItems() {
        return listItems;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(description);
        dest.writeValue(placesSummary);
        dest.writeValue(user);
        dest.writeValue(editable);
        dest.writeValue(publicRename);
        dest.writeValue(collaborative);
        dest.writeValue(url);
        dest.writeValue(canonicalUrl);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
        dest.writeValue(photo);
        dest.writeValue(followers);
        dest.writeValue(listItems);
    }

    public int describeContents() {
        return 0;
    }

}
