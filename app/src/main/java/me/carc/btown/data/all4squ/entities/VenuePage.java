
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VenuePage implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private int id;



    public final static Creator<VenuePage> CREATOR = new Creator<VenuePage>() {


        @SuppressWarnings({
            "unchecked"
        })
        public VenuePage createFromParcel(Parcel in) {
            return new VenuePage(in);
        }

        public VenuePage[] newArray(int size) {
            return (new VenuePage[size]);
        }

    }
    ;
    private final static long serialVersionUID = 965790032214235973L;

    protected VenuePage(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
    }

    public VenuePage() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
    }

    public int describeContents() {
        return  0;
    }

}
