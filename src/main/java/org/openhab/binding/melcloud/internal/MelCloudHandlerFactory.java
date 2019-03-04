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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.melcloud.internal.discovery.MelCloudDiscoveryService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MelCloudHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author LucaCalcaterra - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.melcloud", service = ThingHandlerFactory.class)
public class MelCloudHandlerFactory extends BaseThingHandlerFactory {
    private final Logger logger = LoggerFactory.getLogger(MelCloudHandlerFactory.class);

    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID) || BRIDGE_THING_TYPES_UIDS.contains(thingTypeUID);

    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (LOGIN_BRIDGE_THING_TYPE.equals(thingTypeUID)) {
            MelCloudBridgeHandler handler = new MelCloudBridgeHandler((Bridge) thing);
            registerDiscoveryService(handler);
            logger.debug("createThing(): LOGIN_BRIDGE_THING_TYPE: Creating an '{}' type Thing - {}", thingTypeUID,
                    handler.getID());
            return handler;

        } else if (THING_TYPE_ACDEVICE.equals(thingTypeUID)) {
            MelCloudDeviceHandler handler = new MelCloudDeviceHandler(thing);
            return handler;
        }

        return null;
    }

    private synchronized void registerDiscoveryService(MelCloudBridgeHandler bridgeHandler) {
        // (TODO) Auto-generated method stub
        MelCloudDiscoveryService discoveryService = new MelCloudDiscoveryService(bridgeHandler);
        bridgeHandler.getDiscoveryServiceRegs().put(bridgeHandler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
        logger.debug("registerMelCloudDiscoveryService(): Bridge Handler - {}, Class Name - {}, Discovery Service - {}",
                bridgeHandler, DiscoveryService.class.getName(), discoveryService);
    }
}
