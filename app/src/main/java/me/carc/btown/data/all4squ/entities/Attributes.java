
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Attributes implements Serializable, Parcelable {

    @SerializedName("groups")
    @Expose
    private ArrayList<GroupsAttribute> groupsAttributes = new ArrayList<>();


    public final static Creator<Attributes> CREATOR = new Creator<Attributes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Attributes createFromParcel(Parcel in) {
            return new Attributes(in);
        }

        public Attributes[] newArray(int size) {
            return (new Attributes[size]);
        }

    };
    private final static long serialVersionUID = -5327837503084116868L;

    protected Attributes(Parcel in) {
        in.readList(this.groupsAttributes, (GroupsAttribute.class.getClassLoader()));
    }

    public Attributes() {
    }

    public ArrayList<GroupsAttribute> getGroupsAttributes() {
        return groupsAttributes;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(groupsAttributes);
    }

    public int describeContents() {
        return 0;
    }

}
