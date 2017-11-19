
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupsPhoto implements Serializable, Parcelable {

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
    private ArrayList<Photo> itemsPhotos = new ArrayList<>();


    public final static Creator<GroupsPhoto> CREATOR = new Creator<GroupsPhoto>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GroupsPhoto createFromParcel(Parcel in) {
            return new GroupsPhoto(in);
        }

        public GroupsPhoto[] newArray(int size) {
            return (new GroupsPhoto[size]);
        }

    };
    private final static long serialVersionUID = -4777230382127093125L;

    protected GroupsPhoto(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsPhotos, (Photo.class.getClassLoader()));
    }

    public GroupsPhoto() {
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Photo> getItemsPhotos() {
        return itemsPhotos;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(name);
        dest.writeValue(count);
        dest.writeList(itemsPhotos);
    }

    public int describeContents() {
        return 0;
    }

}
