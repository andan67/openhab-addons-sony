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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sony.internal.scalarweb.models.ScalarWebResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * This class represents a count and is used for deserialization only
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class CurrentTime {

    /** The date time */
    private final ZonedDateTime dateTime;

    /**
     * Instantiates a new current time
     *
     * @param results the results
     */
    public CurrentTime(final ScalarWebResult results) {
        Objects.requireNonNull(results, "results cannot be null");

        final JsonArray resultArray = results.getResults();

        if (resultArray == null || resultArray.size() != 1) {
            throw new JsonParseException("Result should only have a single element: " + resultArray);
        }

        final JsonElement elm = resultArray.get(0);

        if (elm.isJsonPrimitive()) {
            // 2017-02-01T15:07:11-0500
            final String dateString = elm.getAsString();
            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
            dateTime = ZonedDateTime.parse(dateString, dtf);
        } else if (elm.isJsonObject()) {
            final JsonObject obj = elm.getAsJsonObject();

            String myDateTime = null;
            Integer myTimeZoneOffsetMinute = null;
            Integer myDstOffsetMinute = null;

            final JsonElement dateTimeElm = obj.get("dateTime");
            if (dateTimeElm != null) {
                myDateTime = dateTimeElm.getAsString();
            }

            final JsonElement timeZoneElm = obj.get("timeZoneOffsetMinute");
            if (timeZoneElm != null && timeZoneElm.isJsonPrimitive() && timeZoneElm.getAsJsonPrimitive().isNumber()) {
                myTimeZoneOffsetMinute = timeZoneElm.getAsInt();
            }

            final JsonElement dstElm = obj.get("dstOffsetMinute");
            if (dstElm != null && dstElm.isJsonPrimitive() && dstElm.getAsJsonPrimitive().isNumber()) {
                myDstOffsetMinute = dstElm.getAsInt();
            }

            if (myDateTime == null || myDateTime.isEmpty()) {
                throw new JsonParseException("'dateTime' property was not found: " + obj);
            }

            if (myTimeZoneOffsetMinute == null) {
                throw new JsonParseException("'timeZoneOffsetMinute' property was not found: " + obj);
            }

            if (myDstOffsetMinute == null) {
                throw new JsonParseException("'dstOffsetMinute' property was not found: " + obj);
            }

            final int offsetMintues = myTimeZoneOffsetMinute + myDstOffsetMinute;

            if (myDateTime != null) {
                dateTime = LocalDateTime.parse(myDateTime)
                        .atZone(ZoneOffset.ofHoursMinutes(offsetMintues / 60, offsetMintues % 60));
            } else {
                // should not happen
                dateTime = ZonedDateTime.now();
            }
        } else {
            throw new JsonParseException("Unknown result element: " + elm);
        }
    }

    /**
     * Gets the date time
     *
     * @return the date time
     */
    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "CurrentTime [dateTime=" + dateTime + "]";
    }
}
