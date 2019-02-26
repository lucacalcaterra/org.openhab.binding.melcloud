/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.melcloud.internal.handler;

import static org.openhab.binding.melcloud.internal.MelCloudBindingConstants.THING_TYPE_ACDEVICE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.melcloud.json.Device;
import org.openhab.binding.melcloud.json.LoginClientRes;
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
    private @Nullable LoginClientRes loginClientRes;
    private ServerDatasHandler serverDatasHandler;
    private static @Nullable List<Device> deviceList;

    public MelCloudBridgeHandler(Bridge bridge) {
        super(bridge);
        serverDatasHandler = new ServerDatasHandler();
    }

    @Override
    public void initialize() {

        logger.debug("Initializing MelCloud main bridge handler.");
        Configuration config = getThing().getConfiguration();

        loginClientRes = ConnectionHandler.Login(config);

        // Updates the thing status accordingly
        if (loginClientRes.getErrorId() == null) {
            updateStatus(ThingStatus.ONLINE);
        } else {
            /*
             * loginResult.error = loginResult.error.trim();
             * logger.debug("Disabling thing '{}': Error '{}': {}", getThing().getUID(), loginResult.error,
             * loginResult.errorDetail);
             */
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Connection error: Check Config or network");
        }

        updateThings();

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

    /*
     * public boolean isValidConfig() {
     * return loginResult == null ? false : loginResult.error == null;
     * }
     */
    public @Nullable List<Device> getdeviceList() {

        deviceList = ConnectionHandler.pollDevices(loginClientRes).getStructure().getDevices();
        logger.debug("got Device List...");
        return deviceList;

    }

    private void updateThings() {

        this.getdeviceList();
        getThing().getThings().forEach(thing -> {
            logger.debug("update channels...");
            if (thing.getThingTypeUID().equals(THING_TYPE_ACDEVICE)) {
                // handler
            }
        });
    }
}
