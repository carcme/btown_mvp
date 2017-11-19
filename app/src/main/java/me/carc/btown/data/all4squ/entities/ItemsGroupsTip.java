
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.carc.btown.common.C;

public class ItemsGroupsTip implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("canonicalUrl")
    @Expose
    private String canonicalUrl;
    @SerializedName("photo")
    @Expose
    private Photo photo;
    @SerializedName("photourl")
    @Expose
    private String photourl;
    @SerializedName("lang")
    @Expose
    private String lang = C.USER_LANGUAGE;
    @SerializedName("likes")
    @Expose
    private Likes likes;
    @SerializedName("logView")
    @Expose
    private boolean logView;
    @SerializedName("agreeCount")
    @Expose
    private int agreeCount;
    @SerializedName("disagreeCount")
    @Expose
    private int disagreeCount;
    @SerializedName("todo")
    @Expose
    private Todo todo;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("authorInteractionType")
    @Expose
    private String authorInteractionType;



    public final static Creator<ItemsGroupsTip> CREATOR = new Creator<ItemsGroupsTip>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ItemsGroupsTip createFromParcel(Parcel in) {
            return new ItemsGroupsTip(in);
        }

        public ItemsGroupsTip[] newArray(int size) {
            return (new ItemsGroupsTip[size]);
        }

    }
    ;
    private final static long serialVersionUID = -1289061876401222572L;

    protected ItemsGroupsTip(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((Long) in.readValue((Long.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.canonicalUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.photo = ((Photo) in.readValue((Photo.class.getClassLoader())));
        this.photourl = ((String) in.readValue((String.class.getClassLoader())));
        this.lang = ((String) in.readValue((String.class.getClassLoader())));
        this.likes = ((Likes) in.readValue((Likes.class.getClassLoader())));
        this.logView = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.agreeCount = ((int) in.readValue((int.class.getClassLoader())));
        this.disagreeCount = ((int) in.readValue((int.class.getClassLoader())));
        this.todo = ((Todo) in.readValue((Todo.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.authorInteractionType = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ItemsGroupsTip() {
    }


    public String getId() {
        return id;
    }
    public Long getCreatedAt() {
        return createdAt;
    }
    public String getText() {
        return text;
    }
    public String getType() {
        return type;
    }
    public String getCanonicalUrl() {
        return canonicalUrl;
    }
    public Photo getPhoto() {
        return photo;
    }
    public String getPhotourl() { return photourl; }
    public String getLang() {
        return lang;
    }
    public Likes getLikes() {
        return likes;
    }
    public boolean isLogView() {
        return logView;
    }
    public int getAgreeCount() {
        return agreeCount;
    }
    public int getDisagreeCount() {
        return disagreeCount;
    }
    public Todo getTodo() {
        return todo;
    }
    public User getUser() {
        return user;
    }
    public String getAuthorInteractionType() {
        return authorInteractionType;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(createdAt);
        dest.writeValue(text);
        dest.writeValue(type);
        dest.writeValue(canonicalUrl);
        dest.writeValue(photo);
        dest.writeValue(photourl);
        dest.writeValue(lang);
        dest.writeValue(likes);
        dest.writeValue(logView);
        dest.writeValue(agreeCount);
        dest.writeValue(disagreeCount);
        dest.writeValue(todo);
        dest.writeValue(user);
        dest.writeValue(authorInteractionType);
    }

    public int describeContents() {
        return  0;
    }

}
