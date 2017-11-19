
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HighlightTextColor implements Serializable, Parcelable
{

    @SerializedName("photoId")
    @Expose
    private String photoId;
    @SerializedName("value")
    @Expose
    private int value;


    public final static Creator<HighlightTextColor> CREATOR = new Creator<HighlightTextColor>() {


        @SuppressWarnings({
            "unchecked"
        })
        public HighlightTextColor createFromParcel(Parcel in) {
            return new HighlightTextColor(in);
        }

        public HighlightTextColor[] newArray(int size) {
            return (new HighlightTextColor[size]);
        }

    }
    ;
    private final static long serialVersionUID = 1514387280402998990L;

    protected HighlightTextColor(Parcel in) {
        this.photoId = ((String) in.readValue((String.class.getClassLoader())));
        this.value = ((int) in.readValue((int.class.getClassLoader())));
    }

    public HighlightTextColor() {
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(photoId);
        dest.writeValue(value);
    }

    public int describeContents() {
        return  0;
    }

}
