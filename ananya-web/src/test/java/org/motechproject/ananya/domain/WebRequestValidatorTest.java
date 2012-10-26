package org.motechproject.ananya.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.ValidationResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WebRequestValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldInvalidateFlwIdAndChannel(){
        ValidationResponse response = WebRequestValidator.validate("abcd1234", "invalid_channel");

        assertTrue(response.hasErrors());
        assertEquals(2, response.getErrors().size());
        assertEquals("Invalid channel: invalid_channel,Invalid flwId: abcd1234", response.getErrorMessage());
    }

    @Test
    public void shouldValidateFlwIdAndChannel(){
        ValidationResponse response = WebRequestValidator.validate("1234dadb-1234-1234-9876-abcdef1234ab", "contact_center");

        assertFalse(response.hasErrors());
    }
}
