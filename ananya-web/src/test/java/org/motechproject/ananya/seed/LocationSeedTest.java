package org.motechproject.ananya.seed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.service.LocationDimensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class LocationSeedTest {

    @Autowired
    private LocationSeed locationSeed;

    @Autowired
    private AllLocations allLocations;

    @Autowired
    private LocationDimensionService locationDimensionService;

    @Autowired
    private DataAccessTemplate template;

    @Before
    public void setUp() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldLoadAllTheLocationsFromTheCSVFile() throws IOException {
        locationSeed.load();

        List<Location> locations = allLocations.getAll();
        Location location = locations.get(1);
        String externalId = location.getExternalId();
        LocationDimension locationDimension = locationDimensionService.getFor(externalId);

        assertEquals(17, locations.size());
        assertEquals(location.getDistrict(), locationDimension.getDistrict());
        assertEquals(location.getBlock(), locationDimension.getBlock());
        assertEquals(location.getPanchayat(), locationDimension.getPanchayat());
    }

    @After
    public void tearDown() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }
}
