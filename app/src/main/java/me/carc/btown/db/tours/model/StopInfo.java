package me.carc.btown.db.tours.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.common.Commons;


/**
 * Created by bamptonm on 16/10/2017.
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
@Keep
public class StopInfo implements Serializable, Parcelable {
    @SuppressWarnings("unused")
    @SerializedName("title")
    private String[] stopTitle;
    public String getStopTitle() {
        return Commons.buildStringFromArray(stopTitle);
    }

    @SuppressWarnings("unused")
    @SerializedName("teaser")
    private String[] teaser;
    public String[] getTeaser() { return teaser; }
    @SerializedName("teaser_title")
    private String teaserTitle;
    public String getTeaserTitle() {
        return teaserTitle;
    }

    @SuppressWarnings("unused")
    @SerializedName("history")
    private String[] history;
    public String[] getHistory() {
        return history;
    }
    @SerializedName("history_title")
    private String historyTitle;
    public String getHistoryTitle() {
        return historyTitle;
    }

    @SuppressWarnings("unused")
    @SerializedName("qi")
    private String[] qi;
    public String[] getQi() {
        return qi;
    }
    @SerializedName("qi_title")
    private String qiTitle;
    public String getQiTitle() {
        return qiTitle;
    }

    @SuppressWarnings("unused")
    @SerializedName("look_out")
    private String[] look_out;
    public String[] getLoookout() {
        return look_out;
    }
    @SerializedName("lookout_title")
    private String lookoutTitle;
    public String getLookoutTitle() {
        return lookoutTitle;
    }

    @SuppressWarnings("unused")
    @SerializedName("next_stop")
    private String[] next_stop;
    public String[] getNextStop() {
        return next_stop;
    }
    @SerializedName("next_stop_title")
    private String nextstopTitle;
    public String getNextStopTitle() {
        return nextstopTitle;
    }

    @SuppressWarnings("unused")
    @SerializedName("extra")
    private String[] extra;
    public String[] getExtra() {
        return extra;
    }
    @SuppressWarnings("unused")
    @SerializedName("extra_title")
    private String extraTitle;
    public String getExtraTitle() {
        return extraTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.stopTitle);
        dest.writeStringArray(this.teaser);
        dest.writeStringArray(this.history);
        dest.writeStringArray(this.qi);
        dest.writeStringArray(this.look_out);
        dest.writeStringArray(this.next_stop);
        dest.writeStringArray(this.extra);
        dest.writeString(this.extraTitle);
    }

    public StopInfo() {
    }

    protected StopInfo(Parcel in) {
        this.stopTitle = in.createStringArray();
        this.teaser = in.createStringArray();
        this.history = in.createStringArray();
        this.qi = in.createStringArray();
        this.look_out = in.createStringArray();
        this.next_stop = in.createStringArray();
        this.extra = in.createStringArray();
        this.extraTitle = in.readString();
    }

    public static final Creator<StopInfo> CREATOR = new Creator<StopInfo>() {
        public StopInfo createFromParcel(Parcel source) {
            return new StopInfo(source);
        }

        public StopInfo[] newArray(int size) {
            return new StopInfo[size];
        }
    };
}