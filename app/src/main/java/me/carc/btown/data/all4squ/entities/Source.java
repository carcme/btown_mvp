
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Source implements Serializable, Parcelable
{

    @SerializedName("name")
    @Expose
    private String srcName;
    @SerializedName("url")
    @Expose
    private String srcUrl;



    public final static Creator<Source> CREATOR = new Creator<Source>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        public Source[] newArray(int size) {
            return (new Source[size]);
        }

    }
    ;
    private final static long serialVersionUID = 2095371208515446432L;

    protected Source(Parcel in) {
        this.srcName = ((String) in.readValue((String.class.getClassLoader())));
        this.srcUrl = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Source() {
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(srcName);
        dest.writeValue(srcUrl);
    }

    public int describeContents() {
        return  0;
    }

}
