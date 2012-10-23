package org.motechproject.ananya.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.ValidationResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WebRequestValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldInvalidateAndThrowExceptionIfChannelIsInvalid() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();

        ValidationResponse validationResponse = webRequestValidator.validateChannel("invalid_channel");

        assertTrue(validationResponse.hasErrors());
        assertEquals(1, validationResponse.getErrors().size());
        assertEquals("Invalid channel: invalid_channel" + System.lineSeparator(), validationResponse.getErrorMessage());
    }
}
