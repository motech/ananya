package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.springframework.dao.DataIntegrityViolationException;

public class AllSMSSentMeasuresTest extends SpringIntegrationTest{

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertSMSSentMeasureWhenFLWDimensionIsNull() {
        template.save(new SMSSentMeasure(2, "23123123", false, null, new TimeDimension(DateTime.now())));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertSMSSentMeasureWhenTimeDimensionIsNull() {
        template.save(new SMSSentMeasure(2, "23123123", false, new FrontLineWorkerDimension(9876543210L, "", "", ""), null));
    }
}
