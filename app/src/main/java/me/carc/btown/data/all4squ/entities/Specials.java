
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Specials implements Serializable, Parcelable
{

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<Object> itemsSpecials = new ArrayList<>();



    public final static Creator<Specials> CREATOR = new Creator<Specials>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Specials createFromParcel(Parcel in) {
            return new Specials(in);
        }

        public Specials[] newArray(int size) {
            return (new Specials[size]);
        }

    }
    ;
    private final static long serialVersionUID = 1801365143209572699L;

    protected Specials(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsSpecials, (Object.class.getClassLoader()));
    }

    public Specials() {
    }

    public int getCount() {
        return count;
    }
    public ArrayList<Object> getItemsSpecials() {
        return itemsSpecials;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(itemsSpecials);
    }

    public int describeContents() {
        return  0;
    }

}
