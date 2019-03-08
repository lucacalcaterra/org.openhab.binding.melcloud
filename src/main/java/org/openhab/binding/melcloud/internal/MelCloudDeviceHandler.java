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

import static org.openhab.binding.melcloud.internal.MelCloudBindingConstants.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.melcloud.json.DeviceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MelCloudDeviceHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author LucaCalcaterra - Initial contribution
 */
@NonNullByDefault
public class MelCloudDeviceHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(MelCloudDeviceHandler.class);
    @Nullable
    private MelCloudConfiguration config;
    private @Nullable MelCloudBridgeHandler bridgeHandler;
    DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;

    public MelCloudDeviceHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Handled command {}", command);

        if (command instanceof RefreshType) {
            // only for refresh. unused for now... muste be optimized
        } else {
            DeviceStatus cmdtoSend = new DeviceStatus();
            if (CHANNEL_POWER.equals(channelUID.getId())) {
                cmdtoSend.setPower((OnOffType) command == OnOffType.ON ? true : false);
            }
            if (CHANNEL_OPERATION_MODE.equals(channelUID.getId())) {
                cmdtoSend.setOperationMode(((DecimalType) command).intValue());
            }
            if (CHANNEL_SET_TEMPERATURE.equals(channelUID.getId())) {
                cmdtoSend.setSetTemperature(((QuantityType<?>) command).doubleValue());
            }
            if (CHANNEL_SET_FAN_SPEED.equals(channelUID.getId())) {
                cmdtoSend.setSetFanSpeed(((DecimalType) command).intValue());
            }
            if (CHANNEL_VANE_HORIZONTAL.equals(channelUID.getId())) {
                cmdtoSend.setVaneHorizontal(((DecimalType) command).intValue());
            }
            if (CHANNEL_VANE_VERTICAL.equals(channelUID.getId())) {
                cmdtoSend.setVaneVertical(((DecimalType) command).intValue());
            }
            logger.debug("devicestatus prepared for send command");
            cmdtoSend.setDeviceID(Integer.parseInt(thing.getProperties().get("deviceID")));
            // sending command
            bridgeHandler.getConnectionHandler().sendCommand(cmdtoSend);
        }
        /*
         * if (CHANNEL_POWER.equals(channelUID.getId())) {
         * if (command instanceof RefreshType) {
         * // (TODO): handle data refresh
         * }
         *
         * // (TODO): handle command
         *
         * // Note: if communication with thing fails for some reason,
         * // indicate that by setting the status with detail information:
         * // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
         * // "Could not control device at IP address x.x.x.x");
         * }
         *
         * if (CHANNEL_ROOM_TEMPERATURE.equals(channelUID.getId())) {
         * if (command instanceof RefreshType) {
         * // (TODO): handle data refresh
         * }
         *
         * // (TODO): handle command
         *
         * // Note: if communication with thing fails for some reason,
         * // indicate that by setting the status with detail information:
         * // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
         * // "Could not control device at IP address x.x.x.x");
         * }
         */
    }

    @Override
    public void initialize() {
        // logger.debug("Start initializing!");
        config = getConfigAs(MelCloudConfiguration.class);

        logger.debug("Initializing {} handler.", getThing().getThingTypeUID());

        // String errorMsg = null;
        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Bridge Not set");
            return;
        }

        bridgeHandler = (MelCloudBridgeHandler) getBridge().getHandler();
        if (bridgeHandler == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            return;
        }

        // (TODO): Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.

        /*
         * updateStatus(ThingStatus.UNKNOWN);
         *
         * // Example for background initialization:
         * scheduler.execute(() -> {
         * boolean thingReachable = true; // <background task with long running initialization here>
         * // when done do:
         * if (thingReachable) {
         * updateStatus(ThingStatus.ONLINE);
         * } else {
         * updateStatus(ThingStatus.OFFLINE);
         * }
         * });
         */
        // logger.debug("Finished initializing!");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    protected List<Channel> getChannels() {
        return getThing().getChannels();
    }

    public void updateChannel(String channelId, DeviceStatus deviceStatus) {
        switch (channelId) {

            case CHANNEL_POWER:
                updateState(CHANNEL_POWER, deviceStatus.getPower() ? OnOffType.ON : OnOffType.OFF);
                break;
            case CHANNEL_OPERATION_MODE:
                updateState(CHANNEL_OPERATION_MODE, new DecimalType(deviceStatus.getOperationMode()));
                break;
            case CHANNEL_SET_TEMPERATURE:
                updateState(CHANNEL_SET_TEMPERATURE, new DecimalType(deviceStatus.getSetTemperature()));
                break;
            case CHANNEL_SET_FAN_SPEED:
                updateState(CHANNEL_SET_FAN_SPEED, new DecimalType(deviceStatus.getSetFanSpeed()));
                break;
            case CHANNEL_VANE_HORIZONTAL:
                updateState(CHANNEL_VANE_HORIZONTAL, new DecimalType(deviceStatus.getVaneHorizontal()));
                break;
            case CHANNEL_VANE_VERTICAL:
                updateState(CHANNEL_VANE_VERTICAL, new DecimalType(deviceStatus.getVaneVertical()));
                break;
            // Read Only Channels
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
                        new DateTimeType(deviceStatus.getLastCommunication().split("[.]")[0]));
                break;
            case CHANNEL_NEXT_COMMUNICATION:
                updateState(CHANNEL_NEXT_COMMUNICATION,
                        new DateTimeType(deviceStatus.getNextCommunication().split("[.]")[0]));
                break;

        }
    }

    @Override
    protected void updateStatus(ThingStatus status) {
        // (TODO) Auto-generated method stub
        super.updateStatus(status);
    }
}
