
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Collaborators implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<Object> itemsCollaborators = new ArrayList<>();


    public final static Creator<Collaborators> CREATOR = new Creator<Collaborators>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Collaborators createFromParcel(Parcel in) {
            return new Collaborators(in);
        }

        public Collaborators[] newArray(int size) {
            return (new Collaborators[size]);
        }

    };
    private final static long serialVersionUID = -6201427224500991339L;

    protected Collaborators(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsCollaborators, (Object.class.getClassLoader()));
    }

    public Collaborators() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Object> getItemsCollaborators() {
        return itemsCollaborators;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(itemsCollaborators);
    }

    public int describeContents() {
        return 0;
    }

}
