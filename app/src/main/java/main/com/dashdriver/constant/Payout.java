package main.com.dashdriver.constant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by technorizen on 5/12/18.
 */

public class Payout {


    @SerializedName("result")
    @Expose
    private List<PayoutBeen> result = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    public List<PayoutBeen> getResult() {
        return result;
    }

    public void setResult(List<PayoutBeen> result) {
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

