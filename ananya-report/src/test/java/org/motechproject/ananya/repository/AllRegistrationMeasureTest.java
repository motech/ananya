package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

public class AllRegistrationMeasureTest extends SpringIntegrationTest {


    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertRegistrationMeasureWhenFLWDimensionIsNull() {
        template.save(new RegistrationMeasure(null, new LocationDimension(), new TimeDimension()));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertRegistrationMeasureWhenLocationDimensionIsNull() {
        template.save(new RegistrationMeasure(new FrontLineWorkerDimension(), null , new TimeDimension()));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertRegistrationMeasureWhenTimeDimensionIsNull() {
        template.save(new RegistrationMeasure(new FrontLineWorkerDimension(), new LocationDimension(), null));
    }
}
