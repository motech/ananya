package org.motechproject.ananya.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;

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

    @After
    public void clearAllLocations() {
        deleteAllLocations();
    }

    @Test
    public void shouldInsertNewLocationDimensionIfNotPresentInDB(){
        LocationDimension expectedLocationDimension = new LocationDimension("ZZZ999", "Mandwa", "Algarh", "Gujarat");

        assertNull(allLocationDimensions.fetchFor("ZZZ999"));

        allLocationDimensions.addOrUpdate(expectedLocationDimension);

        assertNotNull(allLocationDimensions.fetchFor("ZZZ999"));
    }

    @Test
    public void shouldUpdateLocationIfExistsInDB(){
        LocationDimension initialLocationDimension = new LocationDimension("ZZZ999", "Mandwa", "Algarh", "Gujarat");

        allLocationDimensions.add(initialLocationDimension);
        assertNotNull(allLocationDimensions.fetchFor("ZZZ999"));

        LocationDimension updatedLocationDimension = new LocationDimension("ZZZ999", "Patna", "Jila", "Panchayat");
        updatedLocationDimension = allLocationDimensions.addOrUpdate(updatedLocationDimension);

        LocationDimension existingDbLocationDimension = allLocationDimensions.fetchFor("ZZZ999");

        assertEquals(updatedLocationDimension, existingDbLocationDimension);
    }

}
