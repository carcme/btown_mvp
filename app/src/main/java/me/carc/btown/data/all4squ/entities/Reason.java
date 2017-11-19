package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 10/11/2017.
 */

public class Reason implements Parcelable {

    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("reasonName")
    @Expose
    private String reasonName;

    public String getSummary() { return summary; }
    public String getType() { return type; }
    public String getReasonName() { return reasonName; }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
        dest.writeString(this.type);
        dest.writeString(this.reasonName);
    }

    public Reason() {
    }

    protected Reason(Parcel in) {
        this.summary = in.readString();
        this.type = in.readString();
        this.reasonName = in.readString();
    }

    public static final Parcelable.Creator<Reason> CREATOR = new Parcelable.Creator<Reason>() {
        public Reason createFromParcel(Parcel source) {
            return new Reason(source);
        }

        public Reason[] newArray(int size) {
            return new Reason[size];
        }
    };
}
