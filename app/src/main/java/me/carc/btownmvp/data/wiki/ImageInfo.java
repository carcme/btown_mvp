package me.carc.btownmvp.data.wiki;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 25/09/2017.
 */

public class ImageInfo {
    @SuppressWarnings("unused") private int size;
    @SuppressWarnings("unused") private int width;
    @SuppressWarnings("unused") private int height;
    @SuppressWarnings("unused") @SerializedName("thumburl") @Nullable
    private String thumbUrl;
    @SuppressWarnings("unused") @SerializedName("thumbwidth") private int thumbWidth;
    @SuppressWarnings("unused") @SerializedName("thumbheight") private int thumbHeight;
    @SuppressWarnings("unused") @SerializedName("url") @Nullable private String originalUrl;
    @SuppressWarnings("unused") @SerializedName("descriptionurl") @Nullable private String descriptionUrl;
    @SuppressWarnings("unused") @SerializedName("descriptionshorturl") @Nullable private String descriptionShortUrl;
    @SuppressWarnings("unused,NullableProblems") @SerializedName("mime") @NonNull
    private String mimeType = "*/*";
    @SuppressWarnings("unused") @SerializedName("extmetadata")@Nullable private ExtMetadata metadata;

    public int getSize() {
        return size;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Nullable public String getThumbUrl() {
        return thumbUrl;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    @Nullable public String getOriginalUrl() {
        return originalUrl;
    }

    @Nullable public String getDescriptionUrl() {
        return descriptionUrl;
    }

    @Nullable public String getDescriptionShortUrl() {
        return descriptionShortUrl;
    }

    @NonNull public String getMimeType() {
        return mimeType;
    }

    @Nullable public ExtMetadata getMetadata() {
        return metadata;
    }
}
