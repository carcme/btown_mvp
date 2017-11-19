
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupsAttribute implements Serializable, Parcelable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<ItemsAttribute> itemsAttributes = new ArrayList<>();


    public final static Creator<GroupsAttribute> CREATOR = new Creator<GroupsAttribute>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GroupsAttribute createFromParcel(Parcel in) {
            return new GroupsAttribute(in);
        }

        public GroupsAttribute[] newArray(int size) {
            return (new GroupsAttribute[size]);
        }

    };
    private final static long serialVersionUID = -5567569191089076210L;

    protected GroupsAttribute(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.summary = ((String) in.readValue((String.class.getClassLoader())));
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsAttributes, (ItemsAttribute.class.getClassLoader()));
    }

    public GroupsAttribute() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ItemsAttribute> getItemsAttributes() {
        return itemsAttributes;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(name);
        dest.writeValue(summary);
        dest.writeValue(count);
        dest.writeList(itemsAttributes);
    }

    public int describeContents() {
        return 0;
    }

}
