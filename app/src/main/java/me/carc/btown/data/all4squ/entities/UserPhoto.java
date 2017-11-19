
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserPhoto implements Serializable, Parcelable
{

    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("suffix")
    @Expose
    private String suffix;
    @SerializedName("default")
    @Expose
    private boolean defaultValue;


    public String getPrefix() { return prefix;}
    public String getSuffix() {
        return suffix;
    }
    public boolean isDefaultRename() {
        return defaultValue;
    }



    public final static Creator<UserPhoto> CREATOR = new Creator<UserPhoto>() {


        @SuppressWarnings({
            "unchecked"
        })
        public UserPhoto createFromParcel(Parcel in) {
            return new UserPhoto(in);
        }

        public UserPhoto[] newArray(int size) {
            return (new UserPhoto[size]);
        }

    }
    ;
    private final static long serialVersionUID = 8841769610081354708L;

    protected UserPhoto(Parcel in) {
        this.prefix = ((String) in.readValue((String.class.getClassLoader())));
        this.suffix = ((String) in.readValue((String.class.getClassLoader())));
        this.defaultValue = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public UserPhoto() {
    }



    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(prefix);
        dest.writeValue(suffix);
        dest.writeValue(defaultValue);
    }

    public int describeContents() {
        return  0;
    }

}
