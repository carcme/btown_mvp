
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Likes implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("summary")
    @Expose
    private String summary;


    public final static Creator<Likes> CREATOR = new Creator<Likes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Likes createFromParcel(Parcel in) {
            return new Likes(in);
        }

        public Likes[] newArray(int size) {
            return (new Likes[size]);
        }

    };
    private final static long serialVersionUID = 5090836006128350037L;

    protected Likes(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        this.summary = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Likes() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeValue(summary);
    }

    public int describeContents() {
        return 0;
    }

}
