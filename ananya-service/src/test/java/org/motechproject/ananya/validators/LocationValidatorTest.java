package org.motechproject.ananya.validators;

import org.junit.Test;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.response.LocationValidationResponse;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LocationValidatorTest {
    @Test
    public void shouldFailValidationIfOneOfStateDistrictBlockOrPanchayatIsBlank() {
        LocationValidator locationValidator = new LocationValidator(new LocationList(new ArrayList<Location>()));

        LocationValidationResponse validationResponse = locationValidator.validate(new Location("", "D1", "B1", "P1", 0, 0, 0, 0, null, null));

        assertFalse(validationResponse.isValid());
        assertEquals("[One or more of District, Block, Panchayat details are missing]", validationResponse.getMessage());
        
        validationResponse = locationValidator.validate(new Location("S1", "", "B1", "P1", 0, 0, 0, 0, null, null));

        assertFalse(validationResponse.isValid());
        assertEquals("[One or more of District, Block, Panchayat details are missing]", validationResponse.getMessage());

        validationResponse = locationValidator.validate(new Location("S1", "D1", null, "P1", 0, 0, 0, 0, null, null));

        assertFalse(validationResponse.isValid());
        assertEquals("[One or more of District, Block, Panchayat details are missing]", validationResponse.getMessage());

        validationResponse = locationValidator.validate(new Location("S1", "D1", "B1", "", 0, 0, 0, 0, null, null));

        assertFalse(validationResponse.isValid());
        assertEquals("[One or more of District, Block, Panchayat details are missing]", validationResponse.getMessage());

        validationResponse = locationValidator.validate(new Location("S1", "D1", "B1", "P1", 0, 0, 0, 0, null, null));

        assertTrue(validationResponse.isValid());
    }

    @Test
    public void shouldFailValidationIfLocationIsAlreadyPresent() {
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("S1", "D1", "B1", "P1", 0, 0, 0, 0, null, null));
        LocationValidator locationValidator = new LocationValidator(new LocationList(locations));

        LocationValidationResponse validationResponse = locationValidator.validate(new Location("S1", "D1", "B1", "P1", 0, 0, 0, 0, null, null));

        assertFalse(validationResponse.isValid());
        assertEquals("[The location is already present]", validationResponse.getMessage());
    }
}
