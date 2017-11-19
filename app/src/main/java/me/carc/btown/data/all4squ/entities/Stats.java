
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Stats implements Serializable, Parcelable
{

    @SerializedName("checkinsCount")
    @Expose
    private int checkinsCount;
    @SerializedName("usersCount")
    @Expose
    private int usersCount;
    @SerializedName("tipCount")
    @Expose
    private int tipCount;
    @SerializedName("visitsCount")
    @Expose
    private int visitsCount;


    public final static Creator<Stats> CREATOR = new Creator<Stats>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Stats createFromParcel(Parcel in) {
            return new Stats(in);
        }

        public Stats[] newArray(int size) {
            return (new Stats[size]);
        }

    }
    ;
    private final static long serialVersionUID = -3304668467419866559L;

    protected Stats(Parcel in) {
        this.checkinsCount = ((int) in.readValue((int.class.getClassLoader())));
        this.usersCount = ((int) in.readValue((int.class.getClassLoader())));
        this.tipCount = ((int) in.readValue((int.class.getClassLoader())));
        this.visitsCount = ((int) in.readValue((int.class.getClassLoader())));
    }

    public Stats() {
    }

    public int getCheckinsCount() {
        return checkinsCount;
    }

    public void setCheckinsCount(int checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public int getTipCount() {
        return tipCount;
    }

    public void setTipCount(int tipCount) {
        this.tipCount = tipCount;
    }

    public int getVisitsCount() {
        return visitsCount;
    }

    public void setVisitsCount(int visitsCount) {
        this.visitsCount = visitsCount;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(checkinsCount);
        dest.writeValue(usersCount);
        dest.writeValue(tipCount);
        dest.writeValue(visitsCount);
    }

    public int describeContents() {
        return  0;
    }

}
