/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.sony.internal.scalarweb.models.api;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sony.internal.SonyUtil;

/**
 * This class represents the request for a software update
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class SoftwareUpdateRequest {

    /** Constant saying to use the network for software updates */
    public static final String USENETWORK = "true";

    /** Constant saying to use a USB for software updates */
    public static final String USEUSB = "false";

    /** Whether to use the network ("true") or a USB ("false)") */
    private final String network;

    /**
     * Creates the request using the specified network
     * 
     * @param network a non-null, non-empty network
     */
    public SoftwareUpdateRequest(final String network) {
        SonyUtil.validateNotEmpty(network, "network cannot be empty");

        this.network = network;
    }

    @Override
    public String toString() {
        return "SoftwareUpdateRequest [network=" + network + "]";
    }
}
