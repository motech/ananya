package org.motechproject.ananya.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class LocationDetailsControllerTest extends SpringIntegrationTest {

    @Autowired
    private LocationDetailsController locationDetailsController;
    @Autowired
    private AllLocations allLocations;
    @Autowired
    private AllLocationDimensions allLocationDimensions;
    @Autowired
    private TestDataAccessTemplate template;

    @Before
    @After
    public void setUp() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldCreateNewLocationDetail() {
        String panchayat = "Charkot";
        String block = "Amer";
        String district = "Patna";

        locationDetailsController.create(district, block, panchayat);

        List<Location> allLocations = this.allLocations.getAll();
        assertEquals(1, allLocations.size());
        assertEquals(district, allLocations.get(0).getDistrict());
        assertEquals(block, allLocations.get(0).getBlock());
        assertEquals(panchayat, allLocations.get(0).getPanchayat());
    }

    @Test
    public void shouldNotDuplicateLocationDetail() {
        String panchayat = "Charkot";
        String block = "Amer";
        String district = "Patna";
        locationDetailsController.create(district, block, panchayat);

        locationDetailsController.create(district, block, panchayat);

        List<Location> allLocations = this.allLocations.getAll();
        assertEquals(1, allLocations.size());
        assertEquals(district, allLocations.get(0).getDistrict());
        assertEquals(block, allLocations.get(0).getBlock());
        assertEquals(panchayat, allLocations.get(0).getPanchayat());
    }
}
