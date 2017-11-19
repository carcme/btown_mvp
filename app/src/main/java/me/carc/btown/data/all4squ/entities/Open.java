
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Open implements Serializable, Parcelable {

    @SerializedName("renderedTime")
    @Expose
    private String renderedTime;

    public String getRenderedTime() { return renderedTime; }
    public void setRenderedTime(String renderedTime) { this.renderedTime = renderedTime; }


    protected Open(Parcel in) {
        this.renderedTime = ((String) in.readValue((String.class.getClassLoader())));
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(renderedTime);
    }

    @SuppressWarnings("unchecked")
    public final static Creator<Open> CREATOR = new Creator<Open>() {
        public Open createFromParcel(Parcel in) {
            return new Open(in);
        }

        public Open[] newArray(int size) {
            return (new Open[size]);
        }
    };

    private final static long serialVersionUID = 4190153358564703797L;


    public Open() {
    }


    public int describeContents() {
        return 0;
    }

}
