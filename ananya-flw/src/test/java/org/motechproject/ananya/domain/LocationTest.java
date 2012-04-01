package org.motechproject.ananya.domain;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationTest {
    @Test
    public void shouldGetTheExternalIdBasedOnTheCodes() {
        Location location = new Location("Dis", "Blo", "Pan", 10, 9, 1);

        String externalId = location.getExternalId();

        assertEquals("S01D010B009V001", externalId);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfDistrictIsEmpty() {
        Location location = new Location("", "Blo", "Pan", 0, 0, 0);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfBlockIsEmpty() {
        Location location = new Location("Dis", " ", "Pan", 0, 0, 0);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfPanchayatIsEmpty() {
        Location location = new Location("Dis", "Blo", "", 0, 0, 0);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatNoneOfTheDetailsAreMissingIfAllTheThreeFieldsAreFilled() {
        Location location = new Location("Dis", "Blo", "Pan", 0, 0, 0);

        boolean missingDetails = location.isMissingDetails();

        assertFalse(missingDetails);
    }
}
