package main.com.dashdriver.constant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import main.com.dashdriver.pojoclasses.MyvehicleBean;

/**
 * Created by technorizen on 5/12/18.
 */

public class MyVehicleCls {


    @SerializedName("result")
    @Expose
    private List<MyvehicleBean> result = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    public List<MyvehicleBean> getResult() {
        return result;
    }

    public void setResult(List<MyvehicleBean> result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
