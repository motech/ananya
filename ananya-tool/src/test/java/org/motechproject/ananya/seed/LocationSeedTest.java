package org.motechproject.ananya.seed;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class LocationSeedTest {

    @Autowired
    private LocationSeed locationSeed;

    @Autowired
    private AllLocations allLocations;

    @Autowired
    private LocationDimensionService locationDimensionService;

    @Autowired
    private TestDataAccessTemplate template;

    @Before
    public void setUp() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldLoadAllTheLocationsFromTheCSVFile() throws IOException {
        locationSeed.loadLocationsFromCSVFile();

        List<Location> locations = allLocations.getAll();
        Location location = locations.get(1);
        String externalId = location.getExternalId();
        LocationDimension locationDimension = locationDimensionService.getFor(externalId);

        assertEquals(19, locations.size());
        Assert.assertEquals(location.getDistrict(), locationDimension.getDistrict());
        Assert.assertEquals(location.getBlock(), locationDimension.getBlock());
        Assert.assertEquals(location.getPanchayat(), locationDimension.getPanchayat());
    }

    @After
    public void tearDown() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }
}
