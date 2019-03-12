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
    private @Nullable ScheduledFuture<?> refreshJob;

    private @Nullable ConnectionHandler connectionHandler;
    private @Nullable LoginClientResponse loginClientRes;
    private @Nullable List<Device> deviceList;

    public @Nullable ConnectionHandler getConnectionHandler() {
        if (this.connectionHandler != null) {
            return connectionHandler;
        }

        return null;
    }

    public MelCloudBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing MelCloud main bridge handler.");
        Configuration config = getThing().getConfiguration();
        updateStatus(ThingStatus.UNKNOWN);

        try {
            ConnectionHandler connectionHandler = new ConnectionHandler(config);
            this.connectionHandler = connectionHandler;
            if (connectionHandler.login()) {
                LoginClientResponse loginClientRes = ConnectionHandler.getLoginClientRes();
                this.loginClientRes = loginClientRes;
                // Updates the thing status accordingly
                if ((loginClientRes.getErrorId() == null)) {
                    try {
                        if (connectionHandler.pollDevices()) {
                            deviceList = ConnectionHandler.getListDevicesResponse().getStructure().getDevices();
                        }
                        updateStatus(ThingStatus.ONLINE);
                    } catch (Exception e) {
                        logger.debug("Illegal status transition to ONLINE");
                    }
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                            "Connection error: Check Config or network");
                }
                startAutomaticRefresh();
            }
        } catch (Exception e) {
            logger.debug("exception on login...");
        }
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

    public ThingUID getID() {
        return getThing().getUID();
    }

    public @Nullable List<Device> getdeviceList() {
        logger.debug("got Device List...");
        return deviceList;
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

            int delay = 30;
            refreshJob = scheduler.scheduleWithFixedDelay(runnable, 6, delay, TimeUnit.SECONDS);
        }
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        // TODO: Auto-generated method stub
        super.childHandlerInitialized(childHandler, childThing);
    }

    private synchronized void updateThings() {
        for (Thing thing : getThing().getThings()) {
            MelCloudDeviceHandler handler = (MelCloudDeviceHandler) thing.getHandler();
            if (handler instanceof MelCloudDeviceHandler) {
                try {
                    handler.updateStatus(ThingStatus.ONLINE);
                } catch (Exception e) {
                    logger.debug("Illegal status transition to ONLINE of thing");
                }

                // Device device = getdeviceById(Integer.parseInt(thing.getProperties().get("deviceID")));
                ConnectionHandler connectionHandler = this.connectionHandler;
                if (connectionHandler != null && connectionHandler.isConnected) {
                    DeviceStatus deviceStatus = connectionHandler
                            .pollDeviceStatus(Integer.parseInt(thing.getProperties().get("deviceID")));
                    if (deviceStatus != null) {
                        for (Channel channel : handler.getChannels()) {
                            handler.updateChannels(channel.getUID().getId(), deviceStatus);
                        }
                    }
                }
            }
        }
    }
}
