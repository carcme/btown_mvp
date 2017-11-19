
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeenHere implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("unconfirmedCount")
    @Expose
    private int unconfirmedCount;
    @SerializedName("marked")
    @Expose
    private boolean marked;
    @SerializedName("lastCheckinExpiredAt")
    @Expose
    private int lastCheckinExpiredAt;


    public final static Creator<BeenHere> CREATOR = new Creator<BeenHere>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BeenHere createFromParcel(Parcel in) {
            return new BeenHere(in);
        }

        public BeenHere[] newArray(int size) {
            return (new BeenHere[size]);
        }

    };
    private final static long serialVersionUID = 3849984526719898578L;

    protected BeenHere(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        this.unconfirmedCount = ((int) in.readValue((int.class.getClassLoader())));
        this.marked = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.lastCheckinExpiredAt = ((int) in.readValue((int.class.getClassLoader())));
    }

    public BeenHere() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getUnconfirmedCount() {
        return unconfirmedCount;
    }

    public void setUnconfirmedCount(int unconfirmedCount) {
        this.unconfirmedCount = unconfirmedCount;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public int getLastCheckinExpiredAt() {
        return lastCheckinExpiredAt;
    }

    public void setLastCheckinExpiredAt(int lastCheckinExpiredAt) {
        this.lastCheckinExpiredAt = lastCheckinExpiredAt;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeValue(unconfirmedCount);
        dest.writeValue(marked);
        dest.writeValue(lastCheckinExpiredAt);
    }

    public int describeContents() {
        return 0;
    }

}
