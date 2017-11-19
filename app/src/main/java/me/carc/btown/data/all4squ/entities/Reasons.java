
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Reasons implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<Reason> itemsReasons = new ArrayList<>();


    public int getCount() {
        return count;
    }
    public ArrayList<Reason> getItemsReasons() { return itemsReasons; }




    public final static Creator<Reasons> CREATOR = new Creator<Reasons>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Reasons createFromParcel(Parcel in) {
            return new Reasons(in);
        }

        public Reasons[] newArray(int size) {
            return (new Reasons[size]);
        }

    };
    private final static long serialVersionUID = 5416897323139413842L;

    protected Reasons(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsReasons, (Reason.class.getClassLoader()));
    }

    public Reasons() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(itemsReasons);
    }

    public int describeContents() {
        return 0;
    }

}
