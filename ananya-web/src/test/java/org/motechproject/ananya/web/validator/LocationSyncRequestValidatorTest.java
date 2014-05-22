package org.motechproject.ananya.web.validator;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.request.LocationSyncRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class LocationSyncRequestValidatorTest {
    @Test
    public void shouldInvalidateForBlankFields() {
        Errors errors = LocationSyncRequestValidator.validate(new LocationSyncRequest(new LocationRequest(), new LocationRequest(), "", null));

        assertEquals(4, errors.getCount());
        assertTrue(errors.hasMessage("LocationRequest is null or has blank fields"));
        assertTrue(errors.hasMessage("Location Status is invalid"));
        assertTrue(errors.hasMessage("Last Modified Time is blank"));
    }

    @Test
    public void shouldNotHaveErrorsForAValidRequest() {
        LocationRequest locationRequest = new LocationRequest("s", "d", "b", "p");

        Errors errors = LocationSyncRequestValidator.validate(new LocationSyncRequest(locationRequest, locationRequest, "VALID", DateTime.now()));

        assertEquals(0, errors.getCount());
    }
}
