package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 14/11/2017.
 */

public class PageInfo implements Parcelable {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("banner")
    @Expose
    private String banner;
    @SerializedName("links")
    @Expose
    private Links links;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.banner);
        dest.writeValue(this.links);
    }

    public PageInfo() {
    }

    protected PageInfo(Parcel in) {
        this.description = in.readString();
        this.banner = in.readString();
        this.links = ((Links) in.readValue((Links.class.getClassLoader())));
//        this.links = in.readParcelable(Links.class.getClassLoader());
    }

    public static final Parcelable.Creator<PageInfo> CREATOR = new Parcelable.Creator<PageInfo>() {
        public PageInfo createFromParcel(Parcel source) {
            return new PageInfo(source);
        }

        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };
}
