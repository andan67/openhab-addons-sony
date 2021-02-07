package org.openhab.binding.sony.internal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SonyUtilTest {

    @Test
    public void toBooleanObjectTest() {
        assertTrue(SonyUtil.toBooleanObject("true"));
        assertFalse(SonyUtil.toBooleanObject("false"));
        assertTrue(!Boolean.FALSE.equals(SonyUtil.toBooleanObject("true")));
        assertFalse(!Boolean.FALSE.equals(SonyUtil.toBooleanObject("false")));
        assertTrue(!Boolean.FALSE.equals(SonyUtil.toBooleanObject(null)));
    }
}
