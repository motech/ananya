package org.motechproject.ananya.seed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.service.LocationDimensionService;
import org.motechproject.ananya.service.LocationService;
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
    private LocationService locationService;

    @Autowired
    private LocationDimensionService locationDimensionService;

    @Autowired
    private DataAccessTemplate template;

    @Before
    public void setUp() {
        locationService.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldLoadAllTheLocationsFromTheCSVFile() throws IOException {
        String path = getClass().getResource("/locations_with_codes.csv").getPath();
        locationSeed.loadFromCsv(path);

        List<Location> allLocations = this.locationService.getAll();
        Location location = allLocations.get(1);
        String externalId = location.getExternalId();
        LocationDimension locationDimension = locationDimensionService.getFor(externalId);

        assertEquals(16, allLocations.size());
        assertEquals(location.getDistrict(), locationDimension.getDistrict());
        assertEquals(location.getBlock(), locationDimension.getBlock());
        assertEquals(location.getPanchayat(), locationDimension.getPanchayat());
    }

    @After
    public void tearDown() {
        locationService.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }
}
