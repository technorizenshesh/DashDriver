package main.com.dashdriver.pojoclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by technorizen on 27/11/18.
 */

public class ViechleBean {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("car_name")
    @Expose
    private String vehicleName;
    @SerializedName("vehicle_image")
    @Expose
    private String vehicleImage;
    @SerializedName("base_fare")
    @Expose
    private String baseFare;
    @SerializedName("min_charge")
    @Expose
    private String minCharge;
    @SerializedName("ride_time_charge_permin")
    @Expose
    private String rideTimeChargePermin;
    @SerializedName("service_tax")
    @Expose
    private String serviceTax;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("refrigirated")
    @Expose
    private String refrigirated;

    public String getRefrigirated() {
        return refrigirated;
    }

    public void setRefrigirated(String refrigirated) {
        this.refrigirated = refrigirated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getMinCharge() {
        return minCharge;
    }

    public void setMinCharge(String minCharge) {
        this.minCharge = minCharge;
    }

    public String getRideTimeChargePermin() {
        return rideTimeChargePermin;
    }

    public void setRideTimeChargePermin(String rideTimeChargePermin) {
        this.rideTimeChargePermin = rideTimeChargePermin;
    }

    public String getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(String serviceTax) {
        this.serviceTax = serviceTax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
