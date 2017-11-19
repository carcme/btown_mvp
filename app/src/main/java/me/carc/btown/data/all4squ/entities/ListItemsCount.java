
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ListItemsCount implements Serializable, Parcelable
{

    @SerializedName("count")
    @Expose
    private int count;



    public final static Creator<ListItemsCount> CREATOR = new Creator<ListItemsCount>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ListItemsCount createFromParcel(Parcel in) {
            return new ListItemsCount(in);
        }

        public ListItemsCount[] newArray(int size) {
            return (new ListItemsCount[size]);
        }

    }
    ;
    private final static long serialVersionUID = -5579536654585442393L;

    protected ListItemsCount(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
    }

    public ListItemsCount() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
    }

    public int describeContents() {
        return  0;
    }

}
