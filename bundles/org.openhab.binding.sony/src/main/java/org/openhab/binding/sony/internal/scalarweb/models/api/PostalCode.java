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
 * This class represents the request to set the postal code and is used for deserialization/serialization only
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class PostalCode {
    /** The postal code */
    private @Nullable String postalCode;

    /**
     * Constructor used for deserialization only
     */
    public PostalCode() {
    }

    /**
     * Instantiates a new postal code
     *
     * @param postalCode the postal code
     */
    public PostalCode(final String postalCode) {
        SonyUtil.validateNotEmpty(postalCode, "postalCode cannot be empty");
        this.postalCode = postalCode;
    }

    /**
     * Gets the postal code
     *
     * @return the postal code
     */
    public @Nullable String getPostalCode() {
        return postalCode;
    }

    @Override
    public String toString() {
        return "PostalCode [postalCode=" + postalCode + "]";
    }
}
