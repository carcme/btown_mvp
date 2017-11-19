
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupsList implements Serializable, Parcelable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<ItemsUserList> itemsUserLists = new ArrayList<>();


    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public int getCount() {
        return count;
    }
    public ArrayList<ItemsUserList> getItemsUserLists() {
        return itemsUserLists;
    }



    public final static Creator<GroupsList> CREATOR = new Creator<GroupsList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GroupsList createFromParcel(Parcel in) {
            return new GroupsList(in);
        }

        public GroupsList[] newArray(int size) {
            return (new GroupsList[size]);
        }

    };
    private final static long serialVersionUID = 3084104034834325650L;

    protected GroupsList(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsUserLists, (ItemsUserList.class.getClassLoader()));
    }

    public GroupsList() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(name);
        dest.writeValue(count);
        dest.writeList(itemsUserLists);
    }

    public int describeContents() {
        return 0;
    }

}
