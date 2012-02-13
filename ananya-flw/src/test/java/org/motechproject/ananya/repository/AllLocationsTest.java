package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class AllLocationsTest extends FrontLineWorkerBaseIT {
    @Autowired
    private AllLocations allLocations;

    @Test
    public void shouldFindLocationsByPanchayatCode() {
        Location expectedLocation = new Location("ZZZ999", "Mandwa", "Algarh", "Gujarat");
        String panchayatCode = "ZZZ999";
        allLocations.add(expectedLocation);
        markForDeletion(expectedLocation);

        Location location = allLocations.findByExternalId(panchayatCode);

        assertEquals(expectedLocation , location);
    }

    @Test
    public void shouldInsertNewLocationIfNotPresentInDB(){
        Location expectedLocation = new Location("ZZZ999", "Mandwa", "Algarh", "Gujarat");

        assertNull(allLocations.findByExternalId("ZZZ999"));

        allLocations.addOrUpdate(expectedLocation);
        markForDeletion(expectedLocation);

        assertNotNull(allLocations.findByExternalId("ZZZ999"));
    }

    @Test
    public void shouldUpdateLocationIfExistsInDB(){
        Location initialLocation = new Location("ZZZ999", "Mandwa", "Algarh", "Gujarat");

        allLocations.add(initialLocation);
        assertNotNull(allLocations.findByExternalId("ZZZ999"));

        Location updatedLocation = new Location("ZZZ999", "Patna", "Jila", "Panchayat");
        updatedLocation = allLocations.addOrUpdate(updatedLocation);
        markForDeletion(updatedLocation);

        Location existingDbLocation = allLocations.findByExternalId("ZZZ999");

        assertEquals(updatedLocation, existingDbLocation);
    }

}
