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
package org.openhab.binding.sony.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.core.NewCookie;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a store for cookies associated to single device specified by its host address
 *
 * @author andan - Initial contribution
 */
@NonNullByDefault
public class SonyAuthCookieStore {
    private final Logger logger = LoggerFactory.getLogger(SonyAuthCookieStore.class);
    private static final SonyAuthCookieStore instance = new SonyAuthCookieStore();
    private static final NewCookie EMPTY_COOKIE = new NewCookie("auth", "");

    private final ConcurrentMap<String, @NonNull NewCookie> hostAuthCookieMap = new ConcurrentHashMap<>();

    /**
     * Gets single instance
     *
     * @return the instance
     */
    public static SonyAuthCookieStore getInstance() {
        return instance;
    }

    /**
     * Gets cookie for host
     *
     * @param host the non-null host address of the device
     * @return the associated cookie
     */
    public NewCookie getAuthCookieForHost(String host) {
        final NewCookie cookie = hostAuthCookieMap.get(host);
        logger.debug("getAuthCookieForHost: host: {} cookie: {} instance: {}", host, cookie, instance);
        if (cookie != null) {
            return cookie;
        } else {
            return EMPTY_COOKIE;
        }
    }

    /**
     * Stores the cookie for host
     *
     * @param host the non-null host
     * @param authCookie the non-null cookie
     */
    public void setAuthCookieForHost(String host, NewCookie authCookie) {
        logger.debug("setAuthCookieForHost: host: {} authCookie: {} instance: {}", host, authCookie, instance);
        hostAuthCookieMap.put(host, authCookie);
    }
}
