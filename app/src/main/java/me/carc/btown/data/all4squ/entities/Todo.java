
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Todo implements Serializable, Parcelable
{

    @SerializedName("count")
    @Expose
    private int count;



    public final static Creator<Todo> CREATOR = new Creator<Todo>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        public Todo[] newArray(int size) {
            return (new Todo[size]);
        }

    }
    ;
    private final static long serialVersionUID = -4446029831575996314L;

    protected Todo(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
    }

    public Todo() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
    }

    public int describeContents() {
        return  0;
    }

}
