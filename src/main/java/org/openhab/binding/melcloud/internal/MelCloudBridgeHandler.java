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
import org.openhab.binding.melcloud.json.DeviceStatusResponse;
import org.openhab.binding.melcloud.json.LoginClientResponse;
import org.openhab.binding.melcloud.json.ServerDatasHandler;
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
    private @Nullable LoginClientResponse loginClientRes;
    private ServerDatasHandler serverDatasHandler;
    private @Nullable ScheduledFuture<?> refreshJob;
    private static @Nullable List<DeviceStatusResponse> deviceList;

    public MelCloudBridgeHandler(Bridge bridge) {
        super(bridge);
        serverDatasHandler = new ServerDatasHandler();
    }

    @Override
    public void initialize() {
        logger.debug("Initializing MelCloud main bridge handler.");
        Configuration config = getThing().getConfiguration();
        updateStatus(ThingStatus.UNKNOWN);

        loginClientRes = ConnectionHandler.Login(config);

        // Updates the thing status accordingly
        if (loginClientRes.getErrorId() == null) {
            try {
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
        // startAutomaticRefresh();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // not needed
    }

    @Override
    public void updateStatus(ThingStatus newStatus) {
        super.updateStatus(newStatus);
    }

    public Map<ThingUID, @Nullable ServiceRegistration<?>> getDiscoveryServiceRegs() {
        return discoveryServiceRegs;
    }

    public void setDiscoveryServiceRegs(Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs) {
        this.discoveryServiceRegs = discoveryServiceRegs;
    }

    public ServerDatasHandler getServerDatasHandler() {
        return serverDatasHandler;
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

    public @Nullable List<DeviceStatusResponse> getdeviceList() {

        logger.debug("got Device List...");
        return deviceList;
    }

    public @Nullable DeviceStatusResponse getdeviceById(int id) {
        for (DeviceStatusResponse device : deviceList) {
            if (device.getDeviceID().equals(id)) {
                return device;
            }
        }
        return null;
    }

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

            int delay = 10;
            refreshJob = scheduler.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.SECONDS);
        }
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        // TODO Auto-generated method stub
        super.childHandlerInitialized(childHandler, childThing);
        // updateThings();
        startAutomaticRefresh();
    }

    private void updateThings() {

        deviceList = ConnectionHandler.pollDevices(loginClientRes).getStructure().getDevices();

        for (Thing thing : getThing().getThings()) {
            MelCloudDeviceHandler handler = (MelCloudDeviceHandler) thing.getHandler();
            if (handler instanceof MelCloudDeviceHandler) {
                // MelCloudDeviceHandler devicehandler = new MelCloudDeviceHandler(thing);
                try {
                    handler.updateStatus(ThingStatus.ONLINE);
                } catch (Exception e) {
                    logger.debug("Illegal status transition to ONLINE of thing");
                }

                DeviceStatusResponse device = getdeviceById(Integer.parseInt(thing.getProperties().get("deviceID")));
                if (device != null) {
                    for (Channel channel : handler.getChannels()) {
                        handler.updateChannel(channel.getUID().getId(), device);
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
