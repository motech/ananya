package org.motechproject.ananya.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LocationListTest {

    private LocationList locationList;

    @Before
    public void setUp() {
        ArrayList<Location> allLocations = new ArrayList<Location>();
        allLocations.add(new Location("S1", "D1", "B1", "P1", 1, 1, 1, null, null));
        allLocations.add(new Location("S1", "D1", "B1", "P2", 1, 1, 2, null, null));
        allLocations.add(new Location("S1", "D1", "B2", "P1", 1, 2, 1, null, null));
        allLocations.add(new Location("S1", "D2", "B1", "P1", 2, 1, 1, null, null));
        locationList = new LocationList(allLocations);
    }

    @Test
    public void shouldReturnThatLocationIsAlreadyPresentIfItIsAlreadyPresent() {
        Location currentLocation = new Location("S1", "D1", "B1", "P1", 1, 1, 1, null, null);

        boolean alreadyPresent = locationList.isAlreadyPresent(currentLocation);

        assertTrue(alreadyPresent);
    }

    @Test
    public void shouldReturnThatLocationIsNotPresentIfItIsNotAlreadyPresent() {
        Location currentLocation = new Location("Unknown", "UnknownD1", "UnknownB1", "UnknownP1", 0, 0, 0, null, null);

        boolean alreadyPresent = locationList.isAlreadyPresent(currentLocation);

        assertFalse(alreadyPresent);
    }

    @Test
    public void shouldReturnTheDistrictCodeForTheGivenLocation() {
        Location currentLocation = new Location("S1", "D1", "B1", "P2", 0, 0, 0, null, null);

        Integer districtCode = locationList.getDistrictCodeFor(currentLocation);

        assertEquals(new Integer(1), districtCode);
    }

    @Test
    public void shouldReturnTheNextDistrictCodeIfTheGivenDistrictIsNotPresent() {
        Location currentLocation = new Location("S1", "D3", "B1", "P2", 0, 0, 0, null, null);

        Integer districtCode = locationList.getDistrictCodeFor(currentLocation);

        assertEquals(new Integer(3), districtCode);
    }

    @Test
    public void shouldReturnTheNextBlockCodeIfTheGivenBlockIsNotPresent() {
        Location currentLocation = new Location("S1", "D1", "B3", "P2", 0, 0, 0, null, null);

        Integer blockCode = locationList.getBlockCodeFor(currentLocation);

        assertEquals(new Integer(3), blockCode);
    }

    @Test
    public void shouldReturnTheBlockCodeForTheGivenLocation() {
        Location currentLocation = new Location("S1", "D1", "B1", "P2", 0, 0, 0, null, null);

        Integer blockCode = locationList.getBlockCodeFor(currentLocation);

        assertEquals(new Integer(1), blockCode);
    }

    @Test
    public void shouldReturnTheNextPanchayatCodeForTheGivenLocation() {
        Location currentLocation = new Location("S1", "D1", "B1", "P3", 0, 0, 0, null, null);

        Integer blockCode = locationList.getPanchayatCodeFor(currentLocation);

        assertEquals(new Integer(3), blockCode);
    }

    @Test
    public void shouldGetUniqueDistrictBlockDefaultLocation() {
        List<Location> locations = new ArrayList<Location>();
        Location location1 = new Location("S1", "D1", "B1", "P1", 1, 1, 1, null, null);
        locations.add(location1);
        Location location1Panchayat2 = new Location("S1", "D1", "B1", "P2", 1, 1, 2, null, null);
        locations.add(location1Panchayat2);
        Location location2 = new Location("S1", "D1", "B2", "P3", 1, 2, 1, null, null);
        locations.add(location2);
        LocationList locationList = new LocationList(locations);

        List<Location> uniqueDistrictBlockLocations = locationList.getUniqueDistrictBlockLocations();

        assertEquals(2, uniqueDistrictBlockLocations.size());
        Location firstDefaultLocation = uniqueDistrictBlockLocations.get(0);
        Location expectedFirstDefaultLocation = new Location("S1", "D1", "B1", "", 1, 1, 0, null, null);
        assertEquals(expectedFirstDefaultLocation, firstDefaultLocation);
        Location secondDefaultLocation = uniqueDistrictBlockLocations.get(1);
        Location expectedSecondDefaultLocation = new Location("S1", "D1", "B2", "", 1, 2, 0, null, null);
        assertEquals(expectedSecondDefaultLocation, secondDefaultLocation);
    }

    @Test
    public void shouldNotAddADefaultLocationIfTheDefaultLocationIsAlreadyPresentInTheLocationList() {
        List<Location> locations = new ArrayList<Location>();
        Location location1 = new Location("S1", "D1", "B1", "P1", 1, 1, 1, null, null);
        locations.add(location1);
        Location defaultLocation = new Location("S1", "D1", "B1", "", 1, 1, 0, null, null);
        locations.add(defaultLocation);
        LocationList locationList = new LocationList(locations);

        List<Location> uniqueDistrictBlockLocations = locationList.getUniqueDistrictBlockLocations();

        assertEquals(0, uniqueDistrictBlockLocations.size());
    }

    @Test
    public void shouldGetForAGivenDistrictBlockPanchayatCombination() {
        List<Location> locations = new ArrayList<>();
        Location expectedLocation = new Location("S1", "D1", "B1", "P1", 1, 1, 1, null, null);
        locations.add(expectedLocation);
        locations.add(new Location("S1", "D1", "B1", "P2", 1, 1, 2, null, null));
        LocationList locationList = new LocationList(locations);

        Location actualLocation = locationList.getFor("S1", "D1", "B1", "P1");

        assertEquals(expectedLocation.getExternalId(), actualLocation.getExternalId());
    }
}
