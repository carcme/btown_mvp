package me.carc.btown.extras.messaging;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Carc.me on 26.09.16.
 * <p/>
 * Firebase rating values holder
 */
public class FireBaseRating implements Parcelable, Serializable {

    private int cummalative;
    private float rating;

    public FireBaseRating() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FireBaseRating(int count, float score) {
        cummalative = count;
        rating = score;
    }

    public int getCummalative() {
        return cummalative;
    }

    public void setCummalative(int cummalative) {
        this.cummalative = cummalative;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cummalative);
        dest.writeFloat(this.rating);
    }

    protected FireBaseRating(Parcel in) {
        this.cummalative = in.readInt();
        this.rating = in.readFloat();
    }

    public static final Parcelable.Creator<FireBaseRating> CREATOR = new Parcelable.Creator<FireBaseRating>() {
        public FireBaseRating createFromParcel(Parcel source) {
            return new FireBaseRating(source);
        }

        public FireBaseRating[] newArray(int size) {
            return new FireBaseRating[size];
        }
    };
}
