package org.motechproject.ananya.domain;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationTest {
    @Test
    public void shouldGetTheExternalIdBasedOnTheCodes() {
        Location location = new Location("Stat", "Dis", "Blo", "Pan", 4, 10, 9, 1, null, null);

        String externalId = location.getExternalId();

//        assertEquals("S01D010B009V001", externalId);
        assertEquals("S04D010B009V001", externalId);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfStateIsEmpty() {
        Location location = new Location("", "Dis", "Blo", "Pan", 0, 0, 0, 0, null, null);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }
    
    @Test
    public void shouldTellThatDetailsAreMissingIfDistrictIsEmpty() {
        Location location = new Location("Stat", "", "Blo", "Pan", 0, 0, 0, 0, null, null);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfBlockIsEmpty() {
        Location location = new Location("Stat", "Dis", " ", "Pan", 0, 0, 0, 0, null, null);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatDetailsAreMissingIfPanchayatIsEmpty() {
        Location location = new Location("Stat", "Dis", "Blo", "", 0, 0, 0, 0, null, null);

        boolean missingDetails = location.isMissingDetails();

        assertTrue(missingDetails);
    }

    @Test
    public void shouldTellThatNoneOfTheDetailsAreMissingIfAllTheFourFieldsAreFilled() {
        Location location = new Location("State", "Dis", "Blo", "Pan", 0, 0, 0, 0, null, null);

        boolean missingDetails = location.isMissingDetails();

        assertFalse(missingDetails);
    }
    
    @Test
    public void shouldGetDefaultLocation() {
        Location defaultLocation = Location.getDefaultLocation();

        assertEquals("C00",defaultLocation.getState());
        assertEquals("C00",defaultLocation.getDistrict());
        assertEquals("C00",defaultLocation.getBlock());
        assertEquals("",defaultLocation.getPanchayat());
//      assertEquals("S01D000B000V000",defaultLocation.getExternalId());
        assertEquals("S00D000B000V000",defaultLocation.getExternalId());
    }

    @Test
    public void shouldConvertLocationToTitleCase() {
        Location location = new Location("StaT", "DIST","block","pA pb");

        location.convertToTitleCase();

        assertEquals("Stat", location.getState());
        assertEquals("Dist", location.getDistrict());
        assertEquals("Block", location.getBlock());
        assertEquals("Pa Pb", location.getPanchayat());
    }
}
