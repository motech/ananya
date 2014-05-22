package org.motechproject.ananya.validators;

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

    @Test
    public void shouldValidateCallId() {
        assertTrue(ValidationUtils.isValidCallId("1234567890-12222"));
        assertFalse(ValidationUtils.isValidCallId("1234567890-12222-444"));
        assertFalse(ValidationUtils.isValidCallId("1234567890"));
        assertFalse(ValidationUtils.isValidCallId(""));
        assertFalse(ValidationUtils.isValidCallId(" "));
        assertFalse(ValidationUtils.isValidCallId("-"));
        assertFalse(ValidationUtils.isValidCallId(null));
        assertFalse(ValidationUtils.isValidCallId("1234567890-124a"));
        assertFalse(ValidationUtils.isValidCallId("123456789-12222"));
        assertFalse(ValidationUtils.isValidCallId("123456789a-12222"));
    }

    @Test
    public void shouldValidateCallerId() {
        assertTrue(ValidationUtils.isValidCallerId("9876543210"));
        assertTrue(ValidationUtils.isValidCallerId("09876543210"));
        assertTrue(ValidationUtils.isValidCallerId("919876543210"));
        assertFalse(ValidationUtils.isValidCallerId("987654321"));
        assertFalse(ValidationUtils.isValidCallerId("9876543210111"));
        assertFalse(ValidationUtils.isValidCallerId("987654321a"));
        assertFalse(ValidationUtils.isValidCallerId(""));
        assertFalse(ValidationUtils.isValidCallerId(" "));
        assertFalse(ValidationUtils.isValidCallerId(null));
    }

    @Test
    public void shouldValidateCalledNumber() {
        assertTrue(ValidationUtils.isValidCalledNumber("22333"));
        assertTrue(ValidationUtils.isValidCalledNumber("2"));
        assertFalse(ValidationUtils.isValidCalledNumber("2a"));
        assertFalse(ValidationUtils.isValidCalledNumber("a"));
        assertFalse(ValidationUtils.isValidCalledNumber(""));
        assertFalse(ValidationUtils.isValidCalledNumber(" "));
        assertFalse(ValidationUtils.isValidCalledNumber(null));
    }
}
