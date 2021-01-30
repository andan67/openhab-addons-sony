package org.openhab.binding.sony.internal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SonyBindingTest {

    @Test
    void simpleNullStringTest() {
        String s = null;
        assertTrue(SonyUtil.isEmpty(s));
    }
}
