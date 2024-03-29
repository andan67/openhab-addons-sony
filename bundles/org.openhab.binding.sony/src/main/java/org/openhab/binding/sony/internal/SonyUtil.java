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
package org.openhab.binding.sony.internal;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sony.internal.net.NetUtil;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various, usually unrelated, utility functions used across the sony binding
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
public class SonyUtil {

    /**
     * Maps primitive {@code Class}es to their corresponding wrapper {@code Class}.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();
    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    /** Bigdecimal hundred (used in scale/unscale methods) */
    public static final BigDecimal BIGDECIMAL_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Creates a channel identifier from the group (if specified) and channel id
     *
     * @param groupId the possibly null, possibly empty group id
     * @param channelId the non-null, non-empty channel id
     * @return a non-null, non-empty channel id
     */
    public static String createChannelId(final @Nullable String groupId, final String channelId) {
        SonyUtil.validateNotEmpty(channelId, "channelId cannot be empty");
        return groupId == null || groupId.isEmpty() ? channelId : (groupId + "#" + channelId);
    }

    /**
     * This utility function will take a potential channelUID string and return a valid channelUID by removing all
     * invalidate characters (see {@link AbstractUID#SEGMENT_PATTERN})
     *
     * @param channelUID the non-null, possibly empty channelUID to validate
     * @return a non-null, potentially empty string representing what was valid
     */
    public static String createValidChannelUId(final String channelUID) {
        Objects.requireNonNull(channelUID, "channelUID cannot be null");
        final String id = channelUID.replaceAll("[^A-Za-z0-9_-]", "").toLowerCase();
        return SonyUtil.isEmpty(id) ? "na" : id;
    }

    /**
     * Utility function to close a {@link AutoCloseable} and log any exception thrown.
     *
     * @param closeable a possibly null {@link AutoCloseable}. If null, no action is done.
     */
    public static void close(final @Nullable AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final Exception e) {
                LoggerFactory.getLogger(SonyUtil.class).debug("Exception closing: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Determines if the current thread has been interrupted or not
     *
     * @return true if interrupted, false otherwise
     */
    public static boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    /**
     * Checks whether the current thread has been interrupted and throws {@link InterruptedException} if it's been
     * interrupted.
     *
     * @throws InterruptedException the interrupted exception
     */
    public static void checkInterrupt() throws InterruptedException {
        if (isInterrupted()) {
            throw new InterruptedException("thread interrupted");
        }
    }

    /**
     * Cancels the specified {@link Future}.
     *
     * @param future a possibly null future. If null, no action is done
     */
    public static void cancel(final @Nullable Future<?> future) {
        if (future != null) {
            future.cancel(true);
        }
    }

    /**
     * Returns a new string type or UnDefType.UNDEF if the string is null
     *
     * @param str the possibly null string
     * @return either a StringType or UnDefType.UNDEF is null
     */
    public static State newStringType(final @Nullable String str) {
        return str == null ? UnDefType.UNDEF : new StringType(str);
    }

    /**
     * Returns a new quantity type or UnDefType.UNDEF if the integer is null
     *
     * @param itgr the possibly null integer
     * @param unit a non-null unit
     * @return either a QuantityType or UnDefType.UNDEF is null
     */
    public static <T extends Quantity<T>> State newQuantityType(final @Nullable Integer itgr, final Unit<T> unit) {
        Objects.requireNonNull(unit, "unit cannot be null");
        return itgr == null ? UnDefType.UNDEF : new QuantityType<T>(itgr, unit);
    }

    /**
     * Returns a new quantity type or UnDefType.UNDEF if the double is null
     *
     * @param dbl the possibly null double
     * @param unit a non-null unit
     * @return either a QuantityType or UnDefType.UNDEF is null
     */
    public static <T extends Quantity<T>> State newQuantityType(final @Nullable Double dbl, final Unit<T> unit) {
        Objects.requireNonNull(unit, "unit cannot be null");
        return dbl == null ? UnDefType.UNDEF : new QuantityType<T>(dbl, unit);
    }

    /**
     * Returns a new decimal type or UnDefType.UNDEF if the integer is null
     *
     * @param itgr the possibly null integer
     * @return either a DecimalType or UnDefType.UNDEF is null
     */
    public static State newDecimalType(final @Nullable Integer itgr) {
        return itgr == null ? UnDefType.UNDEF : new DecimalType(itgr);
    }

    /**
     * Returns a new decimal type or UnDefType.UNDEF if the double is null
     *
     * @param dbl the possibly null double
     * @return either a DecimalType or UnDefType.UNDEF is null
     */
    public static State newDecimalType(final @Nullable Double dbl) {
        return dbl == null ? UnDefType.UNDEF : new DecimalType(dbl);
    }

    /**
     * Returns a new decimal type or UnDefType.UNDEF if the string representation is null
     *
     * @param nbr the possibly null, possibly empty string decimal
     * @return either a DecimalType or UnDefType.UNDEF is null
     */
    public static State newDecimalType(final @Nullable String nbr) {
        return nbr == null || nbr.isEmpty() ? UnDefType.UNDEF : new DecimalType(nbr);
    }

    /**
     * Returns a new percent type or UnDefType.UNDEF if the value is null
     *
     * @param val the possibly null big decimal
     * @return either a PercentType or UnDefType.UNDEF is null
     */
    public static State newPercentType(final @Nullable BigDecimal val) {
        return val == null ? UnDefType.UNDEF : new PercentType(val);
    }

    /**
     * Returns a new percent type or UnDefType.UNDEF if the value is null
     *
     * @param val the possibly null big decimal
     * @return either a PercentType or UnDefType.UNDEF is null
     */
    public static State newBooleanType(final @Nullable Boolean val) {
        return val == null ? UnDefType.UNDEF : val.booleanValue() ? OnOffType.ON : OnOffType.OFF;
    }

    /**
     * Scales the associated big decimal within the miniumum/maximum defined
     *
     * @param value a non-null value to scale
     * @param minimum a possibly null minimum value (if null, zero will be used)
     * @param maximum a possibly null maximum value (if null, 100 will be used)
     * @return a scaled big decimal value
     */
    public static BigDecimal scale(final BigDecimal value, final @Nullable BigDecimal minimum,
            final @Nullable BigDecimal maximum) {
        Objects.requireNonNull(value, "value cannot be null");

        final int initialScale = value.scale();

        final BigDecimal min = minimum == null ? BigDecimal.ZERO : minimum;
        final BigDecimal max = maximum == null ? BIGDECIMAL_HUNDRED : maximum;

        if (min.compareTo(max) > 0) {
            return BigDecimal.ZERO;
        }

        final BigDecimal val = guard(value, min, max);
        final BigDecimal scaled = val.subtract(min).multiply(BIGDECIMAL_HUNDRED).divide(max.subtract(min),
                initialScale + 2, RoundingMode.HALF_UP);
        return guard(scaled.setScale(initialScale, RoundingMode.HALF_UP), BigDecimal.ZERO, BIGDECIMAL_HUNDRED);
    }

    /**
     * Unscales the associated big decimal within the miniumum/maximum defined
     *
     * @param scaledValue a non-null scaled value
     * @param minimum a possibly null minimum value (if null, zero will be used)
     * @param maximum a possibly null maximum value (if null, 100 will be used)
     * @return a scaled big decimal value
     */
    public static BigDecimal unscale(final BigDecimal scaledValue, final @Nullable BigDecimal minimum,
            final @Nullable BigDecimal maximum) {
        Objects.requireNonNull(scaledValue, "scaledValue cannot be null");

        final int initialScale = scaledValue.scale();
        final BigDecimal min = minimum == null ? BigDecimal.ZERO : minimum;
        final BigDecimal max = maximum == null ? BIGDECIMAL_HUNDRED : maximum;

        if (min.compareTo(max) > 0) {
            return min;
        }

        final BigDecimal scaled = guard(scaledValue, BigDecimal.ZERO, BIGDECIMAL_HUNDRED);
        final BigDecimal val = max.subtract(min)
                .multiply(scaled.divide(BIGDECIMAL_HUNDRED, initialScale + 2, RoundingMode.HALF_UP)).add(min);

        return guard(val.setScale(initialScale, RoundingMode.HALF_UP), min, max);
    }

    /**
     * Provides a guard to value (value must be within the min/max range - if outside, will be set to the min or max)
     *
     * @param value a non-null value to guard
     * @param minimum a non-null minimum value
     * @param maximum a non-null maximum value
     * @return a big decimal within the min/max range
     */
    public static BigDecimal guard(final BigDecimal value, final BigDecimal minimum, final BigDecimal maximum) {
        Objects.requireNonNull(value, "value cannot be null");
        Objects.requireNonNull(minimum, "minimum cannot be null");
        Objects.requireNonNull(maximum, "maximum cannot be null");
        if (value.compareTo(minimum) < 0) {
            return minimum;
        }
        if (value.compareTo(maximum) > 0) {
            return maximum;
        }
        return value;
    }

    /**
     * Performs a WOL if there is a configured ip address and mac address. If either ip address or mac address is
     * null/empty, call is ignored
     * 
     * @param logger the non-null logger to log messages to
     * @param deviceIpAddress the possibly null, possibly empty device ip address
     * @param deviceMacAddress the possibly null, possibly empty device mac address
     */
    public static void sendWakeOnLan(final Logger logger, final @Nullable String deviceIpAddress,
            final @Nullable String deviceMacAddress) {
        Objects.requireNonNull(logger, "logger cannot be null");

        if (deviceIpAddress != null && deviceMacAddress != null && !deviceIpAddress.isBlank()
                && !deviceMacAddress.isBlank()) {
            try {
                NetUtil.sendWol(deviceIpAddress, deviceMacAddress);
                // logger.debug("WOL packet sent to {}", deviceMacAddress);
                logger.info("WOL packet sent to {}", deviceMacAddress);
            } catch (final IOException e) {
                logger.debug("Exception occurred sending WOL packet to {}", deviceMacAddress, e);
            }
        } else {
            logger.debug(
                    "WOL packet is not supported - specify the IP address and mac address in config if you want a WOL packet sent");
        }
    }

    /**
     * Returns true if the two maps are: both null or of equal size, all keys and values (case insensitve) match
     * 
     * @param map1 a possibly null, possibly empty map
     * @param map2 a possibly null, possibly empty map
     * @return true if they match, false otherwise
     */
    public static boolean equalsIgnoreCase(final Map<String, String> map1, final Map<String, String> map2) {
        Objects.requireNonNull(map1, "map1 cannot be null");
        Objects.requireNonNull(map2, "map2 cannot be null");

        if (map1.size() != map2.size()) {
            return false;
        }

        final Map<String, String> lowerMap1 = map1.entrySet().stream()
                .map(s -> new AbstractMap.SimpleEntry<>(s.getKey().toLowerCase(), s.getValue().toLowerCase()))
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));

        final Map<String, String> lowerMap2 = map1.entrySet().stream()
                .map(s -> new AbstractMap.SimpleEntry<>(s.getKey().toLowerCase(), s.getValue().toLowerCase()))
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));

        return lowerMap1.equals(lowerMap2);
    }

    /**
     * Null safety compare of two strings
     *
     * @param str1 a possibly null string
     * @param str2 a possibly null string to compare
     * @return true if strings are equal or both null, other false
     */
    public static boolean equals(final @Nullable String str1, final @Nullable String str2) {
        if (str1 == str2) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * Converts a string to a Boolean object
     *
     * @param str a possibly null string representation of a boolean
     * @return null if string is null, TRUE if string represents a true boolean, FALSE otherwise
     */
    public static @Nullable Boolean toBooleanObject(@Nullable String str) {
        if (str == null)
            return null;
        if (str.equalsIgnoreCase("true"))
            return Boolean.TRUE;
        if (str.equalsIgnoreCase("yes"))
            return Boolean.TRUE;
        if (str.equalsIgnoreCase("on"))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    /**
     * Checks if a string represents an integer number
     *
     * @param str the possibly null string
     * @return true if string represents an integer number, false otherwise
     */
    public static boolean isNumeric(@Nullable String str) {
        if (str == null) {
            return false;
        }
        try {
            long l = Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a string represetns a (double) number
     *
     * @param str the possibly null string
     * @return true if string represents a double number, false otherwise
     */
    public static boolean isNumber(@Nullable String str) {
        if (str == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Capitalize string
     *
     * @param str the possibly null string
     * @return empty string id string i null or empty, otherwise the capitalized string
     */
    public static String capitalize(@Nullable String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Left padding of string with character
     *
     * @param str the string
     * @param padSize the padding size
     * @param padChar the padding character
     * @return the left padded string
     */
    public static String leftPad(String str, int padSize, Character padChar) {
        return padChar.toString().repeat(Math.max(padSize - str.length(), 0)) + str;
    }

    /**
     * Right padding of string with character
     *
     * @param str the string
     * @param padSize the padding size
     * @param padChar the padding character
     * @return the right padded string
     */
    public static String rightPad(String str, int padSize, Character padChar) {
        return str + padChar.toString().repeat(Math.max(padSize - str.length(), 0));
    }

    /**
     * Trim string
     *
     * @param str possibly null string
     * @return empty string if input string is null, otherwise trimmed string
     */
    public static String trimToEmpty(@Nullable String str) {
        if (str == null) {
            return "";
        } else {
            return str.trim();
        }
    }

    /**
     * Strip character sequence from start of a string
     *
     * @param str the string
     * @param stripChars the strip characters
     * @return the stripped string
     */
    public static String stripStart(final String str, final String stripChars) {
        final int strLen = str.length();
        if (strLen == 0) {
            return str;
        }
        int start = 0;
        if (stripChars.isEmpty()) {
            return str;
        } else {
            while (start != strLen && stripChars.indexOf(str.charAt(start)) >= 0) {
                start++;
            }
        }
        return str.substring(start);
    }

    /**
     * Strip character sequence from end of a string
     *
     * @param str the string
     * @param stripChars the strip characters
     * @return the stripped string
     */
    public static String stripEnd(final String str, final String stripChars) {
        int end = str.length();
        if (end == 0) {
            return str;
        }

        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.isEmpty()) {
            return str;
        } else {
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) >= 0) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    public static String join(final String delimiter, final @Nullable String @Nullable [] strArray) {
        // public static String join(final String delimiter, final String[] strArray) {
        if (strArray == null)
            return "";
        return Arrays.stream(strArray).map(s -> s == null ? "" : s).collect(Collectors.joining(delimiter));
    }

    /**
     * Returns true if the two sets are: both null or of equal size and all keys match (case insensitive)
     * 
     * @param set1 a possibly null, possibly empty set
     * @param set2 a possibly null, possibly empty set
     * @return true if they match, false otherwise
     */
    public static boolean equalsIgnoreCase(final @Nullable Set<@Nullable String> set1,
            final @Nullable Set<@Nullable String> set2) {
        if (set1 == null && set2 == null) {
            return true;
        }

        if (set1 == null) {
            return false;
        }

        if (set2 == null) {
            return false;
        }

        if (set1.size() != set2.size()) {
            return false;
        }
        final TreeSet<String> tset1 = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        // convert null strings in source set to empty strings to avoid null type mismatch
        set1.stream().map(s -> s == null ? "" : s).forEach(s -> tset1.add(Objects.requireNonNull(s)));
        final TreeSet<String> tset2 = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        // convert null strings in source set to empty strings to avoid null type mismatch
        set2.stream().map(s -> s == null ? "" : s).forEach(s -> tset2.add(Objects.requireNonNull(s)));
        return tset1.equals(tset2);
    }

    /**
     * Determines if the model name is valid (alphanumeric plus dash)
     * 
     * @param modelName a non-null, possibly empty model name
     * @return true if a valid model name, false otherwise
     */
    public static boolean isValidModelName(final String modelName) {
        return modelName.matches("[A-Za-z0-9-]+");// && modelName.matches(".*\\d\\d.*"); - only valid for tvs
    }

    /**
     * Determines if the thing type UID is a generic thing type (scalar) or a custom one (scalar-X800)
     * 
     * @param uid a non-null UID
     * @return true if generic, false otherwise
     */
    public static boolean isGenericThingType(final ThingTypeUID uid) {
        Objects.requireNonNull(uid, "uid cannot be null");

        final String typeId = uid.getId();
        return typeId.indexOf("-") < 0;
    }

    /**
     * Get's the service name from a thing type uid ("scalar" for example if "scalar" or "scalar-X800" or
     * "scalar-X800_V2")
     * 
     * @param uid a non-null UID
     * @return a non-null service name
     */
    public static String getServiceName(final ThingTypeUID uid) {
        Objects.requireNonNull(uid, "uid cannot be null");

        final String typeId = uid.getId();
        final int idx = typeId.indexOf("-");

        return idx < 0 ? typeId : typeId.substring(0, idx);
    }

    /**
     * Get's the model name from a thing type uid (null if just "scalar" or "X800" if "scalar-X800" or "X800" if
     * "scalar-X800_V2")
     * 
     * @param uid a non-null UID
     * @return a model name or null if not found (ie generic)
     */
    public static @Nullable String getModelName(final ThingTypeUID uid) {
        Objects.requireNonNull(uid, "uid cannot be null");

        final String typeId = uid.getId();
        final int idx = typeId.indexOf("-");
        if (idx < 0) {
            return null;
        }

        final String modelName = typeId.substring(idx + 1);

        final int versIdx = modelName.lastIndexOf(SonyBindingConstants.MODELNAME_VERSION_PREFIX);
        return versIdx >= 0 ? modelName.substring(0, versIdx) : modelName;
    }

    /**
     * Get's the model version number from a thing type uid ("2" if "scalar-X800_V2" or 0 in all other cases)
     * 
     * @param uid a non-null thing type uid
     * @return the model version (with 0 being the default)
     */
    public static int getModelVersion(final ThingTypeUID uid) {
        Objects.requireNonNull(uid, "uid cannot be null");

        final String modelName = getModelName(uid);
        if (modelName == null || modelName.isEmpty()) {
            return 0;
        }

        final int versIdx = modelName.lastIndexOf(SonyBindingConstants.MODELNAME_VERSION_PREFIX);
        if (versIdx > 0) {
            final String vers = modelName.substring(versIdx + SonyBindingConstants.MODELNAME_VERSION_PREFIX.length());
            try {
                return Integer.parseInt(vers);
            } catch (final NumberFormatException e) {
                return 0;
            }
        }

        return 0;
    }

    /**
     * Determins if a thing type service/model name (which can contain wildcards) matches the corresponding
     * service/model name (regardless of the model version)
     * 
     * @param thingTypeServiceName a possibly null, possibly empty thing service name
     * @param thingTypeModelName a possibly null, possibly empty thing model name. Use "x" (case sensitive) to denote a
     *            wildcard (like 'XBR-xX830' to match all screen sizes)
     * @param serviceName a non-null, non-empty service name
     * @param modelName a non-null, non-empty model name
     * @return true if they match (regardless of model name version), false otherwise
     */
    public static boolean isModelMatch(final @Nullable String thingTypeServiceName,
            final @Nullable String thingTypeModelName, final String serviceName, final String modelName) {
        SonyUtil.validateNotEmpty(serviceName, "serviceName cannot be empty");
        SonyUtil.validateNotEmpty(modelName, "modelName cannot be empty");
        if (thingTypeServiceName == null || thingTypeServiceName.isEmpty()) {
            return false;
        }

        if (thingTypeModelName == null || thingTypeModelName.isEmpty()) {
            return false;
        }

        String modelPattern = thingTypeModelName.replaceAll("x", ".*").toLowerCase();

        // remove a version identifier ("_V1" or "_V292")
        final int versIdx = modelPattern.lastIndexOf(SonyBindingConstants.MODELNAME_VERSION_PREFIX.toLowerCase());
        if (versIdx > 0) {
            final String vers = modelPattern.substring(versIdx + 2);
            if (SonyUtil.isNumeric(vers)) {
                modelPattern = modelPattern.substring(0, versIdx);
            }
        }

        return thingTypeServiceName.equals(serviceName) && modelName.toLowerCase().matches(modelPattern);
    }

    /**
     * Determines if the thingtype uid matches the specified serviceName/model name
     * 
     * @param uid a non-null thing type UID
     * @param serviceName a non-null, non-empty service name
     * @param modelName a non-null, non-empty model name
     * @return true if they match (regardless of model name version), false otherwise
     */
    public static boolean isModelMatch(final ThingTypeUID uid, final String serviceName, final String modelName) {
        Objects.requireNonNull(uid, "uid cannot be null");
        SonyUtil.validateNotEmpty(modelName, "modelName cannot be empty");

        final String uidServiceName = getServiceName(uid);
        final String uidModelName = getModelName(uid);
        return uidModelName == null || uidModelName.isEmpty() ? false
                : isModelMatch(uidServiceName, uidModelName, serviceName, modelName);
    }

    /**
     * Converts a nullable list (with nullable elements) to a non-null list (containing no null elements) by filtering
     * all null elements out
     * 
     * @param list the list to convert
     * @return a non-null list of the same type
     */
    public static <T> List<T> convertNull(final @Nullable List<@Nullable T> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        return list.stream().filter(e -> e != null).collect(Collectors.toList());
    }

    /**
     * Converts a nullable array (with nullable elements) to a non-null list (containing no null elements) by filtering
     * all null elements out
     * 
     * @param list the array to convert
     * @return a non-null list of the same type
     */
    public static <T> List<T> convertNull(final @Nullable T @Nullable [] list) {
        if (list == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(list).filter(e -> e != null).collect(Collectors.toList());
    }

    /**
     * Determines if the pass class is a primitive (we treat string as a primitive here)
     * 
     * @param clazz a non-null class
     * @return true if primitive, false otherwise
     */
    public static <T> boolean isPrimitive(final Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz cannot be null");
        return clazz.isPrimitive() || primitiveWrapperMap.get(clazz) != null || clazz == String.class;
    }

    /**
     * Determines if the pass string is a null or empty
     *
     * @param str the String to check, may be null
     * @return true if string is null or empty, false otherwise
     */
    public static boolean isEmpty(final @Nullable String str) {
        return (str == null || str.isEmpty());
    }

    /**
     * Returns original string if not empty, otherwise default string
     *
     * @param str the original string which is checked for emptiness
     * @param defStr the default string
     * @return str if not null or empty, otherwise the default or empty string
     */
    public static String defaultIfEmpty(final @Nullable String str, final @Nullable String defStr) {
        return str != null && !str.isEmpty() ? str : (defStr != null ? defStr : "");
    }

    /**
     * Case insensitive check if a String ends with a specified suffix
     *
     * @see java.lang.String#endsWith(String)
     * @param str the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case insensitive, or
     *         both <code>null</code>
     * @since 2.4
     */
    public static boolean endsWithIgnoreCase(final String str, final String suffix) {
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(true, strOffset, suffix, 0, suffix.length());
    }

    /**
     * Validates if string is not empty and throws IllegalArgumentExction if invalid
     *
     * @param str the String to validate
     * @param message the message
     */
    public static void validateNotEmpty(final String str, final String message) {
        if (SonyUtil.isEmpty(str))
            throw new IllegalArgumentException(message);
    }

    /**
     * Returns the substring before the first occurrence of a delimiter. The
     * delimiter is not part of the result.
     *
     * @param str String to get a substring from.
     * @param del String to search for.
     * @return Substring before the first occurrence of the delimiter.
     */
    public static String substringBefore(String str, String del) {
        int pos = str.indexOf(del);

        return pos >= 0 ? str.substring(0, pos) : del;
    }

    /**
     * Returns the substring after the first occurrence of a delimiter. The
     * delimiter is not part of the result.
     * 
     * @param str String to get a substring from.
     * @param del String to search for.
     * @return Substring after the last occurrence of the delimiter.
     */
    public static String substringAfter(String str, String del) {
        int pos = str.indexOf(del);

        return pos >= 0 ? str.substring(pos + del.length()) : "";
    }
}
