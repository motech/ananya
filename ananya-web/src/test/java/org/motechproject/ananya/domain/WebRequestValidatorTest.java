package org.motechproject.ananya.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.response.ValidationResponse;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WebRequestValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WebRequestValidator validator = new WebRequestValidator();

    @Test
    public void shouldValidateFlwId() {
        ValidationResponse validationResponse = new ValidationResponse();
        validator.validateFlwId("invalid_flwid", validationResponse);
        assertEquals("invalid flw id: invalid_flwid", validationResponse.getErrorMessage());


        validationResponse = new ValidationResponse();
        validator.validateFlwId(null, validationResponse);
        assertEquals("missing flw id", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateFlwId("", validationResponse);
        assertEquals("missing flw id", validationResponse.getErrorMessage());


        validationResponse = new ValidationResponse();
        validator.validateFlwId("  ", validationResponse);
        assertEquals("invalid flw id:   ", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateFlwId(UUID.randomUUID().toString(), validationResponse);
        assertFalse(validationResponse.hasErrors());
    }

    @Test
    public void shouldValidateChannel() {
        ValidationResponse validationResponse = new ValidationResponse();
        validator.validateChannel("invalid_channel", validationResponse);
        assertEquals("invalid channel: invalid_channel", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateChannel(null, validationResponse);
        assertEquals("missing channel", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateChannel("", validationResponse);
        assertEquals("missing channel", validationResponse.getErrorMessage());


        validationResponse = new ValidationResponse();
        validator.validateChannel("  ", validationResponse);
        assertEquals("invalid channel:   ", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateChannel("contact_Center  ", validationResponse);
        assertFalse(validationResponse.hasErrors());
    }

    @Test
    public void shouldValidateDateRangeForFormats() {
        ValidationResponse validationResponse = new ValidationResponse();
        validator.validateDateRange("invalid_start_date", "invalid_end_date", validationResponse);
        assertEquals("invalid start date: invalid_start_date,invalid end date: invalid_end_date", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateDateRange(null, null, validationResponse);
        assertEquals("invalid start date: null,invalid end date: null", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateDateRange("", "", validationResponse);
        assertEquals("invalid start date: ,invalid end date: ", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateDateRange("  ", "  ", validationResponse);
        assertEquals("invalid start date:   ,invalid end date:   ", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateDateRange("15-12-2009", "16-12-2009", validationResponse);
        assertFalse(validationResponse.hasErrors());
    }

    @Test
    public void shouldValidateIfEndDateIsBeforeOrSameAsStartDate() {
        ValidationResponse validationResponse = new ValidationResponse();
        validator.validateDateRange("15-12-2009", "15-12-2009", validationResponse);
        assertEquals("start date should be before end date", validationResponse.getErrorMessage());

        validationResponse = new ValidationResponse();
        validator.validateDateRange("16-12-2009", "15-12-2009", validationResponse);
        assertEquals("start date should be before end date", validationResponse.getErrorMessage());
    }
}
