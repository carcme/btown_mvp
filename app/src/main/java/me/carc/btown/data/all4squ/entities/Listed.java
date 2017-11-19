
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Listed implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("groups")
    @Expose
    private ArrayList<GroupsListed> groupsListed = new ArrayList<>();


    public final static Creator<Listed> CREATOR = new Creator<Listed>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Listed createFromParcel(Parcel in) {
            return new Listed(in);
        }

        public Listed[] newArray(int size) {
            return (new Listed[size]);
        }

    };
    private final static long serialVersionUID = -1919375601300981833L;

    protected Listed(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.groupsListed, (GroupsListed.class.getClassLoader()));
    }

    public Listed() {
    }

    public int getCount() {
        return count;
    }

    public ArrayList<GroupsListed> getGroupsListed() {
        return groupsListed;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(groupsListed);
    }

    public int describeContents() {
        return 0;
    }

}
