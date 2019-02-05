package org.openhab.binding.melcloud.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuantizedCoordinates {

    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}