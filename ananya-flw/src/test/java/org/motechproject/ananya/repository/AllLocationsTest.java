package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllLocationsTest extends SpringBaseIT {
    @Autowired
    private AllLocations allLocations;

    @Test
    public void shouldGetLocationByStateDistrictBlockPanchayat() {
        Location location1 = new Location("S1", "D1", "B1", "P1", 10, 11, 12, 13, null, null);
        allLocations.add(location1);
        markForDeletion(location1);

        Location location2 = new Location("S2", "D2", "B2", "P2", 20, 21, 22, 23, null, null);
        allLocations.add(location2);
        markForDeletion(location2);

        Location actualLocation = allLocations.findByStateDistrictBlockPanchayat("S1", "D1", "B1", "P1");
        assertNotNull(actualLocation);
        assertEquals("S1", actualLocation.getState());
        assertEquals("D1", actualLocation.getDistrict());
    }
/*    public void shouldGetLocationByDistrictBlockPanchayat() {
        Location location1 = new Location("D1", "B1", "P1", 11, 12, 13, null, null);
        allLocations.add(location1);
        markForDeletion(location1);

        Location location2 = new Location("D2", "B2", "P2", 21, 22, 23, null, null);
        allLocations.add(location2);
        markForDeletion(location2);

        Location actualLocation = allLocations.findByDistrictBlockPanchayat("D1", "B1", "P1");
        assertNotNull(actualLocation);
        assertEquals("D1", actualLocation.getDistrict());
    }
*/
    @Test
    public void shouldGetLocationByStateDistrictBlockPanchayatAndShouldBeCaseInsensitive() {
        Location location1 = new Location("S1", "D1", "B1", "p1", 10, 11, 12, 13, null, null);
        allLocations.add(location1);
        markForDeletion(location1);

        Location actualLocation = allLocations.findByStateDistrictBlockPanchayat("s1", "d1", "B1", "P1");
        assertNotNull(actualLocation);
        assertEquals("S1", actualLocation.getState());
    }
    /*public void shouldGetLocationByDistrictBlockPanchayatAndShouldBeCaseInsensitive() {
        Location location1 = new Location("D1", "B1", "p1", 11, 12, 13, null, null);
        allLocations.add(location1);
        markForDeletion(location1);

        Location actualLocation = allLocations.findByDistrictBlockPanchayat("d1", "B1", "P1");
        assertNotNull(actualLocation);
        assertEquals("D1", actualLocation.getDistrict());
    }*/
}
