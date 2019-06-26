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
package org.openhab.binding.melcloud.internal.discovery;

import static org.openhab.binding.melcloud.internal.MelCloudBindingConstants.THING_TYPE_ACDEVICE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.melcloud.internal.MelCloudBindingConstants;
import org.openhab.binding.melcloud.internal.api.json.Device;
import org.openhab.binding.melcloud.internal.exceptions.MelCloudCommException;
import org.openhab.binding.melcloud.internal.handler.MelCloudAccountHandler;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MelCloudDiscoveryService} creates things based on the configured location.
 *
 * @author Luca Calcaterra - Initial Contribution
 * @author Pauli Anttila - Refactoring
 */
public class MelCloudDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(MelCloudDiscoveryService.class);

    private static final int DISCOVER_TIMEOUT_SECONDS = 10;

    private final MelCloudAccountHandler melCloudHandler;
    private ScheduledFuture<?> scanTask;

    /**
     * Creates a MelCloudDiscoveryService with enabled autostart.
     */
    public MelCloudDiscoveryService(MelCloudAccountHandler melCloudHandler) {
        super(MelCloudBindingConstants.DISCOVERABLE_THING_TYPE_UIDS, DISCOVER_TIMEOUT_SECONDS, true);
        this.melCloudHandler = melCloudHandler;
    }

    @Override
    protected void activate(Map<String, @Nullable Object> configProperties) {
        super.activate(configProperties);
    }

    @Override
    @Modified
    protected void modified(Map<String, @Nullable Object> configProperties) {
        super.modified(configProperties);
    }

    @Override
    protected void startBackgroundDiscovery() {
        discoverDevices();
    }

    @Override
    protected void startScan() {
        if (this.scanTask != null) {
            scanTask.cancel(true);
        }
        this.scanTask = scheduler.schedule(() -> discoverDevices(), 0, TimeUnit.SECONDS);
    }

    @Override
    protected void stopScan() {
        super.stopScan();

        if (this.scanTask != null) {
            this.scanTask.cancel(true);
            this.scanTask = null;
        }
    }

    private void discoverDevices() {
        logger.debug("Discover devices)");

        if (melCloudHandler != null) {
            try {
                List<Device> deviceList = melCloudHandler.getDeviceList();

                if (deviceList == null) {
                    logger.debug("No devices found");
                } else {
                    ThingUID bridgeUID = melCloudHandler.getThing().getUID();

                    deviceList.forEach(device -> {
                        ThingUID deviceThing = new ThingUID(THING_TYPE_ACDEVICE, melCloudHandler.getThing().getUID(),
                                device.getDeviceID().toString());

                        Map<String, Object> deviceProperties = new HashMap<>();
                        deviceProperties.put("deviceID", device.getDeviceID().toString());
                        deviceProperties.put("serialNumber", device.getSerialNumber().toString());
                        deviceProperties.put("macAddress", device.getMacAddress().toString());
                        deviceProperties.put("deviceName", device.getDeviceName().toString());
                        deviceProperties.put("buildingID", device.getBuildingID().toString());

                        String label = createLabel(device);
                        logger.debug("Found device: {} : {}", label, deviceProperties);

                        thingDiscovered(DiscoveryResultBuilder.create(deviceThing).withLabel(label)
                                .withProperties(deviceProperties)
                                .withRepresentationProperty(device.getDeviceID().toString()).withBridge(bridgeUID)
                                .build());
                    });
                }
            } catch (MelCloudCommException e) {
                logger.debug("Error occurred during device  list fecth, rreason {}. ", e.getMessage(), e);
            }
        }
    }

    private String createLabel(Device device) {
        StringBuilder sb = new StringBuilder();
        sb.append("A.C. Device - ");
        if (device.getBuildingName() != null && device.getBuildingName() instanceof String) {
            sb.append(device.getBuildingName()).append(" - ");
        }
        sb.append(device.getDeviceName());
        return sb.toString();
    }
}
