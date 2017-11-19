
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Colors implements Serializable, Parcelable
{

    @SerializedName("highlightColor")
    @Expose
    private HighlightColor highlightColor;
    @SerializedName("highlightTextColor")
    @Expose
    private HighlightTextColor highlightTextColor;
    @SerializedName("algoVersion")
    @Expose
    private int algoVersion;
    public final static Creator<Colors> CREATOR = new Creator<Colors>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Colors createFromParcel(Parcel in) {
            return new Colors(in);
        }

        public Colors[] newArray(int size) {
            return (new Colors[size]);
        }

    }
    ;
    private final static long serialVersionUID = -8129714536250503174L;

    protected Colors(Parcel in) {
        this.highlightColor = ((HighlightColor) in.readValue((HighlightColor.class.getClassLoader())));
        this.highlightTextColor = ((HighlightTextColor) in.readValue((HighlightTextColor.class.getClassLoader())));
        this.algoVersion = ((int) in.readValue((int.class.getClassLoader())));
    }

    public Colors() {
    }

    public HighlightColor getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(HighlightColor highlightColor) {
        this.highlightColor = highlightColor;
    }

    public HighlightTextColor getHighlightTextColor() {
        return highlightTextColor;
    }

    public void setHighlightTextColor(HighlightTextColor highlightTextColor) {
        this.highlightTextColor = highlightTextColor;
    }

    public int getAlgoVersion() {
        return algoVersion;
    }

    public void setAlgoVersion(int algoVersion) {
        this.algoVersion = algoVersion;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(highlightColor);
        dest.writeValue(highlightTextColor);
        dest.writeValue(algoVersion);
    }

    public int describeContents() {
        return  0;
    }

}
