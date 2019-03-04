/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.melcloud.json;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The {@link Structure} is responsible of JSON data For MelCloud API
 * sent to one of the channels.
 *
 * @author LucaCalcaterra - Initial contribution
 */
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