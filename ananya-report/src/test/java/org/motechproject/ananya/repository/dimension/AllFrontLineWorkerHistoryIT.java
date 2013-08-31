package org.motechproject.ananya.repository.dimension;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerHistory;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.*;

@Transactional
public class AllFrontLineWorkerHistoryIT extends SpringIntegrationTest {

    @Autowired
    private AllFrontLineWorkerHistory allFrontLineWorkerHistory;

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Test
    public void shouldGetCurrentHistory() {
        FrontLineWorkerDimension flwDimension = getFlwDimension();
        FrontLineWorkerHistory oldFrontLineWorkerHistory = new FrontLineWorkerHistory(getRegistrationMeasure(flwDimension));
        oldFrontLineWorkerHistory.markOld();
        allFrontLineWorkerHistory.createOrUpdate(oldFrontLineWorkerHistory);
        FrontLineWorkerHistory currentFrontLineWorkerHistory = new FrontLineWorkerHistory(getRegistrationMeasure(flwDimension));
        allFrontLineWorkerHistory.createOrUpdate(currentFrontLineWorkerHistory);

        FrontLineWorkerHistory current = allFrontLineWorkerHistory.getCurrent(flwDimension.getId());

        assertTrue(current.isSame(currentFrontLineWorkerHistory));
    }

    @Test
    public void shouldCreateNewFrontLineWorkerHistory() {
        FrontLineWorkerHistory frontLineWorkerHistory = new FrontLineWorkerHistory(getRegistrationMeasure(getFlwDimension()));
        assertNull(frontLineWorkerHistory.getId());
        allFrontLineWorkerHistory.createOrUpdate(frontLineWorkerHistory);
        assertNotNull(frontLineWorkerHistory.getId());
    }

    private FrontLineWorkerDimension getFlwDimension() {
        return allFrontLineWorkerDimensions.createOrUpdate(911234567890L, null, "operator", "circle", "name", Designation.AWW.name(), "status", UUID.randomUUID(), VerificationStatus.OTHER);
    }

    private RegistrationMeasure getRegistrationMeasure(FrontLineWorkerDimension frontLineWorkerDimension) {
        LocationDimension locationDimension = new LocationDimension("ZZZ999", "bihar", "Mandwa", "Algarh", "Gujarat", "VALID");
        allLocationDimensions.saveOrUpdate(locationDimension);
        TimeDimension timeDimension = new TimeDimension(DateTime.now());
        return new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "123222");
    }

}
