
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Followers implements Serializable, Parcelable
{

    @SerializedName("count")
    @Expose
    private int count;
    public final static Creator<Followers> CREATOR = new Creator<Followers>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Followers createFromParcel(Parcel in) {
            return new Followers(in);
        }

        public Followers[] newArray(int size) {
            return (new Followers[size]);
        }

    }
    ;
    private final static long serialVersionUID = 8342511610580618502L;

    protected Followers(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
    }

    public Followers() {
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
