/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.melcloud.internal.discovery;

import static org.openhab.binding.melcloud.internal.MelCloudBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.melcloud.internal.MelCloudBridgeHandler;
//import org.openhab.binding.riscocloud.handler.SiteBridgeHandler;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MelCloudDiscoveryService} creates things based on the configured location.
 *
 * @author Sébastien Cantineau - Initial Contribution
 */
@NonNullByDefault
public class MelCloudDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(MelCloudDiscoveryService.class);

    private static final int DISCOVER_TIMEOUT_SECONDS = 10;

    private final MelCloudBridgeHandler bridgeHandler;
    private @Nullable ScheduledFuture<?> discoveryJob;

    /**
     * Creates a MelCloudDiscoveryService with enabled autostart.
     */
    public MelCloudDiscoveryService(MelCloudBridgeHandler bridgeHandler) {
        super(SUPPORTED_THING_TYPES_UIDS, DISCOVER_TIMEOUT_SECONDS, true);
        this.bridgeHandler = bridgeHandler;
    }

    @Override
    protected void activate(@Nullable Map<String, @Nullable Object> configProperties) {
        super.activate(configProperties);
    }

    @Override
    @Modified
    protected void modified(@Nullable Map<String, @Nullable Object> configProperties) {
        super.modified(configProperties);
    }

    @Override
    protected void startScan() {
        logger.debug("Starting MelCloud discovery scan");
        createResults();
    }

    @Override
    protected void startBackgroundDiscovery() {
        if (discoveryJob == null) {
            discoveryJob = scheduler.scheduleWithFixedDelay(() -> {
                createResults();
            }, 0, 24, TimeUnit.HOURS);
            logger.debug("Scheduled MelCloud-changed job every {} hours", 24);
        }
    }

    private void createResults() {
        logger.debug("createResults()");
        ThingUID bridgeUID = bridgeHandler.getThing().getUID();
        if (bridgeHandler.getThing().getThingTypeUID().equals(LOGIN_BRIDGE_THING_TYPE)) {
            logger.debug("bridge type");
            // get device list
            bridgeHandler.getdeviceList().forEach(device -> {
                ThingUID deviceThing = new ThingUID(THING_TYPE_ACDEVICE, bridgeHandler.getThing().getUID(),
                        "Device-" + device.getDeviceID());
                Map<String, Object> deviceProperties = new HashMap<>();
                deviceProperties.put("deviceID", device.getDeviceID());

                thingDiscovered(DiscoveryResultBuilder.create(deviceThing).withLabel(device.getDeviceName())
                        .withProperties(deviceProperties).withBridge(bridgeHandler.getThing().getUID()).build());

                logger.debug("return Things belongs to MelCloud Bridge");
            }

            );
            logger.debug("finish list of devices");
        }

    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stopping MelCloud background discovery");
        if (discoveryJob != null && !discoveryJob.isCancelled()) {
            if (discoveryJob.cancel(true)) {
                discoveryJob = null;
                logger.debug("Stopped MelCloud background discovery");
            }
        }
    }

}
