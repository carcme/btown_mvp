package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by bamptonm on 15/11/2017.
 */

public class ExploreItem implements Parcelable {

    @SerializedName("reasons")
    @Expose
    public Reasons reasons;
    @SerializedName("venue")
    @Expose
    public VenueResult venue;
    @SerializedName("tips")
    @Expose
    public ArrayList<ItemsGroupsTip> tips = null;
    @SerializedName("referralId")
    @Expose
    public String referralId;

    public Reasons getReasons() {
        return reasons;
    }

    public VenueResult getVenue() {
        return venue;
    }

    public ArrayList<ItemsGroupsTip> getTips() {
        return tips;
    }

    public String getReferralId() {
        return referralId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.reasons, 0);
        dest.writeParcelable(this.venue, 0);
        dest.writeTypedList(tips);
        dest.writeString(this.referralId);
    }

    public ExploreItem() {
    }

    protected ExploreItem(Parcel in) {
        this.reasons = in.readParcelable(Reasons.class.getClassLoader());
        this.venue = in.readParcelable(VenueResult.class.getClassLoader());
        this.tips = in.createTypedArrayList(ItemsGroupsTip.CREATOR);
        this.referralId = in.readString();
    }

    public static final Parcelable.Creator<ExploreItem> CREATOR = new Parcelable.Creator<ExploreItem>() {
        public ExploreItem createFromParcel(Parcel source) {
            return new ExploreItem(source);
        }

        public ExploreItem[] newArray(int size) {
            return new ExploreItem[size];
        }
    };
}

