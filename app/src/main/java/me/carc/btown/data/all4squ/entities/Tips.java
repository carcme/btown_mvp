
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Tips implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("groups")
    @Expose
    private ArrayList<GroupsTip> groupsTips = new ArrayList<>();


    public final static Creator<Tips> CREATOR = new Creator<Tips>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Tips createFromParcel(Parcel in) {
            return new Tips(in);
        }

        public Tips[] newArray(int size) {
            return (new Tips[size]);
        }

    };
    private final static long serialVersionUID = 3617814011734354855L;

    protected Tips(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.groupsTips, (GroupsTip.class.getClassLoader()));
    }

    public Tips() {
    }

    public int getCount() {
        return count;
    }

    public ArrayList<GroupsTip> getGroupsTips() {
        return groupsTips;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(groupsTips);
    }

    public int describeContents() {
        return 0;
    }

}
