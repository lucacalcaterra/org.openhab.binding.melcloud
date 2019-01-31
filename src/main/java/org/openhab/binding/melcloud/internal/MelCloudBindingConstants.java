/**
 * Copyright (c) 2014,2019 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.melcloud.internal;

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
    public static final String USERNAME = "username";
    public static final String WEBPASS = "webpass";
    public static final String WEBUIURL = "webUIUrl";

    // List of Bridge Type UIDs
    public static final ThingTypeUID LOGIN_BRIDGE_THING_TYPE = new ThingTypeUID(BINDING_ID, "MELCloudServer");

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "sample");

    // List of all Channel ids
    public static final String CHANNEL_1 = "channel1";
}
