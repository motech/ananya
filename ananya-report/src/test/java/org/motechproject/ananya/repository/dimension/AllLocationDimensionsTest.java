package org.motechproject.ananya.repository.dimension;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AllLocationDimensionsTest extends SpringIntegrationTest {

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
        LocationDimension initialLocationDimension = new LocationDimension("ZZZ999", district, block, panchayat);
        allLocationDimensions.add(initialLocationDimension);

        LocationDimension locationDimension = allLocationDimensions.getFor("ZZZ999");

        assertNotNull(locationDimension);
        assertEquals(district, locationDimension.getDistrict());
        assertEquals(block, locationDimension.getBlock());
        assertEquals(panchayat, locationDimension.getPanchayat());
    }

    @After
    public void clearAllLocations() {
        deleteAllLocations();
    }
}
