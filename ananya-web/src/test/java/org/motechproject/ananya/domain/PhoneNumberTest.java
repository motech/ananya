package org.motechproject.ananya.domain;

import org.junit.Test;

import static junit.framework.Assert.*;

public class PhoneNumberTest {
    @Test
    public void shouldCheckIfMsisdnIsValid() {
        assertTrue(new PhoneNumber("919876543210").isValid());
        assertTrue(new PhoneNumber("9876543210").isValid());
        assertFalse(new PhoneNumber("").isValid());
        assertFalse(new PhoneNumber(null).isValid());
        assertFalse(new PhoneNumber("91987654a21").isValid());
        assertFalse(new PhoneNumber("161234567890").isValid());
    }

    @Test
    public void shouldGetFormattedPhoneNumber() {
        assertEquals("919876543210", new PhoneNumber("9876543210").getFormattedMsisdn());
        assertEquals("919876543210", new PhoneNumber("919876543210").getFormattedMsisdn());
        assertNull(new PhoneNumber("9198760").getFormattedMsisdn());
    }
}
