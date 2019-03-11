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
package org.openhab.binding.melcloud.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.melcloud.internal.handler.ConnectionHandler;
import org.openhab.binding.melcloud.json.Device;
import org.openhab.binding.melcloud.json.DeviceStatus;
import org.openhab.binding.melcloud.json.LoginClientResponse;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MelCloudBridgeHandler} is the handler for MelCloud API and connects it
 * to the webservice.
 *
 * @author Luca Calcaterra - Initial contribution
 *
 */
@NonNullByDefault
public class MelCloudBridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(MelCloudBridgeHandler.class);

    private Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();
    // private @Nullable LoginClientResponse loginClientRes;
    private @Nullable ScheduledFuture<?> refreshJob;

    private @Nullable ConnectionHandler connectionHandler;
    private @Nullable LoginClientResponse loginClientRes;
    private static @Nullable List<Device> deviceList;
    // private ListDevicesResponse listDevices;

    public @Nullable ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public MelCloudBridgeHandler(Bridge bridge) {
        super(bridge);
        // listDevices = new ListDevicesResponse();

    }

    @Override
    public void initialize() {
        logger.debug("Initializing MelCloud main bridge handler.");
        Configuration config = getThing().getConfiguration();
        updateStatus(ThingStatus.UNKNOWN);

        this.connectionHandler = new ConnectionHandler(config);
        loginClientRes = connectionHandler.Login();

        // Updates the thing status accordingly
        if ((loginClientRes != null) && (loginClientRes.getErrorId() == null)) {
            try {
                deviceList = connectionHandler.pollDevices().getStructure().getDevices();
                updateStatus(ThingStatus.ONLINE);
            } catch (Exception e) {
                logger.debug("Illegal status transition to ONLINE");
            }
        } else {
            /*
             * loginResult.error = loginResult.error.trim();
             * logger.debug("Disabling thing '{}': Error '{}': {}", getThing().getUID(), loginResult.error,
             * loginResult.errorDetail);
             */
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Connection error: Check Config or network");
        }
        startAutomaticRefresh();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // not needed
    }

    public Map<ThingUID, @Nullable ServiceRegistration<?>> getDiscoveryServiceRegs() {
        return discoveryServiceRegs;
    }

    public void setDiscoveryServiceRegs(Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs) {
        this.discoveryServiceRegs = discoveryServiceRegs;
    }

    @Override
    public void handleRemoval() {
        // removes the old registration service associated to the bridge, if existing
        ServiceRegistration<?> dis = this.getDiscoveryServiceRegs().get(this.getThing().getUID());
        logger.debug("handleRemoval() '{}': ServiceRegistration {}", getThing().getUID(), dis);
        if (null != dis) {
            dis.unregister();
        }
        super.handleRemoval();
    }

    /*
     * public Map<Integer, String> getSiteList() {
     * return loginResult == null ? new HashMap<Integer, String>() : loginResult.siteList;
     * }
     */
    public ThingUID getID() {
        return getThing().getUID();
    }

    public @Nullable List<Device> getdeviceList() {

        logger.debug("got Device List...");
        return deviceList;
    }

    /*
     * public @Nullable Device getdeviceById(int id) {
     *
     * if (deviceList != null) {
     * for (Device device : deviceList) {
     * if (device.getDeviceID().equals(id)) {
     * return device;
     * }
     * }
     * }
     * return null;
     * }
     */

    /**
     * Start the job refreshing the data
     */
    private void startAutomaticRefresh() {
        if (refreshJob == null || refreshJob.isCancelled()) {
            Runnable runnable = () -> {
                try {
                    updateThings();
                } catch (Exception e) {
                    logger.error("Exception occurred during execution: {}", e.getMessage(), e);
                }
            };

            int delay = 30;
            refreshJob = scheduler.scheduleWithFixedDelay(runnable, 4, delay, TimeUnit.SECONDS);
        }
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        // TODO Auto-generated method stub
        super.childHandlerInitialized(childHandler, childThing);
        // updateThings();
    }

    private synchronized void updateThings() {

        for (Thing thing : getThing().getThings()) {
            MelCloudDeviceHandler handler = (MelCloudDeviceHandler) thing.getHandler();
            if (handler instanceof MelCloudDeviceHandler) {
                // MelCloudDeviceHandler devicehandler = new MelCloudDeviceHandler(thing);
                try {
                    handler.updateStatus(ThingStatus.ONLINE);
                } catch (Exception e) {
                    logger.debug("Illegal status transition to ONLINE of thing");
                }

                // Device device = getdeviceById(Integer.parseInt(thing.getProperties().get("deviceID")));
                DeviceStatus deviceStatus = connectionHandler
                        .pollDeviceStatus(Integer.parseInt(thing.getProperties().get("deviceID")));
                if (deviceStatus != null) {
                    for (Channel channel : handler.getChannels()) {
                        handler.updateChannels(channel.getUID().getId(), deviceStatus);
                        /*
                         * switch (channel.getUID().getId()) {
                         * case CHANNEL_POWER:
                         * updateState(channel.getUID(), OnOffType.ON);
                         *
                         * }
                         */
                    }
                }
            }
        }
    }
}
