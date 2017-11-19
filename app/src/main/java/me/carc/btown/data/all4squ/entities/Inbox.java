
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Inbox implements Serializable, Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private ArrayList<Object> itemsInbox = new ArrayList<>();


    public final static Creator<Inbox> CREATOR = new Creator<Inbox>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Inbox createFromParcel(Parcel in) {
            return new Inbox(in);
        }

        public Inbox[] newArray(int size) {
            return (new Inbox[size]);
        }

    };
    private final static long serialVersionUID = -4105831590072495653L;

    protected Inbox(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.itemsInbox, (Object.class.getClassLoader()));
    }

    public Inbox() {
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Object> getItemsInbox() {
        return itemsInbox;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(itemsInbox);
    }

    public int describeContents() {
        return 0;
    }

}
