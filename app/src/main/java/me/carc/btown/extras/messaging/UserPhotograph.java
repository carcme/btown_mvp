package me.carc.btown.extras.messaging;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Firebase struct for user images
 */
@Keep
public class UserPhotograph implements Parcelable, Serializable {

    private String uId;

    private String photoUrl;
    private long timestamp;
    private String displayName;
    private String photoLocation;
    private String imageDownloadUrl;

    private String userName;
    private String userIcon;
    private String email;


    // EMPTY CONSTRUCTOR REQUIRED TO UPLOAD TO FIREBASE
    public UserPhotograph() {
    }

    public UserPhotograph(String name, String email, String icon, String photoUrl) {
        this.userName = name;
        this.userIcon = icon;
        this.email = email;
        timestamp = Calendar.getInstance().getTimeInMillis();
        this.photoUrl = photoUrl;
    }

    public UserPhotograph(String text, String photoUrl) {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
        this.photoUrl = photoUrl;
    }

    public String getuId() {
        return uId;
    }
    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoLocation() { return photoLocation; }
    public void setPhotoLocation(String photoLocation) { this.photoLocation = photoLocation; }

    public String getDisplayName() { return displayName; }

    public void setDisplayName(String photoName) { this.displayName = photoName; }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() { return userName; }

    public void setUserName(String displayName) {
        this.userName = displayName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userPhotoUrl) {
        this.userIcon = userPhotoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageDownloadUrl() {
        return imageDownloadUrl;
    }

    public void setImageDownloadUrl(String imageDownloadUrl) {
        this.imageDownloadUrl = imageDownloadUrl;
    }

    public static Creator<UserPhotograph> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uId);
        dest.writeString(this.photoUrl);
        dest.writeLong(this.timestamp);
        dest.writeString(this.displayName);
        dest.writeString(this.photoLocation);
        dest.writeString(this.imageDownloadUrl);
        dest.writeString(this.userName);
        dest.writeString(this.userIcon);
        dest.writeString(this.email);
    }

    protected UserPhotograph(Parcel in) {
        this.uId = in.readString();
        this.photoUrl = in.readString();
        this.timestamp = in.readLong();
        this.displayName = in.readString();
        this.photoLocation = in.readString();
        this.imageDownloadUrl = in.readString();
        this.userName = in.readString();
        this.userIcon = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<UserPhotograph> CREATOR = new Parcelable.Creator<UserPhotograph>() {
        public UserPhotograph createFromParcel(Parcel source) {
            return new UserPhotograph(source);
        }

        public UserPhotograph[] newArray(int size) {
            return new UserPhotograph[size];
        }
    };
}
