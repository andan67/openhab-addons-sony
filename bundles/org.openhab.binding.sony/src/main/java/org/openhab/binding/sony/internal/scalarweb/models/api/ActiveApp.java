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
 * This class represents the activate application and is used for serialization only
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class ActiveApp {

    /** The uri of the active application */
    private final @Nullable String uri;

    /** The extra data of the active application */
    private final @Nullable String data;

    /**
     * Instantiates a new active application
     *
     * @param uri the non-null, non-empty uri
     * @param data the possibly null, possibly empty data
     */
    public ActiveApp(final String uri, final @Nullable String data) {
        SonyUtil.validateNotEmpty(uri, "uri cannot be empty");
        this.uri = uri;
        this.data = data;
    }

    /**
     * Gets the uri of the active application
     *
     * @return the uri of the active application
     */
    public @Nullable String getUri() {
        return uri;
    }

    /**
     * Gets the optional data for the active application
     *
     * @return the optional data for the active application
     */
    public @Nullable String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ActiveApp [uri=" + uri + ", data=" + data + "]";
    }
}
