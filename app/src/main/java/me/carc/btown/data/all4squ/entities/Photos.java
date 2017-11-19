
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Photos implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("groups")
    @Expose
    private ArrayList<GroupsPhoto> groupsPhotos = new ArrayList<>();
    @SerializedName("items")
    @Expose
    public ArrayList<Photo> photos = new ArrayList<>();


    public int getCount() {
        return count;
    }
    public ArrayList<GroupsPhoto> getGroupsPhotos() {
        return groupsPhotos;
    }
    public ArrayList<Photo> getPhotos() { return photos; }



    protected Photos(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.groupsPhotos, (GroupsPhoto.class.getClassLoader()));
        this.photos = in.createTypedArrayList(Photo.CREATOR);
    }

    public Photos() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(groupsPhotos);
        dest.writeTypedList(photos);
    }

    private final static long serialVersionUID = -8026180373242162776L;

    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public final static Creator<Photos> CREATOR = new Creator<Photos>() {
        public Photos createFromParcel(Parcel in) {
            return new Photos(in);
        }

        public Photos[] newArray(int size) {
            return (new Photos[size]);
        }
    };
}
