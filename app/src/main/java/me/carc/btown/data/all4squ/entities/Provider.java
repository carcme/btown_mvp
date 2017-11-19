package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 10/11/2017.
 */

public class Provider implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("attributionImage")
    @Expose
    private String attributionImage;
    @SerializedName("attributionLink")
    @Expose
    private String attributionLink;
    @SerializedName("attributionText")
    @Expose
    private String attributionText;

    public String getName() { return name; }
    public String getAttributionImage() { return attributionImage; }
    public String getAttributionLink() { return attributionLink; }
    public String getAttributionText() { return attributionText; }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.attributionImage);
        dest.writeString(this.attributionLink);
        dest.writeString(this.attributionText);
    }

    public Provider() {
    }

    protected Provider(Parcel in) {
        this.name = in.readString();
        this.attributionImage = in.readString();
        this.attributionLink = in.readString();
        this.attributionText = in.readString();
    }

    public static final Parcelable.Creator<Provider> CREATOR = new Parcelable.Creator<Provider>() {
        public Provider createFromParcel(Parcel source) {
            return new Provider(source);
        }

        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
}
