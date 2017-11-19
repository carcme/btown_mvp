
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Categories implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<ItemsCategory> itemsCategories = new ArrayList<>();


    public final static Creator<Categories> CREATOR = new Creator<Categories>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        public Categories[] newArray(int size) {
            return (new Categories[size]);
        }

    };
    private final static long serialVersionUID = 4814857890164635452L;

    protected Categories(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsCategories, (ItemsCategory.class.getClassLoader()));
    }

    public Categories() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<ItemsCategory> getItemsCategories() {
        return itemsCategories;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(itemsCategories);
    }

    public int describeContents() {
        return 0;
    }

}
