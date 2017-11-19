
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemsAttribute implements Serializable, Parcelable
{

    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("displayValue")
    @Expose
    private String displayValue;
    @SerializedName("priceTier")
    @Expose
    private int priceTier;

    public String getDisplayName() { return displayName; }
    public String getDisplayValue() {
        return displayValue;
    }
    public int getPriceTier() {
        return priceTier;
    }



    public final static Creator<ItemsAttribute> CREATOR = new Creator<ItemsAttribute>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ItemsAttribute createFromParcel(Parcel in) {
            return new ItemsAttribute(in);
        }

        public ItemsAttribute[] newArray(int size) {
            return (new ItemsAttribute[size]);
        }

    }
    ;
    private final static long serialVersionUID = -3074382429849705533L;

    protected ItemsAttribute(Parcel in) {
        this.displayName = ((String) in.readValue((String.class.getClassLoader())));
        this.displayValue = ((String) in.readValue((String.class.getClassLoader())));
        this.priceTier = ((int) in.readValue((int.class.getClassLoader())));
    }

    public ItemsAttribute() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(displayName);
        dest.writeValue(displayValue);
        dest.writeValue(priceTier);
    }

    public int describeContents() {
        return  0;
    }

}
