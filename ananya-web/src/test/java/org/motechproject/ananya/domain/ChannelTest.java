package org.motechproject.ananya.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ChannelTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldInvalidateIncorrectChannel() {
        assertFalse(Channel.isValid("invalid_channel"));
        assertFalse(Channel.isValid(""));
        assertFalse(Channel.isValid("  "));
        assertFalse(Channel.isValid(null));
    }

    @Test
    public void shouldValidateCorrectChannel() {
        assertTrue(Channel.isValid("  contact_centER  "));
    }

    @Test
    public void shouldThrowExceptionWhileParsingInvalidChannel() {
        expectedException.expect(IllegalArgumentException.class);
        Channel.from("invalid_channel");
    }

    @Test
    public void shouldParseValidChannel() {
        assertEquals(Channel.CONTACT_CENTER, Channel.from("  contact_CentER  "));
    }
}
