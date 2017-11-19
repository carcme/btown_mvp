
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HighlightColor implements Serializable, Parcelable
{

    @SerializedName("photoId")
    @Expose
    private String photoId;
    @SerializedName("value")
    @Expose
    private int value;


    public String getPhotoId() {
        return photoId;
    }
    public int getValue() { return value; }


    public final static Creator<HighlightColor> CREATOR = new Creator<HighlightColor>() {


        @SuppressWarnings({
            "unchecked"
        })
        public HighlightColor createFromParcel(Parcel in) {
            return new HighlightColor(in);
        }

        public HighlightColor[] newArray(int size) {
            return (new HighlightColor[size]);
        }

    }
    ;
    private final static long serialVersionUID = -1147668744428025595L;

    protected HighlightColor(Parcel in) {
        this.photoId = ((String) in.readValue((String.class.getClassLoader())));
        this.value = ((int) in.readValue((int.class.getClassLoader())));
    }

    public HighlightColor() {
    }



    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(photoId);
        dest.writeValue(value);
    }

    public int describeContents() {
        return  0;
    }

}
