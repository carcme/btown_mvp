package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 14/11/2017.
 */

public class LinkItem implements Parcelable {

    @SerializedName("url")
    @Expose
    private String linkUrl;

    public String getUrl() {
        return linkUrl;
    }

    public void setUrl(String url) {
        this.linkUrl = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.linkUrl);
    }

    public LinkItem() {
    }

    protected LinkItem(Parcel in) {
        this.linkUrl = in.readString();
    }

    public static final Parcelable.Creator<LinkItem> CREATOR = new Parcelable.Creator<LinkItem>() {
        public LinkItem createFromParcel(Parcel source) {
            return new LinkItem(source);
        }

        public LinkItem[] newArray(int size) {
            return new LinkItem[size];
        }
    };
}