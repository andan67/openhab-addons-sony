package org.openhab.binding.sony.internal;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.NewCookie;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * This class is used to provide the cookie used for the normal key authentication across all sessions
 */
@NonNullByDefault
public class SonyAuthCookieStore {
    private static SonyAuthCookieStore instance;
    private static final String AUTHCOOKIENAME = "auth";

    private final Map<String, NewCookie> hostAuthCookieMap = new HashMap<>();

    private SonyAuthCookieStore() {
    };

    public static SonyAuthCookieStore getInstance() {
        if (SonyAuthCookieStore.instance == null) {
            SonyAuthCookieStore.instance = new SonyAuthCookieStore();
        }
        return SonyAuthCookieStore.instance;
    }

    public NewCookie getAuthCookieForHost(String host) {
        if (hostAuthCookieMap.containsKey(host)) {
            return hostAuthCookieMap.get(host);
        } else {
            return new NewCookie(AUTHCOOKIENAME, "");
        }
    }

    public void setAuthCookieForHost(String host, NewCookie authCookie) {
        hostAuthCookieMap.put(host, authCookie);
    }
}
