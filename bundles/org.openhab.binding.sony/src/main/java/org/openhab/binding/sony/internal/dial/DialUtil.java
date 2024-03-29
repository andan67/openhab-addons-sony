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
package org.openhab.binding.sony.internal.dial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sony.internal.SonyUtil;
import org.openhab.binding.sony.internal.dial.models.DialApp;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;

/**
 * This is a utility class for the DIAL app
 *
 * @author Tim Roberts - Initial contribution
 * @author andan - Adaptions for OH3
 */
@NonNullByDefault
public class DialUtil {
    /**
     * Generates the list of channels for each of the dial applications
     *
     * @param thingUID a non-null thing UID
     * @param dialApps a non-null, possibly empty list of dial applications
     * @return a non-null, possibly empty list of channels
     */
    static List<Channel> generateChannels(final ThingUID thingUID, final Collection<DialApp> dialApps) {
        Objects.requireNonNull(thingUID, "thingUID cannot be null");
        Objects.requireNonNull(dialApps, "dialApps cannot be null");

        final Set<String> cachedIds = new HashSet<>();

        return dialApps.stream().map(da -> {
            final List<Channel> channels = new ArrayList<>();

            final String applId = da.getId();
            if (applId != null && !applId.isEmpty()) {
                final Map<String, String> props = new HashMap<>();
                props.put(DialConstants.CHANNEL_PROP_APPLID, applId);

                // The following tries to simplify the channel names generated by
                // using the part of the ApplID that is likely unique.
                //
                // Applids are similar to domain names in form: com.sony.netflix
                // The following tries to find a unique channel name from it by ..
                // 1. Starting with the last node ('netflix')
                // 2. If not unique - backups up one digit ('ynetflix')
                // Note: non valid characters will be ignored
                // 3. If the whole id is not unique (should never happen) - starts
                // to add the count to the end ('com.sony.netflix-1')
                // Eventually we'll have a unique id
                //
                // The risk is that there is a very small chance channel ids may not be stable
                // between restarts.
                // Example: let say we generated a channel from 'com.sony.netflix' named 'netflix'
                // Now lets say the user added a new channel with an id of 'com.malware.netflix'
                // and it comes in before the original one. 'com.malware.netflix' will be assigned 'netflix'
                // and the real one would be 'ynetflix'.
                // Sucks but I think the risk is very very small (doubt sony would assign something like that)
                // and we gain much smaller channel names as the benefit
                int i = applId.lastIndexOf('.');
                String channelId = SonyUtil.createValidChannelUId(applId.substring(i));
                while (cachedIds.contains(channelId)) {
                    if (i <= 0) {
                        channelId = applId + "-" + (-(--i));
                    } else {
                        channelId = SonyUtil.createValidChannelUId(applId.substring(--i));
                    }
                }
                cachedIds.add(channelId);

                channels.add(ChannelBuilder
                        .create(new ChannelUID(thingUID, channelId + "-" + DialConstants.CHANNEL_TITLE), "String")
                        .withLabel(da.getName() + " Title").withProperties(props)
                        .withType(DialConstants.CHANNEL_TITLE_UID).build());
                channels.add(ChannelBuilder
                        .create(new ChannelUID(thingUID, channelId + "-" + DialConstants.CHANNEL_ICON), "Image")
                        .withLabel(da.getName() + " Icon").withProperties(props)
                        .withType(DialConstants.CHANNEL_ICON_UID).build());
                channels.add(ChannelBuilder
                        .create(new ChannelUID(thingUID, channelId + "-" + DialConstants.CHANNEL_STATE), "Switch")
                        .withLabel(da.getName() + " Status").withProperties(props)
                        .withType(DialConstants.CHANNEL_STATE_UID).build());
            }
            return channels;
        }).flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
    }
}
