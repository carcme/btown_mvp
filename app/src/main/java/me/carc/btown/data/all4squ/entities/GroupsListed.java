
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupsListed implements Serializable, Parcelable {

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
    private ArrayList<ItemsGroupsListed> itemsGroupsListed = new ArrayList<>();


    public final static Creator<GroupsListed> CREATOR = new Creator<GroupsListed>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GroupsListed createFromParcel(Parcel in) {
            return new GroupsListed(in);
        }

        public GroupsListed[] newArray(int size) {
            return (new GroupsListed[size]);
        }

    };
    private final static long serialVersionUID = 3568242673923188297L;

    protected GroupsListed(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsGroupsListed, (ItemsGroupsListed.class.getClassLoader()));
    }

    public GroupsListed() {
    }

    public String getType() {
        return type;
    }
    public String getName() { return name; }
    public int getCount() { return count; }
    public ArrayList<ItemsGroupsListed> getItemsGroupsListed() { return itemsGroupsListed; }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(name);
        dest.writeValue(count);
        dest.writeList(itemsGroupsListed);
    }

    public int describeContents() {
        return 0;
    }

}
