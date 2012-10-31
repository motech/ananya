package org.motechproject.ananya.domain;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationTest {
    @Test
    public void shouldGetTheExternalIdBasedOnTheCodes() {
        Location location = new Location("Dis", "Blo", "Pan", 10, 9, 1, null);

        String externalId = location.getExternalId();

        assertEquals("S01D010B009V001", externalId);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfDistrictIsEmpty() {
        Location location = new Location("", "Blo", "Pan", 0, 0, 0, null);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfBlockIsEmpty() {
        Location location = new Location("Dis", " ", "Pan", 0, 0, 0, null);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfPanchayatIsEmpty() {
        Location location = new Location("Dis", "Blo", "", 0, 0, 0, null);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatNoneOfTheDetailsAreMissingIfAllTheThreeFieldsAreFilled() {
        Location location = new Location("Dis", "Blo", "Pan", 0, 0, 0, null);

        boolean missingDetails = location.isMissingDetails();

        assertFalse(missingDetails);
    }
    
    @Test
    public void shouldGetDefaultLocation() {
        Location defaultLocation = Location.getDefaultLocation();

        assertEquals("C00",defaultLocation.getDistrict());
        assertEquals("C00",defaultLocation.getBlock());
        assertEquals("",defaultLocation.getPanchayat());
        assertEquals("S01D000B000V000",defaultLocation.getExternalId());
    }
}
