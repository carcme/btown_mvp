
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Icon implements Serializable, Parcelable
{

    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("suffix")
    @Expose
    private String suffix;



    public final static Creator<Icon> CREATOR = new Creator<Icon>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Icon createFromParcel(Parcel in) {
            return new Icon(in);
        }

        public Icon[] newArray(int size) {
            return (new Icon[size]);
        }

    }
    ;
    private final static long serialVersionUID = -6931097183716397327L;

    protected Icon(Parcel in) {
        this.prefix = ((String) in.readValue((String.class.getClassLoader())));
        this.suffix = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Icon() {
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(prefix);
        dest.writeValue(suffix);
    }

    public int describeContents() {
        return  0;
    }

}
