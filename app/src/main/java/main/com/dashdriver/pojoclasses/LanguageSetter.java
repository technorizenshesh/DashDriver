package main.com.dashdriver.pojoclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by technorizen on 3/7/18.
 */

public class LanguageSetter {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("short_code")
    @Expose
    private Object shortCode;
    @SerializedName("status")
    @Expose
    private String status;

    private boolean isSelected;
    public String getId() {
        return id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getShortCode() {
        return shortCode;
    }

    public void setShortCode(Object shortCode) {
        this.shortCode = shortCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
