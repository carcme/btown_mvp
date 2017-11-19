
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Contact implements Serializable, Parcelable
{

    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("formattedPhone")
    @Expose
    private String formattedPhone;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("instagram")
    @Expose
    private String instagram;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("facebookUsername")
    @Expose
    private String facebookUsername;
    @SerializedName("facebookName")
    @Expose
    private String facebookName;



    public final static Creator<Contact> CREATOR = new Creator<Contact>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return (new Contact[size]);
        }

    }
    ;
    private final static long serialVersionUID = -7919694644480186613L;

    public Contact() {
    }

    public String getPhone() {
        return phone;
    }
    public String getEmail() { return email; }
    public String getFormattedPhone() { return formattedPhone; }
    public String getTwitter() {
        return twitter;
    }
    public String getInstagram() {
        return instagram;
    }
    public String getFacebook() {
        return facebook;
    }
    public String getFacebookUsername() {
        return facebookUsername;
    }
    public String getFacebookName() {
        return facebookName;
    }


    protected Contact(Parcel in) {
        this.phone = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.formattedPhone = ((String) in.readValue((String.class.getClassLoader())));
        this.twitter = ((String) in.readValue((String.class.getClassLoader())));
        this.instagram = ((String) in.readValue((String.class.getClassLoader())));
        this.facebook = ((String) in.readValue((String.class.getClassLoader())));
        this.facebookUsername = ((String) in.readValue((String.class.getClassLoader())));
        this.facebookName = ((String) in.readValue((String.class.getClassLoader())));
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(phone);
        dest.writeValue(email);
        dest.writeValue(formattedPhone);
        dest.writeValue(twitter);
        dest.writeValue(instagram);
        dest.writeValue(this.facebook);
        dest.writeValue(facebookUsername);
        dest.writeValue(facebookName);
    }

    public int describeContents() {
        return  0;
    }

}
