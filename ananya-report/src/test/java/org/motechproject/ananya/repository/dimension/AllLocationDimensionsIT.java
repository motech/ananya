package org.motechproject.ananya.repository.dimension;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AllLocationDimensionsIT extends SpringIntegrationTest {

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    private void deleteAllLocations() {
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Before
    public void setUp() {
        deleteAllLocations();
    }

    @Test
    public void shouldGetTheLocationBasedOnTheExternalId() {
        String district = "Mandwa";
        String block = "Algarh";
        String panchayat = "Gujarat";
        String state = "bihar";
        LocationDimension initialLocationDimension = new LocationDimension("ZZZ999", state, district, block, panchayat, "VALID");
        allLocationDimensions.saveOrUpdate(initialLocationDimension);

        LocationDimension locationDimension = allLocationDimensions.getFor("ZZZ999");

        assertNotNull(locationDimension);
        assertEquals(state, locationDimension.getState());
        assertEquals(district, locationDimension.getDistrict());
        assertEquals(block, locationDimension.getBlock());
        assertEquals(panchayat, locationDimension.getPanchayat());
    }

    @Test
    public void shouldFilterLocationBasedOnStateDistrictBlockAndPanchayat() {
        LocationDimension initialLocationDimension = new LocationDimension("ZZZ100", "S1", "d1", "B1", "P1", "VALID");
        LocationDimension initialLocationDimension1 = new LocationDimension("ZZZ101", "S1", "D1", "B2", "P2", "VALID");
        LocationDimension initialLocationDimension2 = new LocationDimension("ZZZ102", "S1", "D1", "B2", "P3", "VALID");
        LocationDimension initialLocationDimension3 = new LocationDimension("ZZZ103", "S1", "D2", "B3", "P4", "VALID");
        LocationDimension initialLocationDimension4 = new LocationDimension("ZZZ104", "S2", "D2", "B3", "P4", "VALID");
        allLocationDimensions.saveOrUpdate(initialLocationDimension);
        allLocationDimensions.saveOrUpdate(initialLocationDimension1);
        allLocationDimensions.saveOrUpdate(initialLocationDimension2);
        allLocationDimensions.saveOrUpdate(initialLocationDimension3);
        allLocationDimensions.saveOrUpdate(initialLocationDimension4);

        List<LocationDimension> locationDimensions = allLocationDimensions.getFilteredLocationFor(null, "D1", null, null);
        assertEquals(3, locationDimensions.size());

        locationDimensions = allLocationDimensions.getFilteredLocationFor(null, null, "B2", null);
        assertEquals(2, locationDimensions.size());

        locationDimensions = allLocationDimensions.getFilteredLocationFor(null, null, null, "P1");
        assertEquals(1, locationDimensions.size());

        locationDimensions = allLocationDimensions.getFilteredLocationFor(null, "D1", "B2", "P2");
        assertEquals(1, locationDimensions.size());
        
        locationDimensions = allLocationDimensions.getFilteredLocationFor("S2", null, null, null);
        assertEquals(1, locationDimensions.size());
    }

    @After
    public void clearAllLocations() {
        deleteAllLocations();
    }
}
