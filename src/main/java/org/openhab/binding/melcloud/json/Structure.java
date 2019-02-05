package org.openhab.binding.melcloud.json;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Structure {

    @SerializedName("Floors")
    @Expose
    private List<Object> floors = null;
    @SerializedName("Areas")
    @Expose
    private List<Object> areas = null;
    @SerializedName("Devices")
    @Expose
    private List<Device> devices = null;
    @SerializedName("Clients")
    @Expose
    private List<Object> clients = null;

    public List<Object> getFloors() {
        return floors;
    }

    public void setFloors(List<Object> floors) {
        this.floors = floors;
    }

    public List<Object> getAreas() {
        return areas;
    }

    public void setAreas(List<Object> areas) {
        this.areas = areas;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public List<Object> getClients() {
        return clients;
    }

    public void setClients(List<Object> clients) {
        this.clients = clients;
    }

}