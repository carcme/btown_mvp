
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Timeframe implements Serializable, Parcelable {

    @SerializedName("days")
    @Expose
    private String days;
    @SerializedName("includesToday")
    @Expose
    private boolean includesToday;
    @SerializedName("open")
    @Expose
    private ArrayList<Open> open = new ArrayList<>();
    @SerializedName("segments")
    @Expose
    private ArrayList<Object> segments = new ArrayList<>();


    public String getDays() {
        return days;
    }
    public boolean isIncludesToday() {
        return includesToday;
    }
    public ArrayList<Open> getOpen() { return open; }
    public ArrayList<Object> getSegments() {
        return segments;
    }
    public void setDays(String days) { this.days = days; }
    public void setIncludesToday(boolean includesToday) { this.includesToday = includesToday; }
    public void setOpen(ArrayList<Open> open) { this.open = open; }
    public void setSegments(ArrayList<Object> segments) { this.segments = segments; }

    public final static Creator<Timeframe> CREATOR = new Creator<Timeframe>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Timeframe createFromParcel(Parcel in) {
            return new Timeframe(in);
        }

        public Timeframe[] newArray(int size) {
            return (new Timeframe[size]);
        }

    };
    private final static long serialVersionUID = -5103516718677795500L;

    protected Timeframe(Parcel in) {
        this.days = ((String) in.readValue((String.class.getClassLoader())));
        this.includesToday = ((boolean) in.readValue((boolean.class.getClassLoader())));
        in.readList(this.open, (Open.class.getClassLoader()));
        in.readList(this.segments, (Object.class.getClassLoader()));
    }

    public Timeframe() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(days);
        dest.writeValue(includesToday);
        dest.writeList(open);
        dest.writeList(segments);
    }

    public int describeContents() {
        return 0;
    }

}
