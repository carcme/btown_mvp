package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 10/11/2017.
 */

public class Menu implements Parcelable {

    @SerializedName("provider")
    @Expose
    private Provider provider;

    public Provider getProvider() { return provider; }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.provider, 0);
    }

    public Menu() {
    }

    protected Menu(Parcel in) {
        this.provider = in.readParcelable(Provider.class.getClassLoader());
    }

    public static final Parcelable.Creator<Menu> CREATOR = new Parcelable.Creator<Menu>() {
        public Menu createFromParcel(Parcel source) {
            return new Menu(source);
        }

        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };
}
