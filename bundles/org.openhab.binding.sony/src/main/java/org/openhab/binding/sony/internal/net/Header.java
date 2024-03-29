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
package org.openhab.binding.sony.internal.net;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sony.internal.SonyUtil;

/**
 * The class simply represents a header as a single entity
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class Header {

    /** The name of the header */
    private final String name;

    /** The value of the header */
    private final String value;

    /**
     * Instantiates a new header based on the name and value
     *
     * @param name the non-null, non-empty name
     * @param value the non-null, non-empty value
     */
    public Header(final String name, final String value) {
        SonyUtil.validateNotEmpty(name, "name cannot be empty");
        SonyUtil.validateNotEmpty(value, "value cannot be empty");

        this.name = name;
        this.value = value;
    }

    /**
     * Gets the header name
     *
     * @return the non-null, non-empty name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the header value
     *
     * @return the non-null, non-empty value
     */
    public String getValue() {
        return value;
    }
}
