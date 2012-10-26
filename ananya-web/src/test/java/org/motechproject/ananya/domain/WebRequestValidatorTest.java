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
    public void shouldValidateFlwIdAndChannel(){
        ValidationResponse response = WebRequestValidator.validate("abcd1234", "invalid_channel");

        assertTrue(response.hasErrors());
        assertEquals(2, response.getErrors().size());
        assertEquals("Invalid channel: invalid_channel,Invalid flwGuid: abcd1234", response.getErrorMessage());
    }

    @Test
    public void shouldInvalidateTheFlwIdFormat(){
        String uuidInInvalidFormat = "abcd1234";

        ValidationResponse response = WebRequestValidator.validate(uuidInInvalidFormat, "contact_center");

        assertTrue(response.hasErrors());
        assertEquals(1, response.getErrors().size());
        assertEquals("Invalid flwGuid: " + uuidInInvalidFormat , response.getErrorMessage());
    }

    @Test
    public void shouldInvalidateTheFlwIdWithExtraCharacters(){
        String uuidWithExtraCharacters = "1234dadb-1234-1234-9876-abcdeef2345689";

        ValidationResponse response = WebRequestValidator.validate(uuidWithExtraCharacters, "contact_center");

        assertTrue(response.hasErrors());
        assertEquals(1, response.getErrors().size());
        assertEquals("Invalid flwGuid: " + uuidWithExtraCharacters , response.getErrorMessage());
    }

    @Test
    public void shouldInvalidateFlwIdWithInvalidCharacters(){
        String uuidWithInvalidCharacters = "1234dadb-1234-1234-9876-abcdef1234xy";

        ValidationResponse response = WebRequestValidator.validate(uuidWithInvalidCharacters, "contact_center");

        assertTrue(response.hasErrors());
        assertEquals(1, response.getErrors().size());
        assertEquals("Invalid flwGuid: " + uuidWithInvalidCharacters , response.getErrorMessage());
    }
}
