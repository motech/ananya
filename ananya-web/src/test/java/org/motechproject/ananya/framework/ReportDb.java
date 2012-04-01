package org.motechproject.ananya.framework;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Repository
public class ReportDb {

    @Autowired
    private AllLocationDimensions allLocationDimensions;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private AllRegistrationMeasures allRegistrationMeasures;
    @Autowired
    private DataAccessTemplate template;


    public ReportDb confirmFLWDimensionForPartiallyRegistered(String callerId, String operator) {

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));

        assertNotNull(frontLineWorkerDimension);
        assertThat(frontLineWorkerDimension.getOperator(), is(operator));
        assertFalse(RegistrationStatus.valueOf(frontLineWorkerDimension.getStatus()).isRegistered());

        return this;
    }

    public ReportDb confirmRegistrationMeasureForPartiallyRegistered(String callerId) {

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));

        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        assertTrue(registrationMeasure.getTimeDimension().matches(new DateTime()));

        LocationDimension locationDimension = registrationMeasure.getLocationDimension();
        assertTrue(locationDimension.getBlock().equals(FrontLineWorker.DEFAULT_LOCATION));
        assertTrue(locationDimension.getDistrict().equals(FrontLineWorker.DEFAULT_LOCATION));
        assertTrue(locationDimension.getPanchayat().equals(""));
        return this;
    }

    public void clearDimensionAndMeasures(String callerId) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        template.delete(registrationMeasure);
        template.delete(frontLineWorkerDimension);
    }
}
