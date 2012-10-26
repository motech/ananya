package org.motechproject.ananya.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilsTest {
    @Test
    public void shouldValidateTheFlwIdFormat() {
        String uuidInInvalidFormat = "abcd1234";
        assertFalse(ValidationUtils.isValidUUID(uuidInInvalidFormat));

        String uuidWithExtraCharacters = "1234dadb-1234-1234-9876-abcdeef2345689";
        assertFalse(ValidationUtils.isValidUUID(uuidWithExtraCharacters));

        String uuidWithInvalidCharacters = "1234dadb-1234-1234-9876-abcdef1234xy";
        assertFalse(ValidationUtils.isValidUUID(uuidWithInvalidCharacters));

        String validUuid = "1234dadb-1234-1234-9876-abcdef1234ab";
        assertTrue(ValidationUtils.isValidUUID(validUuid));
    }
}
