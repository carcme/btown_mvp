package me.carc.btown.extras.messaging;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Firebase chat message
 */
@Keep
public class CommentMessage implements Parcelable, Serializable {

    private String id;
    private String text;
    private String name;
    private long date;
    private String photoUrl;


    // EMPTY CONSTRUCTOR REQUIRED TO UPLOAD TO FIREBASE
    public CommentMessage() {
    }


    public CommentMessage(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        setDate(Calendar.getInstance().getTimeInMillis());
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public static Creator<CommentMessage> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeString(this.name);
        dest.writeLong(this.date);
        dest.writeString(this.photoUrl);
    }

    protected CommentMessage(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.name = in.readString();
        this.date = in.readLong();
        this.photoUrl = in.readString();
    }

    public static final Creator<CommentMessage> CREATOR = new Creator<CommentMessage>() {
        public CommentMessage createFromParcel(Parcel source) {
            return new CommentMessage(source);
        }

        public CommentMessage[] newArray(int size) {
            return new CommentMessage[size];
        }
    };
}
