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
        allLocations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        allLocations.add(new Location("D1", "B1", "P2", 1, 1, 2));
        allLocations.add(new Location("D1", "B2", "P1", 1, 2, 1));
        allLocations.add(new Location("D2", "B1", "P1", 2, 1, 1));
        locationList = new LocationList(allLocations);
    }

    @Test
    public void shouldReturnThatLocationIsAlreadyPresentIfItIsAlreadyPresent() {
        Location currentLocation = new Location("D1", "B1", "P1", 1, 1, 1);

        boolean alreadyPresent = locationList.isAlreadyPresent(currentLocation);

        assertTrue(alreadyPresent);
    }

    @Test
    public void shouldReturnThatLocationIsNotPresentIfItIsNotAlreadyPresent() {
        Location currentLocation = new Location("UnknownD1", "UnknownB1", "UnknownP1", 0, 0, 0);

        boolean alreadyPresent = locationList.isAlreadyPresent(currentLocation);

        assertFalse(alreadyPresent);
    }

    @Test
    public void shouldReturnTheDistrictCodeForTheGivenLocation() {
        Location currentLocation = new Location("D1", "B1", "P2", 0, 0, 0);

        Integer districtCode = locationList.getDistrictCodeFor(currentLocation);

        assertEquals(new Integer(1), districtCode);
    }

    @Test
    public void shouldReturnTheNextDistrictCodeIfTheGivenDistrictIsNotPresent() {
        Location currentLocation = new Location("D3", "B1", "P2", 0, 0, 0);

        Integer districtCode = locationList.getDistrictCodeFor(currentLocation);

        assertEquals(new Integer(3), districtCode);
    }

    @Test
    public void shouldReturnTheNextBlockCodeIfTheGivenBlockIsNotPresent() {
        Location currentLocation = new Location("D1", "B3", "P2", 0, 0, 0);

        Integer blockCode = locationList.getBlockCodeFor(currentLocation);

        assertEquals(new Integer(3), blockCode);
    }

    @Test
    public void shouldReturnTheBlockCodeForTheGivenLocation() {
        Location currentLocation = new Location("D1", "B1", "P2", 0, 0, 0);

        Integer blockCode = locationList.getBlockCodeFor(currentLocation);

        assertEquals(new Integer(1), blockCode);
    }

    @Test
    public void shouldReturnTheNextPanchayatCodeForTheGivenLocation() {
        Location currentLocation = new Location("D1", "B1", "P3", 0, 0, 0);

        Integer blockCode = locationList.getPanchayatCodeFor(currentLocation);

        assertEquals(new Integer(3), blockCode);
    }

    @Test
    public void shouldFetchForGivenDistrictBlockAndPanchayat() {
        List<Location> locations = new ArrayList<Location>();
        Location location1 = new Location("D1", "B1", "P1", 1, 1, 1);
        locations.add(location1);
        Location location2 = new Location("D2", "B2", "P5", 1, 1, 1);
        locations.add(location2);
        Location location3 = new Location("D1", "B3", "P2", 1, 1, 1);
        locations.add(location3);

        LocationList locationList = new LocationList(locations);

        assertEquals(location1, locationList.findFor("D1", "b1", "P1 " +
                ""));
        assertEquals(location2, locationList.findFor("D2", "b2", "P5"));
        assertNull(locationList.findFor("D1", "b1", "P45"));
    }

    @Test
    public void shouldFetchTheDefaultLocationForAGivenBlockIfPanchayatIsNotPresent() {
        List<Location> locations = new ArrayList<Location>();
        Location location1 = new Location("D1", "B1", "P1", 1, 1, 1);
        locations.add(location1);
        Location defaultLocation = new Location("D1", "B1", "", 1, 1, 0);
        locations.add(defaultLocation);

        LocationList locationList = new LocationList(locations);

        assertEquals(defaultLocation, locationList.findFor("D1","B1","P2"));
    }

    @Test
    public void shouldGetUniqueDistrictBlockDefaultLocation(){
        List<Location> locations = new ArrayList<Location>();
        Location location1 = new Location("D1", "B1", "P1", 1, 1, 1);
        locations.add(location1);
        Location location1Panchayat2 = new Location("D1", "B1", "P2", 1, 1, 2);
        locations.add(location1Panchayat2);
        Location location2 = new Location("D1", "B2", "P3", 1, 2, 1);
        locations.add(location2);
        LocationList locationList = new LocationList(locations);

        List<Location> uniqueDistrictBlockLocations = locationList.getUniqueDistrictBlockLocations();

        assertEquals(2,uniqueDistrictBlockLocations.size());
        Location firstDefaultLocation = uniqueDistrictBlockLocations.get(0);
        Location expectedFirstDefaultLocation = new Location("D1","B1","",1,1,0);
        assertEquals(expectedFirstDefaultLocation, firstDefaultLocation);
        Location secondDefaultLocation = uniqueDistrictBlockLocations.get(1);
        Location expectedSecondDefaultLocation = new Location("D1","B2","",1,2,0);
        assertEquals(expectedSecondDefaultLocation, secondDefaultLocation);
    }

    @Test
    public void shouldNotAddADefaultLocationIfTheDefaultLocationIsAlreadyPresentInTheLocationList() {
        List<Location> locations = new ArrayList<Location>();
        Location location1 = new Location("D1", "B1", "P1", 1, 1, 1);
        locations.add(location1);
        Location defaultLocation = new Location("D1", "B1", "", 1, 1, 0);
        locations.add(defaultLocation);
        LocationList locationList = new LocationList(locations);

        List<Location> uniqueDistrictBlockLocations = locationList.getUniqueDistrictBlockLocations();

        assertEquals(0, uniqueDistrictBlockLocations.size());
    }
}
