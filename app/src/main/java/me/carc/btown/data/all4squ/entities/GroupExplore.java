package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by bamptonm on 15/11/2017.
 */

public class GroupExplore implements Parcelable {
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("items")
    @Expose
    public ArrayList<ExploreItem> items = null;

    public String getType() { return type; }
    public String getName() {return name;    }
    public ArrayList<ExploreItem> getItems() {return items;}



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeList(this.items);
    }

    public GroupExplore() {
    }

    protected GroupExplore(Parcel in) {
        this.type = in.readString();
        this.name = in.readString();
        this.items = new ArrayList<ExploreItem>();
        in.readList(this.items, ExploreItem.class.getClassLoader());
    }

    public static final Parcelable.Creator<GroupExplore> CREATOR = new Parcelable.Creator<GroupExplore>() {
        public GroupExplore createFromParcel(Parcel source) {
            return new GroupExplore(source);
        }

        public GroupExplore[] newArray(int size) {
            return new GroupExplore[size];
        }
    };
}