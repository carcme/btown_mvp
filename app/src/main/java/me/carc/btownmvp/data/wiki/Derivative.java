package me.carc.btownmvp.data.wiki;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 25/09/2017.
 */
public class Derivative {
    @SuppressWarnings("unused") @SerializedName("src") @Nullable
    private String src;
    @SuppressWarnings("unused") @SerializedName("type") @Nullable private String type;
    @SuppressWarnings("unused") @SerializedName("title") @Nullable private String title;
    @SuppressWarnings("unused") @SerializedName("shorttitle") @Nullable private String shortTitle;
    @SuppressWarnings("unused") @SerializedName("width") @Nullable private long width;
    @SuppressWarnings("unused") @SerializedName("height") @Nullable private long height;
    @SuppressWarnings("unused") @SerializedName("bandwidth") @Nullable private long bandWidth;
    @SuppressWarnings("unused") @SerializedName("framerate") @Nullable private double frameRate;

    @Nullable
    public String getSrc() {
        return src;
    }

    @Nullable public String getType() {
        return type;
    }

    @Nullable public String getTitle() {
        return title;
    }

    @Nullable public String getShortTitle() {
        return shortTitle;
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    public long getBandWidth() {
        return bandWidth;
    }

    public double getFrameRate() {
        return frameRate;
    }
}
