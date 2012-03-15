package org.motechproject.ananya.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationsTest {

    private Locations locations;

    @Before
    public void setUp() {
        ArrayList<Location> allLocations = new ArrayList<Location>();
        allLocations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        allLocations.add(new Location("D1", "B1", "P2", 1, 1, 2));
        allLocations.add(new Location("D1", "B2", "P1", 1, 2, 1));
        allLocations.add(new Location("D2", "B1", "P1", 2, 1, 1));
        locations = new Locations(allLocations);
    }

    @Test
    public void shouldReturnThatLocationIsAlreadyPresentIfItIsAlreadyPresent() {
        Location currentLocation = new Location("D1", "B1", "P1", 1, 1, 1);

        boolean alreadyPresent = locations.isAlreadyPresent(currentLocation);

        assertTrue(alreadyPresent);
    }

    @Test
    public void shouldReturnThatLocationIsNotPresentIfItIsNotAlreadyPresent() {
        Location currentLocation = new Location("UnknownD1", "UnknownB1", "UnknownP1", 0, 0, 0);

        boolean alreadyPresent = locations.isAlreadyPresent(currentLocation);

        assertFalse(alreadyPresent);
    }

    @Test
    public void shouldReturnTheDistrictCodeForTheGivenLocation() {
        Location currentLocation = new Location("D1", "B1", "P2", 0, 0, 0);

        Integer districtCode = locations.getDistrictCodeFor(currentLocation);

        assertEquals(new Integer(1), districtCode);
    }

    @Test
    public void shouldReturnTheNextDistrictCodeIfTheGivenDistrictIsNotPresent() {
        Location currentLocation = new Location("D3", "B1", "P2", 0, 0, 0);

        Integer districtCode = locations.getDistrictCodeFor(currentLocation);

        assertEquals(new Integer(3), districtCode);
    }

    @Test
    public void shouldReturnTheNextBlockCodeIfTheGivenBlockIsNotPresent() {
        Location currentLocation = new Location("D1", "B3", "P2", 0, 0, 0);

        Integer blockCode = locations.getBlockCodeFor(currentLocation);

        assertEquals(new Integer(3), blockCode);
    }

    @Test
    public void shouldReturnTheBlockCodeForTheGivenLocation() {
        Location currentLocation = new Location("D1", "B1", "P2", 0, 0, 0);

        Integer blockCode = locations.getBlockCodeFor(currentLocation);

        assertEquals(new Integer(1), blockCode);
    }

    @Test
    public void shouldReturnTheNextPanchayatCodeForTheGivenLocation() {
        Location currentLocation = new Location("D1", "B1", "P3", 0, 0, 0);

        Integer blockCode = locations.getPanchayatCodeFor(currentLocation);

        assertEquals(new Integer(3), blockCode);
    }
}
