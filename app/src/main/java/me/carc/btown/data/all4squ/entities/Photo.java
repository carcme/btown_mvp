
package me.carc.btown.data.all4squ.entities;

import android.arch.persistence.room.Embedded;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private String photoId;
    @SerializedName("createdAt")
    @Expose
    private int createdAt;
    @SerializedName("source")
    @Expose
    @Embedded
    private Source source;
    @SerializedName("prefix")
    @Expose
    private String photoPrefix;
    @SerializedName("suffix")
    @Expose
    private String photoSuffix;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("visibility")
    @Expose
    private String visibility;


    private final static long serialVersionUID = 7406580941284672028L;

    protected Photo(Parcel in) {
        this.photoId = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((int) in.readValue((int.class.getClassLoader())));
        this.source = ((Source) in.readValue((Source.class.getClassLoader())));
        this.photoPrefix = ((String) in.readValue((String.class.getClassLoader())));
        this.photoSuffix = ((String) in.readValue((String.class.getClassLoader())));
        this.width = ((int) in.readValue((int.class.getClassLoader())));
        this.height = ((int) in.readValue((int.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.visibility = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Photo() {
    }


    public String getPhotoId() { return photoId; }
    public String getPhotoPrefix() { return photoPrefix; }
    public String getPhotoSuffix() { return photoSuffix; }

    public String getId() { return photoId; }
    public int getCreatedAt() { return createdAt; }
    public Source getSource() { return source; }
    public String getPrefix() { return photoPrefix; }
    public String getSuffix() { return photoSuffix; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public User getUser() { return user; }
    public String getVisibility() { return visibility; }


    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void setPhotoPrefix(String photoPrefix) {
        this.photoPrefix = photoPrefix;
    }

    public void setPhotoSuffix(String photoSuffix) {
        this.photoSuffix = photoSuffix;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(photoId);
        dest.writeValue(createdAt);
        dest.writeValue(source);
        dest.writeValue(photoPrefix);
        dest.writeValue(photoSuffix);
        dest.writeValue(width);
        dest.writeValue(height);
        dest.writeValue(user);
        dest.writeValue(visibility);
    }

    public int describeContents() {
        return 0;
    }

    @SuppressWarnings({"unchecked"})
    public final static Creator<Photo> CREATOR = new Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }
        public Photo[] newArray(int size) {
            return (new Photo[size]);
        }
    };
}
