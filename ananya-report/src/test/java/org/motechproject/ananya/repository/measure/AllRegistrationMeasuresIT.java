package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
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

public class AllRegistrationMeasuresIT extends SpringIntegrationTest {

    @Autowired
    AllRegistrationMeasures allRegistrationMeasures;


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
        long msisdn = 123L;
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(msisdn,
                "airtel", "bihar", "name", Designation.ANM.name(), RegistrationStatus.PARTIALLY_REGISTERED.toString(), UUID.randomUUID());
        template.save(frontLineWorkerDimension);
        String locationId = "locationId";
        LocationDimension locationDimension = new LocationDimension(locationId, "district", "block", "panchayat", "VALID");
        template.save(locationDimension);
        TimeDimension timeDimension = new TimeDimension(DateTime.now());
        template.save(timeDimension);
        template.save(new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, ""));

        List<RegistrationMeasure> registrationMeasures = allRegistrationMeasures.findByLocationId(locationId);

        assertEquals(1, registrationMeasures.size());
        assertEquals(msisdn, (long)registrationMeasures.get(0).getFrontLineWorkerDimension().getMsisdn());
    }
}
