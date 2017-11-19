
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Price implements Serializable, Parcelable {

    @SerializedName("tier")
    @Expose
    private int tier;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("currency")
    @Expose
    private String currency;


    public final static Creator<Price> CREATOR = new Creator<Price>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Price createFromParcel(Parcel in) {
            return new Price(in);
        }

        public Price[] newArray(int size) {
            return (new Price[size]);
        }

    };
    private final static long serialVersionUID = -7592529410050123805L;

    protected Price(Parcel in) {
        this.tier = ((int) in.readValue((int.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.currency = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Price() {
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(tier);
        dest.writeValue(message);
        dest.writeValue(currency);
    }

    public int describeContents() {
        return 0;
    }

}
