package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

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
}
