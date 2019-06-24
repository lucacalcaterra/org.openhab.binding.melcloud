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
package org.openhab.binding.melcloud.internal.handler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.melcloud.internal.api.MelCloudConnection;
import org.openhab.binding.melcloud.internal.api.json.Device;
import org.openhab.binding.melcloud.internal.api.json.DeviceStatus;
import org.openhab.binding.melcloud.internal.api.json.ListDevicesResponse;
import org.openhab.binding.melcloud.internal.config.AccountConfig;
import org.openhab.binding.melcloud.internal.exceptions.MelCloudCommException;
import org.openhab.binding.melcloud.internal.exceptions.MelCloudLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MelCloudAccountHandler} is the handler for MELCloud API and connects it
 * to the webservice.
 *
 * @author Luca Calcaterra - Initial contribution
 * @author Pauli Anttila - Refactoring
 */
public class MelCloudAccountHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(MelCloudAccountHandler.class);

    private MelCloudConnection connection;
    private List<Device> devices;
    private ScheduledFuture<?> connectionCheckTask;
    private AccountConfig config;

    public MelCloudAccountHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing MELCloud bridge handler.");
        config = getThing().getConfiguration().as(AccountConfig.class);
        connection = new MelCloudConnection();
        devices = Collections.emptyList();
        scheduler.execute(() -> {
            connect();
        });

    }

    @Override
    public void dispose() {
        logger.debug("Running dispose()");
        devices = Collections.emptyList();
        if (this.connectionCheckTask != null) {
            this.connectionCheckTask.cancel(true);
            this.connectionCheckTask = null;
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public ThingUID getID() {
        return getThing().getUID();
    }

    public List<Device> getDeviceList() {
        return devices;
    }

    private void connect() {
        logger.debug("Initializing connection to MELCloud");
        boolean loginError = false;
        try {
            connection.login(config.username, config.password, config.language);
            ListDevicesResponse response = connection.pollDeviceList();
            if (response != null) {
                devices = response.getStructure().getDevices();
            } else {
                devices = Collections.emptyList();
            }
            updateStatus(ThingStatus.ONLINE);
        } catch (MelCloudCommException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
        } catch (MelCloudLoginException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
            loginError = true;
        }

        if (!loginError) {
            startConnectionCheck();
        }
    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public DeviceStatus getDeviceStatus(int deviceId, Optional<Integer> buildingId) throws MelCloudCommException {
        int bid = buildingId.orElse(findBuildingId(deviceId));
        return connection.pollDeviceStatus(deviceId, bid);
    }

    private int findBuildingId(int deviceId) throws MelCloudCommException {
        List<Device> deviceList = getDeviceList();
        if (deviceList != null) {
            return deviceList.stream().filter(d -> d.getDeviceID() == deviceId).findFirst().orElseThrow(
                    () -> new MelCloudCommException(String.format("Can't find building id for device id %s", deviceId)))
                    .getBuildingID();
        }
        throw new MelCloudCommException(String.format("Can't find building id for device id %s", deviceId));
    }

    public DeviceStatus sendCommand(DeviceStatus deviceStatusToSend) throws MelCloudCommException {
        return connection.sendCommand(deviceStatusToSend);
    }

    private void startConnectionCheck() {
        if (connectionCheckTask == null || connectionCheckTask.isCancelled()) {
            logger.debug("Start periodic connection check");
            Runnable runnable = () -> {
                if (connection.isConnected()) {
                    logger.trace("Connection to MELCloud open");
                } else {
                    try {
                        connect();
                    } catch (RuntimeException e) {
                        logger.warn("Unknown error occured during connection check, reason: {}.", e.getMessage(), e);
                    }
                }
            };

            connectionCheckTask = scheduler.scheduleWithFixedDelay(runnable, 30, 30, TimeUnit.SECONDS);
        } else {
            logger.debug("Connection check task already running");
        }
    }
}
