package org.motechproject.ananya.seed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.AllLocationDimensions;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class LocationSeedTest {

    @Autowired
    private LocationSeed locationSeed;

    @Autowired
    private AllLocations allLocations;

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    @Autowired
    private DataAccessTemplate template;

    @Before
    public void setUp() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldLoadDataFromCSVToTransactionalAndReportingDBs() throws IOException {

        String path = getClass().getResource("/locations_with_codes.csv").getPath();
        locationSeed.loadFromCsv(path);

        assertEquals(allLocations.getAll().size(), 56);
        assertEquals(allLocationDimensions.getCount(), 56);

        Location location = allLocations.findByExternalId("S01D001");
        assertEquals(location.district(), "Patna");

        location = allLocations.findByExternalId("S01D001B001");
        assertEquals(location.district(), "Patna");
        assertEquals(location.blockName(), "Dulhin Bazar");

        location = allLocations.findByExternalId("S01D001B001V003");
        assertEquals(location.district(), "Patna");
        assertEquals(location.blockName(), "Dulhin Bazar");
        assertEquals(location.panchayat(), "Bharatpura");

        location = allLocations.findByExternalId("S01D003");
        assertEquals(location.district(), "West Champaran");

        location = allLocations.findByExternalId("S01D003B001");
        assertEquals(location.district(), "West Champaran");
        assertEquals(location.blockName(), "Majhhaulia");

        location = allLocations.findByExternalId("S01D003B001V003");
        assertEquals(location.district(), "West Champaran");
        assertEquals(location.blockName(), "Majhhaulia");
        assertEquals(location.panchayat(), "Bahuarawa");
    }

    @After
    public void tearDown() {
        allLocations.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }
}
