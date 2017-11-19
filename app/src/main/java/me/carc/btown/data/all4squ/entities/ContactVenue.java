
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ContactVenue implements Serializable, Parcelable
{

    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("formattedPhone")
    @Expose
    private String formattedPhone;

    public final static Creator<ContactVenue> CREATOR = new Creator<ContactVenue>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ContactVenue createFromParcel(Parcel in) {
            return new ContactVenue(in);
        }

        public ContactVenue[] newArray(int size) {
            return (new ContactVenue[size]);
        }

    }
    ;
    private final static long serialVersionUID = -7919694644480186613L;

    public ContactVenue() {
    }

    public String getPhone() {
        return phone;
    }
    public String getFormattedPhone() {
        return formattedPhone;
    }


    protected ContactVenue(Parcel in) {
        this.phone = ((String) in.readValue((String.class.getClassLoader())));
        this.formattedPhone = ((String) in.readValue((String.class.getClassLoader())));
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(phone);
        dest.writeValue(formattedPhone);
    }

    public int describeContents() {
        return  0;
    }

}
