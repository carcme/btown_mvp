
package me.carc.btown.data.all4squ.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Deprecated
public class BestPhoto implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private int createdAt;
    @SerializedName("source")
    @Expose
    private Source source;
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("suffix")
    @Expose
    private String suffix;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("visibility")
    @Expose
    private String visibility;



    public final static Creator<BestPhoto> CREATOR = new Creator<BestPhoto>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BestPhoto createFromParcel(Parcel in) {
            return new BestPhoto(in);
        }

        public BestPhoto[] newArray(int size) {
            return (new BestPhoto[size]);
        }

    };
    private final static long serialVersionUID = 7371214403447938532L;

    protected BestPhoto(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((int) in.readValue((int.class.getClassLoader())));
        this.source = ((Source) in.readValue((Source.class.getClassLoader())));
        this.prefix = ((String) in.readValue((String.class.getClassLoader())));
        this.suffix = ((String) in.readValue((String.class.getClassLoader())));
        this.width = ((int) in.readValue((int.class.getClassLoader())));
        this.height = ((int) in.readValue((int.class.getClassLoader())));
        this.visibility = ((String) in.readValue((String.class.getClassLoader())));
    }

    public BestPhoto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(createdAt);
        dest.writeValue(source);
        dest.writeValue(prefix);
        dest.writeValue(suffix);
        dest.writeValue(width);
        dest.writeValue(height);
        dest.writeValue(visibility);
    }

    public int describeContents() {
        return 0;
    }

}
