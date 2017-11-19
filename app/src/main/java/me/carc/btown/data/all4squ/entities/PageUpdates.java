
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PageUpdates implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<Object> itemsPageUpdates = new ArrayList<>();


    public final static Creator<PageUpdates> CREATOR = new Creator<PageUpdates>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PageUpdates createFromParcel(Parcel in) {
            return new PageUpdates(in);
        }

        public PageUpdates[] newArray(int size) {
            return (new PageUpdates[size]);
        }

    };
    private final static long serialVersionUID = 1458681571476566019L;

    protected PageUpdates(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsPageUpdates, (Object.class.getClassLoader()));
    }

    public PageUpdates() {
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Object> getItemsPageUpdates() {
        return itemsPageUpdates;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(itemsPageUpdates);
    }

    public int describeContents() {
        return 0;
    }

}
