
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class HereNow implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("groups")
    @Expose
    private ArrayList<Object> groupsHereNow = new ArrayList<>();


    public final static Creator<HereNow> CREATOR = new Creator<HereNow>() {


        @SuppressWarnings({
                "unchecked"
        })
        public HereNow createFromParcel(Parcel in) {
            return new HereNow(in);
        }

        public HereNow[] newArray(int size) {
            return (new HereNow[size]);
        }

    };
    private final static long serialVersionUID = -1996553664823950843L;

    protected HereNow(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        this.summary = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.groupsHereNow, (Object.class.getClassLoader()));
    }

    public HereNow() {
    }

    public int getCount() {
        return count;
    }

    public String getSummary() {
        return summary;
    }

    public ArrayList<Object> getGroupsHereNow() {
        return groupsHereNow;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeValue(summary);
        dest.writeList(groupsHereNow);
    }

    public int describeContents() {
        return 0;
    }

}
