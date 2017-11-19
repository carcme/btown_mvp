
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemsGroupsListed implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("editable")
    @Expose
    private boolean editable;
    @SerializedName("public_rename")
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
    @SerializedName("followers")
    @Expose
    private Followers followers;
    @SerializedName("listItems")
    @Expose
    private ListItems listItems;
    public final static Creator<ItemsGroupsListed> CREATOR = new Creator<ItemsGroupsListed>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ItemsGroupsListed createFromParcel(Parcel in) {
            return new ItemsGroupsListed(in);
        }

        public ItemsGroupsListed[] newArray(int size) {
            return (new ItemsGroupsListed[size]);
        }

    }
    ;
    private final static long serialVersionUID = -8979877831416380575L;

    protected ItemsGroupsListed(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.editable = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.publicRename = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.collaborative = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.canonicalUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((int) in.readValue((int.class.getClassLoader())));
        this.updatedAt = ((int) in.readValue((int.class.getClassLoader())));
        this.followers = ((Followers) in.readValue((Followers.class.getClassLoader())));
        this.listItems = ((ListItems) in.readValue((ListItems.class.getClassLoader())));
    }

    public ItemsGroupsListed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isPublicRename() {
        return publicRename;
    }

    public void setPublicRename(boolean publicRename) {
        this.publicRename = publicRename;
    }

    public boolean isCollaborative() {
        return collaborative;
    }

    public void setCollaborative(boolean collaborative) {
        this.collaborative = collaborative;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Followers getFollowers() {
        return followers;
    }

    public void setFollowers(Followers followers) {
        this.followers = followers;
    }

    public ListItems getListItems() {
        return listItems;
    }

    public void setListItems(ListItems listItems) {
        this.listItems = listItems;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(description);
        dest.writeValue(type);
        dest.writeValue(user);
        dest.writeValue(editable);
        dest.writeValue(publicRename);
        dest.writeValue(collaborative);
        dest.writeValue(url);
        dest.writeValue(canonicalUrl);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
        dest.writeValue(followers);
        dest.writeValue(listItems);
    }

    public int describeContents() {
        return  0;
    }

}
