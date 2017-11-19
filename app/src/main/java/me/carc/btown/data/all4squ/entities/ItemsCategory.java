
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemsCategory implements Serializable, Parcelable
{

    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("count")
    @Expose
    private int count;


    public final static Creator<ItemsCategory> CREATOR = new Creator<ItemsCategory>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ItemsCategory createFromParcel(Parcel in) {
            return new ItemsCategory(in);
        }

        public ItemsCategory[] newArray(int size) {
            return (new ItemsCategory[size]);
        }

    }
    ;
    private final static long serialVersionUID = -8821646430295564719L;

    protected ItemsCategory(Parcel in) {
        this.category = ((Category) in.readValue((Category.class.getClassLoader())));
        this.count = ((int) in.readValue((int.class.getClassLoader())));
    }

    public ItemsCategory() {
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(category);
        dest.writeValue(count);
    }

    public int describeContents() {
        return  0;
    }

}
