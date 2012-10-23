package org.motechproject.ananya.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ChannelTest {

    @Test
    public void shouldInvalidateIncorrectChannel() {
        assertTrue(Channel.isInvalid("invalid_channel"));
    }

    @Test
    public void shouldValidateCorrectChannel() {
        assertFalse(Channel.isInvalid("contact_center  "));
    }
}
