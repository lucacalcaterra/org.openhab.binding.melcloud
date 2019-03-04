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
package org.openhab.binding.melcloud.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link ServerDatasHandler} is responsible for handling
 * and interpreting json from server
 *
 * @author Luca Calcaterra - Initial contribution
 */
public class ServerDatasHandler extends ListDevicesResponse implements Cloneable {

    private final static Logger logger = LoggerFactory.getLogger(ServerDatasHandler.class);

    public ServerDatasHandler createServerDatasHandler(String json) {
        logger.debug("debug response complete: {}", json);
        Gson gson = new Gson();
        return gson.fromJson(json, ServerDatasHandler.class);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
