package main.com.dashdriver.pojoclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by technorizen on 5/12/18.
 */

public class MyvehicleBean {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("active_car")
    @Expose
    private String active_car;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("vehicle_type_id")
    @Expose
    private String vehicleTypeId;
    @SerializedName("vehicle_number")
    @Expose
    private String vehicleNumber;
    @SerializedName("vehicle_model")
    @Expose
    private String vehicleModel;
    @SerializedName("vehicle_model_year")
    @Expose
    private String vehicleModelYear;
    @SerializedName("vehicle_color")
    @Expose
    private String vehicleColor;
    @SerializedName("vehicle_image")
    @Expose
    private String vehicleImage;
    @SerializedName("vehicle_name")
    @Expose
    private String vehicleName;
    @SerializedName("vehicle_type_name")
    @Expose
    private String vehicleTypeName;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;
    @SerializedName("vehicle_size")
    @Expose
    private String vehicleSize;
    @SerializedName("vehicle_size_id")
    @Expose
    private String vehicleSizeId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("refrigirated")
    @Expose
    private String refrigirated;
    @SerializedName("vehicle_make")
    @Expose
    private String vehicle_make;
    @SerializedName("license_plate")
    @Expose
    private String license_plate;

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicle_make() {
        return vehicle_make;
    }

    public void setVehicle_make(String vehicle_make) {
        this.vehicle_make = vehicle_make;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public String getActive_car() {
        return active_car;
    }

    public void setActive_car(String active_car) {
        this.active_car = active_car;
    }

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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleModelYear() {
        return vehicleModelYear;
    }

    public void setVehicleModelYear(String vehicleModelYear) {
        this.vehicleModelYear = vehicleModelYear;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }

    public String getVehicleSize() {
        return vehicleSize;
    }

    public void setVehicleSize(String vehicleSize) {
        this.vehicleSize = vehicleSize;
    }

    public String getVehicleSizeId() {
        return vehicleSizeId;
    }

    public void setVehicleSizeId(String vehicleSizeId) {
        this.vehicleSizeId = vehicleSizeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


}
