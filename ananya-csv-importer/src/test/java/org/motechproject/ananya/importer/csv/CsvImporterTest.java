package org.motechproject.ananya.importer.csv;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.importer.csv.exception.FileReadException;
import org.motechproject.ananya.importer.csv.exception.InvalidArgumentException;
import org.motechproject.ananya.importer.csv.exception.WrongNumberArgsException;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CsvImporterTest extends SpringIntegrationTest {
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllLocations allLocations;

    @Before
    @After
    public void setUp() {
        allFrontLineWorkers.removeAll();
        allLocations.removeAll();
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    public void shouldImportFlwData() throws Exception {
        template.save(new TimeDimension(DateTime.now()));
        Location location = new Location("S1", "D1", "B1", "P1", 9, 9, 9, 9, null, null);
        allLocations.add(location);
        template.save(new LocationDimension(location.getExternalId(), location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat(), "VALID"));
        URL flwData = this.getClass().getResource("/flwData.csv");
        String[] arguments = {"FrontLineWorker", flwData.getPath()};

        CsvImporter.main(arguments);

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = template.loadAll(FrontLineWorkerDimension.class);
        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals("919988776655", frontLineWorkerDimensions.get(0).getMsisdn().toString());
    }

    @Test
    @ExpectedException(InvalidArgumentException.class)
    public void shouldFailForRandomEntityNames() throws Exception {
        Location location = new Location("S1", "D1", "B1", "P1", 9, 9, 9, 9, null, null);
        allLocations.add(location);
        template.save(new LocationDimension(location.getExternalId(), location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat(), "VALID"));
        URL flwData = this.getClass().getResource("/flwData.csv");
        String[] arguments = {"RandomEntityName", flwData.getPath()};

        CsvImporter.main(arguments);
    }

    @Test
    @ExpectedException(WrongNumberArgsException.class)
    public void shouldFailForWrongNumberOfArguments() throws Exception {
        Location location = new Location("S1", "D1", "B1", "P1", 9, 9, 9, 9, null, null);
        allLocations.add(location);
        template.save(new LocationDimension(location.getExternalId(), location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat(), "VALID"));
        URL flwData = this.getClass().getResource("/flwData.csv");
        String[] arguments = {"FrontLineWorker", flwData.getPath(), "unwanted-argument"};

        CsvImporter.main(arguments);
    }

    @Test
    @ExpectedException(FileReadException.class)
    public void shouldFailForInvalidImportFile() throws Exception {
        Location location = new Location("S1", "D1", "B1", "P1", 9, 9, 9, 9, null, null);
        allLocations.add(location);
        template.save(new LocationDimension(location.getExternalId(), location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat(), "VALID"));
        String[] arguments = {"FrontLineWorker", "random-file-path.csv"};

        CsvImporter.main(arguments);
    }
}
