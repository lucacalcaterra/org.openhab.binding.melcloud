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

import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link MelCloudBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author LucaCalcaterra - Initial contribution
 */
@NonNullByDefault
public class MelCloudBindingConstants {

    private static final String BINDING_ID = "melcloud";

    // List of all Main Bridge properties
    public static final String LOGIN_USERNAME = "username";
    public static final String LOGIN_PASS = "webpass";

    // Url's
    public static final String WEBUIURL = "webUIUrl";
    public static final String LOGIN_URL = "loginUrl";
    public static final String LOGIN_LANG = "loginLanguageId";
    public static final String LOGIN_APPVERSION = "loginAppVersion";
    // List of Bridge Type UIDs
    public static final ThingTypeUID LOGIN_BRIDGE_THING_TYPE = new ThingTypeUID(BINDING_ID, "melCloudServerBridge");

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_ACDEVICE = new ThingTypeUID(BINDING_ID, "acDevice");

    // List of all Channel ids
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_ROOM_TEMPERATURE = "roomTemperature";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_ACDEVICE);
    public static final Set<ThingTypeUID> BRIDGE_THING_TYPES_UIDS = Collections.singleton(LOGIN_BRIDGE_THING_TYPE);
}
