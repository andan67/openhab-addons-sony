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

/**
 * This class represents the picture-in-picture (PIP) screen position and is used for deserialization only
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class PipSubScreenPosition {

    /** The PIP screen position */
    private @Nullable String position;

    /**
     * Constructor used for deserialization only
     */
    public PipSubScreenPosition() {
    }

    /**
     * Gets the PIP screen position
     *
     * @return the PIP screen position
     */
    public @Nullable String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "PipSubScreenPosition [position=" + position + "]";
    }
}
