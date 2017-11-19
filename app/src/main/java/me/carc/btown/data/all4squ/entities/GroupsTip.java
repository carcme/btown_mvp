
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupsTip implements Serializable, Parcelable {

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
    private ArrayList<ItemsGroupsTip> itemsGroupsTips = new ArrayList<>();

    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public int getCount() {
        return count;
    }
    public ArrayList<ItemsGroupsTip> getItemsGroupsTips() { return itemsGroupsTips; }




    public final static Creator<GroupsTip> CREATOR = new Creator<GroupsTip>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GroupsTip createFromParcel(Parcel in) {
            return new GroupsTip(in);
        }

        public GroupsTip[] newArray(int size) {
            return (new GroupsTip[size]);
        }

    };
    private final static long serialVersionUID = -4315725056963262591L;

    protected GroupsTip(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsGroupsTips, (ItemsGroupsTip.class.getClassLoader()));
    }

    public GroupsTip() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(name);
        dest.writeValue(count);
        dest.writeList(itemsGroupsTips);
    }

    public int describeContents() {
        return 0;
    }

}
