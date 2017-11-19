package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 14/11/2017.
 */

public class Page implements Parcelable {

    @SerializedName("pageInfo")
    @Expose
    private PageInfo pageInfo;
    @SerializedName("user")
    @Expose
    private User user;

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.pageInfo);
        dest.writeParcelable(this.user, 0);
    }

    public Page() {
    }

    protected Page(Parcel in) {
        this.pageInfo = ((PageInfo) in.readValue((PageInfo.class.getClassLoader())));
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>() {
        public Page createFromParcel(Parcel source) {
            return new Page(source);
        }

        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}
