
package me.carc.btown.data.all4squ;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.carc.btown.data.all4squ.entities.Response;


public class FourSquResult implements Serializable, Parcelable
{

    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }


    @SuppressWarnings("unchecked")
    public final static Creator<FourSquResult> CREATOR = new Creator<FourSquResult>() {
        public FourSquResult createFromParcel(Parcel in) {
            return new FourSquResult(in);
        }
        public FourSquResult[] newArray(int size) {
            return (new FourSquResult[size]);
        }
    };

    private final static long serialVersionUID = -7853239957788151370L;

    protected FourSquResult(Parcel in) {
        this.response = ((Response) in.readValue((Response.class.getClassLoader())));
    }

    public FourSquResult() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(response);
    }

    public int describeContents() {
        return  0;
    }

}
