package main.com.dashdriver.pojoclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by technorizen on 5/12/18.
 */

public class VehicleType {
    @SerializedName("result")
    @Expose
    private List<ViechleBean> result = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    public List<ViechleBean> getResult() {
        return result;
    }

    public void setResult(List<ViechleBean> result) {
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
