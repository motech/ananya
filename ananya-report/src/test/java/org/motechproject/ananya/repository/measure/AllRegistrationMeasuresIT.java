package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AllRegistrationMeasuresIT extends SpringIntegrationTest {

    @Autowired
    AllRegistrationMeasures allRegistrationMeasures;
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private String locationId;
    private LocationDimension locationDimension;
    private TimeDimension timeDimension;
    private RegistrationMeasure registrationMeasure;
    private long msisdn;

    @Before
    public void setUp(){
        tearDown();
        msisdn = 123L;
        frontLineWorkerDimension = new FrontLineWorkerDimension(msisdn,
                null, "airtel", "bihar", "name", Designation.ANM.name(), RegistrationStatus.PARTIALLY_REGISTERED.toString(), UUID.randomUUID(), null);
        template.save(frontLineWorkerDimension);
        locationId = "locationId";
        locationDimension = new LocationDimension(locationId, "state", "district", "block", "panchayat", "VALID");
        template.save(locationDimension);
        timeDimension = new TimeDimension(DateTime.now());
        template.save(timeDimension);
        registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "");
        template.save(registrationMeasure);
    }

    @After
    public void tearDown(){
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(RegistrationMeasure.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertRegistrationMeasureWhenFLWDimensionIsNull() {
        template.save(new RegistrationMeasure(null, new LocationDimension(), new TimeDimension(), ""));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertRegistrationMeasureWhenLocationDimensionIsNull() {
        template.save(new RegistrationMeasure(new FrontLineWorkerDimension(), null, new TimeDimension(), ""));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertRegistrationMeasureWhenTimeDimensionIsNull() {
        template.save(new RegistrationMeasure(new FrontLineWorkerDimension(), new LocationDimension(), null, ""));
    }

    @Test
    public void shouldFindAllRegistrationMeasuresByLocationId() {
        List<RegistrationMeasure> registrationMeasures = allRegistrationMeasures.findByLocationId(locationId);

        assertEquals(1, registrationMeasures.size());
        assertEquals(msisdn, (long)registrationMeasures.get(0).getFrontLineWorkerDimension().getMsisdn());
    }

    @Test
    public void shouldRemoveRegistrationMeasure() {
        assertNotNull(allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId()));

        allRegistrationMeasures.remove(registrationMeasure);

        assertNull(allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId()));

    }
}
