package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by bamptonm on 14/11/2017.
 */

public class Links implements Parcelable {

    @SerializedName("count")
    @Expose
    private Long count;
    @SerializedName("items")
    @Expose
    private ArrayList<LinkItem> items = null;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public ArrayList<LinkItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<LinkItem> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.count);
        dest.writeTypedList(items);
    }

    public Links() {
    }

    protected Links(Parcel in) {
        this.count = (Long) in.readValue(Long.class.getClassLoader());
        this.items = in.createTypedArrayList(LinkItem.CREATOR);
    }

    public static final Parcelable.Creator<Links> CREATOR = new Parcelable.Creator<Links>() {
        public Links createFromParcel(Parcel source) {
            return new Links(source);
        }

        public Links[] newArray(int size) {
            return new Links[size];
        }
    };
}
