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
 * @author Wietse van Buitenen - Return all devices
 */
public class MelCloudAccountHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(MelCloudAccountHandler.class);

    private MelCloudConnection connection;
    private List<Device> devices;
    private ScheduledFuture<?> connectionCheckTask;
    private AccountConfig config;
    private boolean loginCredentialError;

    public MelCloudAccountHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing MELCloud account handler.");
        config = getThing().getConfiguration().as(AccountConfig.class);
        connection = new MelCloudConnection();
        devices = Collections.emptyList();
        loginCredentialError = false;
        scheduler.execute(() -> {
            try {
                connect();
            } catch (MelCloudCommException e) {
                logger.debug("Cannot open the connection to MELCloud, reason: {}", e.getMessage());
            } finally {
                startConnectionCheck();
            }
        });
    }

    @Override
    public void dispose() {
        logger.debug("Running dispose()");
        stopConnectionCheck();
        connection = null;
        devices = Collections.emptyList();
        config = null;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public ThingUID getID() {
        return getThing().getUID();
    }

    public List<Device> getDeviceList() throws MelCloudCommException, MelCloudLoginException {
        connectIfNotConnected();
        return connection.fetchDeviceList();
    }

    private void connect() throws MelCloudCommException, MelCloudLoginException {
        if (loginCredentialError) {
            throw new MelCloudLoginException("Connection to MELCloud can't be open because of wrong credentials");
        }
        logger.debug("Initializing connection to MELCloud");
        updateStatus(ThingStatus.OFFLINE);
        try {
            connection.login(config.username, config.password, config.language);
            devices = connection.fetchDeviceList();
            updateStatus(ThingStatus.ONLINE);
        } catch (MelCloudLoginException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
            loginCredentialError = true;
            throw e;
        } catch (MelCloudCommException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            throw e;
        }
    }

    private synchronized void connectIfNotConnected() throws MelCloudCommException, MelCloudLoginException {
        if (!isConnected()) {
            connect();
        }
    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public DeviceStatus sendDeviceStatus(DeviceStatus deviceStatus)
            throws MelCloudCommException, MelCloudLoginException {
        connectIfNotConnected();
        try {
            return connection.sendDeviceStatus(deviceStatus);
        } catch (MelCloudLoginException e) {
            throw e;
        } catch (MelCloudCommException e) {
            logger.debug("Sending failed, retry once with relogin");
            connect();
            return connection.sendDeviceStatus(deviceStatus);
        }
    }

    public DeviceStatus fetchDeviceStatus(int deviceId, Optional<Integer> buildingId)
            throws MelCloudCommException, MelCloudLoginException {
        connectIfNotConnected();
        int bid = buildingId.orElse(findBuildingId(deviceId));

        try {
            return connection.fetchDeviceStatus(deviceId, bid);
        } catch (MelCloudLoginException e) {
            throw e;
        } catch (MelCloudCommException e) {
            logger.debug("Sending failed, retry once with relogin");
            connect();
            return connection.fetchDeviceStatus(deviceId, bid);
        }
    }

    private int findBuildingId(int deviceId) throws MelCloudCommException {
        if (devices != null) {
            return devices.stream().filter(d -> d.getDeviceID() == deviceId).findFirst().orElseThrow(
                    () -> new MelCloudCommException(String.format("Can't find building id for device id %s", deviceId)))
                    .getBuildingID();
        }
        throw new MelCloudCommException(String.format("Can't find building id for device id %s", deviceId));
    }

    private void startConnectionCheck() {
        if (connectionCheckTask == null || connectionCheckTask.isCancelled()) {
            logger.debug("Start periodic connection check");
            Runnable runnable = () -> {
                logger.debug("Check MELCloud connection");
                if (connection.isConnected()) {
                    logger.debug("Connection to MELCloud open");
                } else {
                    try {
                        connect();
                    } catch (MelCloudCommException e) {
                        logger.debug("Connection to MELCloud down");
                    } catch (RuntimeException e) {
                        logger.warn("Unknown error occured during connection check, reason: {}.", e.getMessage(), e);
                    }
                }
            };
            connectionCheckTask = scheduler.scheduleWithFixedDelay(runnable, 30, 60, TimeUnit.SECONDS);
        } else {
            logger.debug("Connection check task already running");
        }
    }

    private void stopConnectionCheck() {
        if (connectionCheckTask != null) {
            logger.debug("Stop periodic connection check");
            connectionCheckTask.cancel(true);
            connectionCheckTask = null;
        }
    }
}
