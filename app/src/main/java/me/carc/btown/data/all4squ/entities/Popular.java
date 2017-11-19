
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Popular implements Serializable, Parcelable {

    @SerializedName("isOpen")
    @Expose
    private boolean isOpen;
    @SerializedName("isLocalHoliday")
    @Expose
    private boolean isLocalHoliday;
    @SerializedName("timeframes")
    @Expose
    private ArrayList<Timeframe> timeframes = new ArrayList<>();


    public final static Creator<Popular> CREATOR = new Creator<Popular>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Popular createFromParcel(Parcel in) {
            return new Popular(in);
        }

        public Popular[] newArray(int size) {
            return (new Popular[size]);
        }

    };
    private final static long serialVersionUID = 4368695905859026418L;

    protected Popular(Parcel in) {
        this.isOpen = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.isLocalHoliday = ((boolean) in.readValue((boolean.class.getClassLoader())));
        in.readList(this.timeframes, (Timeframe.class.getClassLoader()));
    }

    public Popular() {
    }

    public boolean isIsOpen() {
        return isOpen;
    }

    public boolean isIsLocalHoliday() {
        return isLocalHoliday;
    }

    public ArrayList<Timeframe> getTimeframes() {
        return timeframes;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(isOpen);
        dest.writeValue(isLocalHoliday);
        dest.writeList(timeframes);
    }

    public int describeContents() {
        return 0;
    }

}
