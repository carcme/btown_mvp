
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ListItems implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<ItemsListItem> itemsListItems = new ArrayList<>();


    public final static Creator<ListItems> CREATOR = new Creator<ListItems>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ListItems createFromParcel(Parcel in) {
            return new ListItems(in);
        }

        public ListItems[] newArray(int size) {
            return (new ListItems[size]);
        }

    };
    private final static long serialVersionUID = 8871085369782772177L;

    protected ListItems(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsListItems, (ItemsListItem.class.getClassLoader()));
    }

    public ListItems() {
    }

    public int getCount() {
        return count;
    }

    public ArrayList<ItemsListItem> getItemsListItems() {
        return itemsListItems;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(itemsListItems);
    }

    public int describeContents() {
        return 0;
    }

}
