/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sony.internal.SonyUtil;
import org.openhab.binding.sony.internal.scalarweb.gson.SupportedApiInfoDeserializer;

/**
 * This class represents a supported API version and is used for serialization and deserialization via
 * {@link SupportedApiInfoDeserializer}
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class SupportedApiVersionInfo {
    /** The authorization level of the API */
    private final String authLevel;

    /** The protocols for the API */
    private final Set<String> protocols;

    /** The API version */
    final String version;

    /**
     * Constructs the API with only the version (authLevel and protols are blank)
     * 
     * @param version a non-null, non-empty version
     */
    public SupportedApiVersionInfo(final String version) {
        this("", new HashSet<>(), version);
    }

    /**
     * Constructs the API with the parameters
     * 
     * @param authLevel a non-null, possibly empty authLevel
     * @param protocols a non-null, possibly empty set of protocols
     * @param version a non-null, non-empty version
     */
    public SupportedApiVersionInfo(final String authLevel, final Set<String> protocols, final String version) {
        Objects.requireNonNull(authLevel, "authLevel cannot be null");
        Objects.requireNonNull(protocols, "protocols cannot be null");
        SonyUtil.validateNotEmpty(version, "version cannot be empty");

        this.authLevel = authLevel;
        this.protocols = protocols;
        this.version = version;
    }

    /**
     * Returns the auth level
     * 
     * @return the non-null, possibly empty auth level
     */
    public String getAuthLevel() {
        return authLevel;
    }

    /**
     * Returns the supported protocols
     * 
     * @return a non-null, possibly empty set of protocols
     */
    public Set<String> getProtocols() {
        return protocols;
    }

    /**
     * Returns the API version
     * 
     * @return a non-null, non-empty version
     */
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "SupportedApiVersionInfo [authLevel=" + authLevel + ", protocols=" + protocols + ", version=" + version
                + "]";
    }
}
