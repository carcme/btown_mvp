package me.carc.btownmvp.map.sheets.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

import me.carc.btownmvp.R;

/** Holder for routing parameters
 * Created by bamptonm on 02/10/2017.
 */
public class RouteInfo implements Parcelable {

    private GeoPoint from;
    private String addressFrom;
    private GeoPoint to;
    private String addressTo;
    private Vehicle vehicle;

    public enum Vehicle {
        CAR(R.string.vehicle_car),
        WALK(R.string.vehicle_walk),
        TRAIN(R.string.vehicle_train),
        BIKE(R.string.vehicle_bike);

        private int vehicle;

        Vehicle(int vehicle) {
           this.vehicle = vehicle;
        }

        public int getVehicle() {
            return vehicle;
        }
    }

    public RouteInfo(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public GeoPoint getFrom() { return from; }
    public void setFrom(GeoPoint from) { this.from = from; }

    public GeoPoint getTo() { return to; }
    public void setTo(GeoPoint to) { this.to = to; }

    public int getVehicle() { return vehicle.getVehicle(); }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public String getAddressFrom() { return addressFrom; }
    public void setAddressFrom(String addressFrom) { this.addressFrom = addressFrom; }

    public String getAddressTo() { return addressTo; }
    public void setAddressTo(String addressTo) { this.addressTo = addressTo; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.from, 0);
        dest.writeParcelable(this.to, 0);
        dest.writeInt(this.vehicle == null ? -1 : this.vehicle.ordinal());
    }

    protected RouteInfo(Parcel in) {
        this.from = in.readParcelable(GeoPoint.class.getClassLoader());
        this.to = in.readParcelable(GeoPoint.class.getClassLoader());
        int tmpVehicle = in.readInt();
        this.vehicle = tmpVehicle == -1 ? null : Vehicle.values()[tmpVehicle];
    }

    public static final Parcelable.Creator<RouteInfo> CREATOR = new Parcelable.Creator<RouteInfo>() {
        public RouteInfo createFromParcel(Parcel source) { return new RouteInfo(source); }
        public RouteInfo[] newArray(int size) { return new RouteInfo[size]; }
    };
}
