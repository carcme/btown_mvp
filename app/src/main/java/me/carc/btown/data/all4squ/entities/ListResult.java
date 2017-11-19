
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ListResult implements Serializable, Parcelable
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
    @SerializedName("public")
    @Expose
    private boolean publicVlaue;
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
    @SerializedName("visitedCount")
    @Expose
    private int visitedCount;
    @SerializedName("venueCount")
    @Expose
    private int venueCount;
    @SerializedName("categories")
    @Expose
    private Categories categories;
    @SerializedName("following")
    @Expose
    private boolean following;
    @SerializedName("followers")
    @Expose
    private Followers followers;
    @SerializedName("collaborators")
    @Expose
    private Collaborators collaborators;
    @SerializedName("sort")
    @Expose
    private String sort;
    @SerializedName("listItems")
    @Expose
    private ListItems listItems;
    @SerializedName("completedCount")
    @Expose
    private int completedCount;
    public final static Creator<ListResult> CREATOR = new Creator<ListResult>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ListResult createFromParcel(Parcel in) {
            return new ListResult(in);
        }

        public ListResult[] newArray(int size) {
            return (new ListResult[size]);
        }

    }
    ;
    private final static long serialVersionUID = 381665096098204027L;

    protected ListResult(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.editable = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.publicVlaue = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.collaborative = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.canonicalUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((int) in.readValue((int.class.getClassLoader())));
        this.updatedAt = ((int) in.readValue((int.class.getClassLoader())));
        this.photo = ((Photo) in.readValue((Photo.class.getClassLoader())));
        this.visitedCount = ((int) in.readValue((int.class.getClassLoader())));
        this.venueCount = ((int) in.readValue((int.class.getClassLoader())));
        this.categories = ((Categories) in.readValue((Categories.class.getClassLoader())));
        this.following = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.followers = ((Followers) in.readValue((Followers.class.getClassLoader())));
        this.collaborators = ((Collaborators) in.readValue((Collaborators.class.getClassLoader())));
        this.sort = ((String) in.readValue((String.class.getClassLoader())));
        this.listItems = ((ListItems) in.readValue((ListItems.class.getClassLoader())));
        this.completedCount = ((int) in.readValue((int.class.getClassLoader())));
    }

    public ListResult() {
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
    public String getType() {
        return type;
    }
    public User getUser() {
        return user;
    }
    public boolean isEditable() {
        return editable;
    }
    public boolean isPublicRename() {
        return publicVlaue;
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
    public int getVisitedCount() {
        return visitedCount;
    }
    public int getVenueCount() {
        return venueCount;
    }
    public Categories getCategories() {
        return categories;
    }
    public boolean isFollowing() {
        return following;
    }
    public Followers getFollowers() {
        return followers;
    }
    public Collaborators getCollaborators() {
        return collaborators;
    }
    public String getSort() {
        return sort;
    }
    public ListItems getListItems() { return listItems; }
    public int getCompletedCount() {
        return completedCount;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(description);
        dest.writeValue(type);
        dest.writeValue(user);
        dest.writeValue(editable);
        dest.writeValue(publicVlaue);
        dest.writeValue(collaborative);
        dest.writeValue(url);
        dest.writeValue(canonicalUrl);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
        dest.writeValue(photo);
        dest.writeValue(visitedCount);
        dest.writeValue(venueCount);
        dest.writeValue(categories);
        dest.writeValue(following);
        dest.writeValue(followers);
        dest.writeValue(collaborators);
        dest.writeValue(sort);
        dest.writeValue(listItems);
        dest.writeValue(completedCount);
    }

    public int describeContents() {
        return  0;
    }

}
