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
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sony.internal.SonyUtil;

/**
 * This class represents the request for the power savings mode and is used for deserialization/serialization only
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class PowerSavingMode {
    /** The power savings mode */
    private @Nullable String mode;

    /**
     * Constructor used for deserialization only
     */
    public PowerSavingMode() {
    }

    /**
     * Instantiates a new power saving mode.
     *
     * @param mode the mode
     */
    public PowerSavingMode(final String mode) {
        SonyUtil.validateNotEmpty(mode, "mode cannot be empty");
        this.mode = mode;
    }

    /**
     * Gets the power savings mode
     *
     * @return the power savings mode
     */
    public @Nullable String getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "PowerSavingMode [mode=" + mode + "]";
    }
}
