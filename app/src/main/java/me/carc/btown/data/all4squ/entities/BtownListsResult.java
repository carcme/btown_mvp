
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BtownListsResult implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("groups")
    @Expose
    private ArrayList<GroupsList> groupsLists = new ArrayList<>();


    public final static Creator<BtownListsResult> CREATOR = new Creator<BtownListsResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BtownListsResult createFromParcel(Parcel in) {
            return new BtownListsResult(in);
        }

        public BtownListsResult[] newArray(int size) {
            return (new BtownListsResult[size]);
        }

    };
    private final static long serialVersionUID = 1425577223591105573L;

    protected BtownListsResult(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.groupsLists, (GroupsList.class.getClassLoader()));
    }

    public BtownListsResult() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<GroupsList> getGroupsLists() {
        return groupsLists;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(groupsLists);
    }

    public int describeContents() {
        return 0;
    }

}
