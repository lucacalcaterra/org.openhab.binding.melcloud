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

import static org.eclipse.smarthome.core.library.unit.SIUnits.CELSIUS;
import static org.openhab.binding.melcloud.internal.MelCloudBindingConstants.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Temperature;

import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.melcloud.internal.api.json.DeviceStatus;
import org.openhab.binding.melcloud.internal.config.AcDeviceConfig;
import org.openhab.binding.melcloud.internal.exceptions.MelCloudCommException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MelCloudDeviceHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author LucaCalcaterra - Initial contribution
 * @author Pauli Anttila - Refactoring
 */

public class MelCloudDeviceHandler extends BaseThingHandler {

    private static final int EFFECTIVE_FLAG_POWER = 0x01;
    private static final int EFFECTIVE_FLAG_OPERATION_MODE = 0x02;
    private static final int EFFECTIVE_FLAG_TEMPERATURE = 0x04;
    private static final int EFFECTIVE_FLAG_FAN_SPEED = 0x08;
    private static final int EFFECTIVE_FLAG_VANE_VERTICAL = 0x10;
    private static final int EFFECTIVE_FLAG_VANE_HORIZONTAL = 0x100;

    private final Logger logger = LoggerFactory.getLogger(MelCloudDeviceHandler.class);
    private AcDeviceConfig config;
    private MelCloudAccountHandler melCloudHandler;
    private DeviceStatus deviceStatus;
    private ScheduledFuture<?> refreshTask;

    public MelCloudDeviceHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing {} handler.", getThing().getThingTypeUID());

        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Bridge Not set");
            return;
        }

        config = getThing().getConfiguration().as(AcDeviceConfig.class);
        logger.debug("A.C. device config: {}", config);

        initializeBridge((getBridge() == null) ? null : getBridge().getHandler(),
                (getBridge() == null) ? null : getBridge().getStatus());
    }

    @Override
    public void dispose() {
        logger.debug("Running dispose()");
        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
        melCloudHandler = null;
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        logger.debug("bridgeStatusChanged {} for thing {}", bridgeStatusInfo, getThing().getUID());
        initializeBridge((getBridge() == null) ? null : getBridge().getHandler(), bridgeStatusInfo.getStatus());

        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            startAutomaticRefresh();
        }
    }

    private void initializeBridge(ThingHandler thingHandler, ThingStatus bridgeStatus) {
        logger.debug("initializeBridge {} for thing {}", bridgeStatus, getThing().getUID());

        if (thingHandler != null && bridgeStatus != null) {
            melCloudHandler = (MelCloudAccountHandler) thingHandler;

            if (bridgeStatus == ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Received command '{}' to channel {}", command, channelUID);

        if (command instanceof RefreshType) {
            logger.debug("Resfresh command not supported");
            return;
        }

        if (melCloudHandler == null) {
            logger.warn("No connection to MELCloud available, ignore command");
            return;
        }

        if (deviceStatus == null) {
            logger.warn("No initial data available, ignore command");
            return;
        }

        DeviceStatus cmdtoSend = getDeviceStatusCopy(deviceStatus);
        cmdtoSend.setEffectiveFlags(0);

        switch (channelUID.getId()) {
            case CHANNEL_POWER:
                cmdtoSend.setPower((OnOffType) command == OnOffType.ON ? true : false);
                cmdtoSend.setEffectiveFlags(EFFECTIVE_FLAG_POWER);
                break;
            case CHANNEL_OPERATION_MODE:
                cmdtoSend.setOperationMode(((DecimalType) command).intValue());
                cmdtoSend.setEffectiveFlags(EFFECTIVE_FLAG_OPERATION_MODE);
                break;
            case CHANNEL_SET_TEMPERATURE:
                BigDecimal val = null;
                if (command instanceof QuantityType) {
                    QuantityType<Temperature> quantity = ((QuantityType<Temperature>) command).toUnit(CELSIUS);
                    if (quantity != null) {
                        val = quantity.toBigDecimal().setScale(1, RoundingMode.HALF_UP);
                    }
                } else {
                    val = new BigDecimal(command.toString()).setScale(1, RoundingMode.HALF_UP);

                }
                if (val != null) {
                    // round nearest .5
                    double v = Math.round(val.doubleValue() * 2) / 2.0;
                    cmdtoSend.setSetTemperature(v);
                    cmdtoSend.setEffectiveFlags(EFFECTIVE_FLAG_TEMPERATURE);
                } else {
                    logger.debug("Can't convert '{}' to set temperature", command);
                }
                break;
            case CHANNEL_FAN_SPEED:
                cmdtoSend.setSetFanSpeed(((DecimalType) command).intValue());
                cmdtoSend.setEffectiveFlags(EFFECTIVE_FLAG_FAN_SPEED);
                break;
            case CHANNEL_VANE_VERTICAL:
                cmdtoSend.setVaneVertical(((DecimalType) command).intValue());
                cmdtoSend.setEffectiveFlags(EFFECTIVE_FLAG_VANE_VERTICAL);
                break;
            case CHANNEL_VANE_HORIZONTAL:
                cmdtoSend.setVaneHorizontal(((DecimalType) command).intValue());
                cmdtoSend.setEffectiveFlags(EFFECTIVE_FLAG_VANE_HORIZONTAL);
                break;
            default:
                logger.debug("Read only or unknown channel {}, skip update", channelUID);
        }

        if (cmdtoSend.getEffectiveFlags() > 0) {
            cmdtoSend.setHasPendingCommand(true);
            cmdtoSend.setDeviceID(config.deviceID);
            try {
                DeviceStatus newDeviceStatus = melCloudHandler.sendDeviceStatus(cmdtoSend);
                updateChannels(newDeviceStatus);
            } catch (MelCloudCommException e) {
                logger.warn("Command '{}' to channel '{}' failed, reason {}. ", command, channelUID, e.getMessage(), e);
            }
        } else {
            logger.debug("Nothing to send");
        }

    }

    private DeviceStatus getDeviceStatusCopy(DeviceStatus deviceStatus) {
        DeviceStatus copy = new DeviceStatus();
        synchronized (this) {
            copy.setPower(deviceStatus.getPower());
            copy.setOperationMode(deviceStatus.getOperationMode());
            copy.setSetTemperature(deviceStatus.getSetTemperature());
            copy.setSetFanSpeed(deviceStatus.getSetFanSpeed());
            copy.setVaneVertical(deviceStatus.getVaneVertical());
            copy.setVaneHorizontal(deviceStatus.getVaneHorizontal());
            copy.setEffectiveFlags(deviceStatus.getEffectiveFlags());
            copy.setHasPendingCommand(deviceStatus.getHasPendingCommand());
            copy.setDeviceID(deviceStatus.getDeviceID());
        }
        return copy;
    }

    private void startAutomaticRefresh() {
        if (refreshTask == null || refreshTask.isCancelled()) {
            Runnable runnable = () -> getDeviceDataAndUpdateChannels();
            refreshTask = scheduler.scheduleWithFixedDelay(runnable, 1, config.pollingInterval, TimeUnit.SECONDS);
        }
    }

    private void getDeviceDataAndUpdateChannels() {
        if (melCloudHandler.isConnected()) {
            logger.debug("Update device '{}' channels", getThing().getThingTypeUID());
            try {
                DeviceStatus newDeviceStatus = melCloudHandler.fetchDeviceStatus(config.deviceID,
                        Optional.ofNullable(config.buildingID));
                updateChannels(newDeviceStatus);
            } catch (MelCloudCommException e) {
                logger.debug("Error occurred during device '{}' polling, reason {}. ", getThing().getThingTypeUID(),
                        e.getMessage(), e);
            }
        } else {
            logger.debug("Connection to MELCloud is not open, skip periodic update");
        }
    }

    private synchronized void updateChannels(DeviceStatus newDeviceStatus) {
        synchronized (this) {
            deviceStatus = newDeviceStatus;
            for (Channel channel : getThing().getChannels()) {
                updateChannels(channel.getUID().getId(), deviceStatus);
            }
        }
    }

    private void updateChannels(String channelId, DeviceStatus deviceStatus) {
        switch (channelId) {
            case CHANNEL_POWER:
                updateState(CHANNEL_POWER, deviceStatus.getPower() ? OnOffType.ON : OnOffType.OFF);
                break;
            case CHANNEL_OPERATION_MODE:
                updateState(CHANNEL_OPERATION_MODE, new DecimalType(deviceStatus.getOperationMode()));
                break;
            case CHANNEL_SET_TEMPERATURE:
                updateState(CHANNEL_SET_TEMPERATURE,
                        new QuantityType<Temperature>(deviceStatus.getSetTemperature(), SIUnits.CELSIUS));
                break;
            case CHANNEL_FAN_SPEED:
                updateState(CHANNEL_FAN_SPEED, new DecimalType(deviceStatus.getSetFanSpeed()));
                break;
            case CHANNEL_VANE_HORIZONTAL:
                updateState(CHANNEL_VANE_HORIZONTAL, new DecimalType(deviceStatus.getVaneHorizontal()));
                break;
            case CHANNEL_VANE_VERTICAL:
                updateState(CHANNEL_VANE_VERTICAL, new DecimalType(deviceStatus.getVaneVertical()));
                break;
            case CHANNEL_OFFLINE:
                updateState(CHANNEL_OFFLINE, deviceStatus.getOffline() ? OnOffType.ON : OnOffType.OFF);
                break;
            case CHANNEL_HAS_PENDING_COMMAND:
                updateState(CHANNEL_HAS_PENDING_COMMAND,
                        deviceStatus.getHasPendingCommand() ? OnOffType.ON : OnOffType.OFF);
                break;
            case CHANNEL_ROOM_TEMPERATURE:
                updateState(CHANNEL_ROOM_TEMPERATURE, new DecimalType(deviceStatus.getRoomTemperature()));
                break;
            case CHANNEL_LAST_COMMUNICATION:
                updateState(CHANNEL_LAST_COMMUNICATION,
                        new DateTimeType(convertDateTime(deviceStatus.getLastCommunication())));
                break;
            case CHANNEL_NEXT_COMMUNICATION:
                updateState(CHANNEL_NEXT_COMMUNICATION,
                        new DateTimeType(convertDateTime(deviceStatus.getNextCommunication())));
                break;
        }
    }

    private ZonedDateTime convertDateTime(String dateTime) {
        return ZonedDateTime.ofInstant(LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                ZoneOffset.UTC, ZoneId.systemDefault());
    }
}
